package org.usfirst.frc.team1554.lib.installer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.usfirst.frc.team1554.lib.installer.exceptions.IORuntimeException;
import org.usfirst.frc.team1554.lib.installer.exceptions.MissingRequirementException;
import org.usfirst.frc.team1554.lib.installer.exceptions.RuntimeParsingException;
import org.usfirst.frc.team1554.lib.io.RoboFile;
import org.usfirst.frc.team1554.lib.meta.LibVersion;
import org.usfirst.frc.team1554.lib.util.OS;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Properties;

/**
 * Primary Class for Library Installation (handles .classpath and build.properties editing)
 *
 * @author Matthew
 */
public final class LibInstaller extends Application {

    private static final String LIB_FILENAME = LibVersion.NAME.toLowerCase() + "-" + LibVersion.VERSION + ".jar";

    private ProgressDisplay progDisplay;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Confirm Installation
        displayAwarenessMessage(primaryStage);
        if (!FXOptionPane.showConfirmation(primaryStage, "Proceed with Installation?", "Install " + LibVersion.NAME + " v" + LibVersion.VERSION + "?", FXOptionPane.OptionType.YES_NO, FXOptionPane.IconType.CONFIRM)) {
            primaryStage.close();
            return;
        }

        // Set up Progress Display
        progDisplay = new ProgressDisplay(500, 50);
        primaryStage.setScene(progDisplay);
        primaryStage.setTitle(LibVersion.NAME + " v" + LibVersion.VERSION + " Installation");
        primaryStage.centerOnScreen();
        primaryStage.show();

        try {
            // Locate Project Directory and Library Jar File
            // Through the power of Java NIO!
            progDisplay.setInfoText("Locating Project Directory and Library File...");
            Path projectDir = getProjectDirectory(primaryStage);
            Path libFile = locateLibFile(projectDir);
            Path srcFile = libFile.resolveSibling(LibVersion.NAME.toLowerCase() + "-" + LibVersion.VERSION + "-sources.jar");

            progDisplay.setProgress(10);
            progDisplay.setInfoText("Retrieving Necessary .classpath and .properties files...");
            Path classpathFile = projectDir.resolve(".classpath");
            Path wpilibProperties = getWPIProperties(projectDir);

            if (!Files.exists(wpilibProperties, LinkOption.NOFOLLOW_LINKS))
                throw new MissingRequirementException("No WPILib build.properties Found! Tried: " + wpilibProperties.toString());

            progDisplay.setProgress(20);
            progDisplay.setInfoText("Adding " + LibVersion.NAME + " to WPILib classpath variable...");
            // Set Classpath Property in wpilib build.properties
            modifyClasspathWPI(wpilibProperties, projectDir.relativize(libFile.toAbsolutePath()));

            try {
                // Inject Library as a dependency in .classpath
                // <classpathentry kind="lib" path="<libpath>" sourcepath="<libsrcpath>" />
                progDisplay.setProgress(50);
                progDisplay.setInfoText("Generating new .classpath XML Data...");
                injectDependencyEclipseClasspathXML(libFile, srcFile, classpathFile);
            } catch (ParserConfigurationException | SAXException e) {
                throw new RuntimeParsingException("Failed to Parse .classpath XML!", e);
            } catch (TransformerException e) {
                throw new IORuntimeException("Failed to transform and write out new XML Graph to .classpath!", e);
            }

            progDisplay.setProgress(100);
            progDisplay.setInfoText("Finished!");
            FXOptionPane.showMessage("Finished!");
        } catch (Exception e) {
            progDisplay.setInfoText("ERROR -- " + e.getMessage());
            FXOptionPane.showBlockingMessage(primaryStage, "Application has Errored!", assembleErrorMessage(e), FXOptionPane.IconType.ERROR);
            createErrorFile(e);
        }

        primaryStage.close();
    }

    Path getWPIProperties(Path project) throws IOException {
        Path buildFile = project.resolve("build.xml");
        String wpiVersion = "current";
        String wpiPath = "/wpilib/java/${version}/ant/build.properties";

        if (!Files.exists(buildFile, LinkOption.NOFOLLOW_LINKS))
            throw new MissingRequirementException("No build.xml found in project!");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            Document xml = factory.newDocumentBuilder().parse(buildFile.toFile());
            NodeList nodes = xml.getElementsByTagName("property");

            boolean versionRetrieved = false;
            boolean pathRetrieved = false;
            for (int i = 0; i < nodes.getLength() && (!versionRetrieved || !pathRetrieved); i++) {
                Node cur = nodes.item(i);
                NamedNodeMap map = cur.getAttributes();

                Node fileNode = map.getNamedItem("file");

                if (fileNode.getNodeValue().endsWith("wpilib.properties")) {
                    int index = fileNode.getNodeValue().indexOf('/');
                    Path toVersionProps = Paths.get(System.getProperty("user.home"), fileNode.getNodeValue().substring(index));

                    Properties props = new Properties();
                    props.load(Files.newInputStream(toVersionProps));
                    wpiVersion = props.getProperty("version");
                    versionRetrieved = true;
                } else if (fileNode.getNodeValue().endsWith("build.properties") && fileNode.getNodeValue().contains("/wpilib/")) {
                    int index = fileNode.getNodeValue().indexOf('/');
                    wpiPath = fileNode.getNodeValue().substring(index);
                    pathRetrieved = true;
                }
            }

            return Paths.get(System.getProperty("user.home"), wpiPath.replaceAll("\\$\\{version\\}", wpiVersion));
        } catch (SAXException | ParserConfigurationException e) {
            throw new IORuntimeException("Failed to Parse WPILib build.xml! Is it still ANT?", e);
        }
    }

    void modifyClasspathWPI(Path wpilibProperties, Path libRelative) throws IOException {
        Properties wpiProps = new Properties();
        wpiProps.load(Files.newInputStream(wpilibProperties));
        String entry = wpiProps.getProperty("classpath");

        if (entry.indexOf(LibVersion.NAME.toLowerCase()) == -1) {
            wpiProps.setProperty("classpath", entry + ":" + libRelative);
            wpiProps.store(Files.newOutputStream(wpilibProperties), "Deployment Information -- Re-written by " + LibVersion.NAME + " v" + LibVersion.VERSION);
        }
    }

    void injectDependencyEclipseClasspathXML(Path libFile, Path srcFile, Path classpathFile) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        Document classpathXml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(classpathFile.toFile());
        Element libElement = createLibraryElement(classpathXml, libFile, srcFile);

        progDisplay.setProgress(70);
        NodeList libNodes = classpathXml.getElementsByTagName("classpathentry");
        DOMSearchResult<Node> result = searchForLastElementOfKind(libNodes, "var");

        progDisplay.setProgress(90);
        progDisplay.setInfoText("Adding " + LibVersion.NAME + " as dependency and writing XML...");
        if (!result.roboLibFound) {
            libNodes.item(0).getParentNode().insertBefore(libElement, result.value.getNextSibling());

            Transformer transform = TransformerFactory.newInstance().newTransformer();
            transform.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(classpathXml);
            StreamResult res = new StreamResult(Files.newOutputStream(classpathFile));
            transform.transform(source, res);
        }
    }

    Element createLibraryElement(Document classpathXml, Path libFile, Path srcFile) {
        Element libElement = classpathXml.createElement("classpathentry");
        libElement.setAttribute("kind", "lib");
        libElement.setAttribute("path", libFile.toAbsolutePath().toString());
        if (Files.exists(srcFile, LinkOption.NOFOLLOW_LINKS))
            libElement.setAttribute("sourcepath", srcFile.toAbsolutePath().toString());

        return libElement;
    }

    DOMSearchResult<Node> searchForLastElementOfKind(NodeList list, String kind) {
        Node last = null;
        boolean found = false;

        for (int i = 0; i < list.getLength(); i++) {
            Node cur = list.item(i);
            if (cur.getAttributes().getNamedItem("kind").getNodeValue().equals(kind))
                last = cur;
            if (cur.getAttributes().getNamedItem("path").getNodeValue().indexOf(LibVersion.NAME.toLowerCase()) != -1)
                found = true;
        }

        return new DOMSearchResult<>(last, found);
    }

    Path getProjectDirectory(Stage primaryStage) {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setTitle(GUIRef.TITLE + " - Please Select Eclipse Project Directory");
        chooser.setInitialDirectory(new File("."));

        File f = chooser.showDialog(primaryStage);

        if (f == null) {
            boolean cancel = FXOptionPane.showConfirmation(primaryStage, "Confirmation", "Cancel Installation?", FXOptionPane.OptionType.YES_NO, FXOptionPane.IconType.CONFIRM);

            if (cancel) {
                primaryStage.close();
                System.exit(0);
            } else
                return getProjectDirectory(primaryStage);
        }
        Path dir = Paths.get(f.toURI());
        if (!isEclipseProject(dir))
            throw new MissingRequirementException(String.valueOf(dir) + " is not an eclipse project! No .classpath found!");

        return dir;
    }

    boolean isEclipseProject(Path dir) {
        return Files.exists(dir.resolve(".classpath"), LinkOption.NOFOLLOW_LINKS);
    }

    Path locateLibFile(Path root) {
        final ObjectProperty<Path> lib = new SimpleObjectProperty<>();
        try {
            Files.walkFileTree(root, EnumSet.noneOf(FileVisitOption.class), 10, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().equals(LIB_FILENAME)) {
                        lib.set(file);
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    if (file.getFileName().equals(LIB_FILENAME)) {
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new IORuntimeException("Error Walking File Tree while looking for " + LIB_FILENAME + "!", e);
        }

        if (lib.get() == null)
            throw new MissingRequirementException("Could not locate " + LIB_FILENAME + " in the Project Directory or it's subfolders.");

        return lib.get();
    }

    public static void startInstallation(String[] args) throws IllegalAccessException {
        try {
            // Access Check
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            boolean found = false;
            for (int i = 0; i < stackTrace.length && !found; i++) {
                if (stackTrace[i].getClassName().equals(LibInstallerLauncher.class.getName()))
                    found = true;
            }

            if (!found)
                throw null;
        } catch (Exception e) {
            throw new IllegalAccessException("LibInstaller must be checked through LibInstallerLauncher!");
        }

        Application.launch(args);
        Platform.runLater(() -> Application.setUserAgentStylesheet(Application.STYLESHEET_CASPIAN));
    }

    private static String assembleErrorMessage(Exception t) {
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StringBuilder message = new StringBuilder();
        final String separator = "===\n";
        final Throwable cause = t.getCause();

        message.append("Exception of type ").append(t.getClass().getName()).append('\n');
        message.append("Message: ").append(t.getMessage()).append('\n');
        message.append(separator);
        message.append("   ").append(stackTrace[0]).append('\n');

        for (int i = 1; i < stackTrace.length; i++) {
            message.append(" \t").append(stackTrace[i]).append('\n');
        }

        if (cause != null) {
            final StackTraceElement[] causeTrace = cause.getStackTrace();
            message.append(" \t\t").append("Caused by ").append(cause.getClass().getName()).append('\n');
            message.append(" \t\t").append("Because: ").append(cause.getMessage()).append('\n');
            message.append(" \t\t   ").append(causeTrace[0]).append('\n');
            message.append(" \t\t \t").append(causeTrace[2]).append('\n');
            message.append(" \t\t \t").append(causeTrace[3]);
        }

        return message.toString();
    }

    private static void createErrorFile(Exception e) {
        Path path = Paths.get("robolib-install-error.log");

        try {
            if (!Files.exists(path)) {
                if (OS.get() == OS.UNIX)
                    Files.createFile(path, RoboFile.getAttributes_POSIX());
                else
                    Files.createFile(path);
            }

            e.printStackTrace(new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)));
        } catch (Exception ignore) {
        }

        e.printStackTrace();
    }

    static void displayAwarenessMessage(Stage primary) {
        FXOptionPane.showBlockingMessage(primary, "Notes",
                "Be aware that the following requirements must be met:\n" +
                        "\t 1. The Library file is either in the project directory or in some sub-directory\t\n" +
                        "\t 2. There is a build.xml in the project directory that was auto-generated\t\n" +
                        "\t 3. There is a wpilib folder in your User Home Directory (" + System.getProperty("user.home") + ")\t\n",
                FXOptionPane.IconType.WARNING);
    }

    static class DOMSearchResult<T extends Node> {
        public final T value;
        public final boolean roboLibFound;

        public DOMSearchResult(T val, boolean roboLibFound) {
            this.value = val;
            this.roboLibFound = roboLibFound;
        }
    }
}
