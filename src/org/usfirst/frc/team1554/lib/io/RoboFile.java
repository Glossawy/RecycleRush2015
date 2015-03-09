package org.usfirst.frc.team1554.lib.io;

import org.usfirst.frc.team1554.lib.common.ex.RobotIOException;
import org.usfirst.frc.team1554.lib.util.IOUtils;
import org.usfirst.frc.team1554.lib.util.Predicate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 3/7/2015 at 11:30 PM
 */
public class RoboFile {

    public static final RoboFile LVUSER = new RoboFile(Paths.get("home", "lvuser"));
    private static final FileAttribute<Set<PosixFilePermission>> STANDARD_ATTR;
    private static final Consumer<Path> DELETE_CONSUMER = (p) -> del(p);

    static {
        Set<PosixFilePermission> permits = PosixFilePermissions.fromString("rw-rw-rw-");
        STANDARD_ATTR = PosixFilePermissions.asFileAttribute(permits);
    }

    public static final RoboFile createUserFile(String name) {
        return LVUSER.child(name);
    }

    public static final String getStandardPermissions_POSIX() {
        return PosixFilePermissions.toString(STANDARD_ATTR.value());
    }

    public static final FileAttribute<Set<PosixFilePermission>> getAttributes_POSIX() {
        Set<PosixFilePermission> perms = EnumSet.copyOf(STANDARD_ATTR.value());
        return PosixFilePermissions.asFileAttribute(perms);
    }

    private final Path path;

    public RoboFile(File f) {
        this.path = Paths.get(f.toURI());
    }

    public RoboFile(Path path) {
        this.path = path;
    }

    public RoboFile(String path) {
        this.path = Paths.get(path);
    }

    public RoboFile absolute() {
        return new RoboFile(path.toAbsolutePath());
    }

    public String path() {
        return path.toString();
    }

    public String pathWithoutExtension() {
        String pathname = path.toString();

        int index = pathname.lastIndexOf('.');
        if (index == -1)
            return pathname;

        return pathname.substring(0, index);
    }

    public String name() {
        return path.getFileName().toString();
    }

    public String extension() {
        String filename = path.getFileName().toString();

        int index = filename.indexOf('.');
        if (index == -1)
            return "";

        return filename.substring(index);
    }

    public String nameWithoutExtension() {
        String filename = path.getFileName().toString();

        int index = filename.indexOf('.');
        if (index == -1)
            return filename;

        return filename.substring(0, index);
    }

    public Path representation() {
        return path;
    }

    public File file() {
        return path.toFile();
    }

    public RoboFile parent() {
        if (path.getNameCount() <= 1)
            throw new RobotIOException("Attempting to get parent of Root!");

        return new RoboFile(path.getParent());
    }

    public RoboFile sibling(String name) {
        if (path.getNameCount() <= 1)
            throw new RobotIOException("Attempt to get sibling of Root! This is not a thing...");

        return new RoboFile(path.resolveSibling(name));
    }

    public RoboFile child(String name) {
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS))
            if (Files.exists(path, LinkOption.NOFOLLOW_LINKS))
                throw new RobotIOException("Cannot get Child of a File! Must be a directory...");
            else
                mkdirs();

        return new RoboFile(path.resolve(name));
    }

    public boolean create() {
        try {
            Files.createDirectories(path.getParent(), STANDARD_ATTR);
            Files.createFile(path, STANDARD_ATTR);

            return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            throw new RobotIOException("Failed to create file : " + name(), e);
        }
    }

    public void mkdirs() {
        try {
            Files.createDirectories(path, STANDARD_ATTR);
        } catch (Exception e) {
            throw new RobotIOException("Failed to create directories", e);
        }
    }

    public boolean delete() throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            deleteDirectory(path);
            return !Files.exists(path, LinkOption.NOFOLLOW_LINKS);
        }

        return Files.deleteIfExists(path);
    }

    public boolean exists() {
        return Files.exists(path, LinkOption.NOFOLLOW_LINKS);
    }

    public InputStream read() {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Attempt to read Directory as File! This is not a possible operation...", e);

            throw new RobotIOException("Error opening InputStream to read from " + name(), e);
        }
    }

    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    public BufferedReader reader() {
        try {
            return Files.newBufferedReader(path);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Attempt to read Directory as File! This is not a possible operation...", e);

            throw new RobotIOException("Error opening Reader to read from " + name(), e);
        }
    }

    public BufferedReader reader(Charset charset) {
        try {
            return Files.newBufferedReader(path, charset);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Attempt to read Directory as File! This is not a possible operation...", e);

            throw new RobotIOException("Error opening Reader to read from " + name(), e);
        }
    }

    public BufferedReader reader(int bufferSize) {
        try {
            return new BufferedReader(new InputStreamReader(read()));
        } catch (RobotIOException e) {
            if (isDirectory())
                throw new RobotIOException("Attempt to read Directory as File! This is not a possible operation...", e.getCause());

            throw new RobotIOException("Error opening Reader to read from " + name(), e.getCause());
        }
    }

    public BufferedReader reader(int bufferSize, Charset cs) {
        try {
            return new BufferedReader(new InputStreamReader(read(), cs));
        } catch (RobotIOException e) {
            if (isDirectory())
                throw new RobotIOException("Attempt to read Directory as File! This is not a possible operation...", e.getCause());

            throw new RobotIOException("Error opening Reader to read from " + name(), e.getCause());
        }
    }

    public String readToString() throws IOException {
        return readToString(StandardCharsets.UTF_8);
    }

    public String readToString(Charset cs) throws IOException {
        return IOUtils.fileToString(file(), cs);
    }

    public byte[] readBytes() {
        byte[] arr;

        try (InputStream is = read(); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];

            for (int c = is.read(buffer); c != -1; c = is.read(buffer))
                os.write(buffer, 0, c);

            arr = os.toByteArray();
        } catch (IOException e) {
            throw new RobotIOException("Failure to read all bytes from " + name(), e);
        }

        return arr;
    }

    public OutputStream write(boolean append) {
        try {
            create();
            return Files.newOutputStream(path, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Cannot open OutputStream to directory...");
            throw new RobotIOException("Error writing to file: " + name(), e);
        }
    }

    public BufferedOutputStream write(boolean append, int bufferSize) {
        return new BufferedOutputStream(write(append), bufferSize);
    }

    public void write(InputStream input, boolean append) {
        try (OutputStream output = write(append)) {
            byte[] buf = new byte[4096];

            for (int c = input.read(buf); c != -1; c = input.read(buf))
                output.write(buf, 0, c);
        } catch (IOException e) {
            throw new RobotIOException("Error writing stream to file: " + name(), e);
        }
    }

    public BufferedWriter writer(boolean append) {
        try {
            create();
            Files.createDirectories(path.getParent(), STANDARD_ATTR);
            return Files.newBufferedWriter(path, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Cannot open Writer to directory...");
            throw new RobotIOException("Error writing to file: " + name(), e);
        }
    }

    public BufferedWriter writer(boolean append, Charset cs) {
        try {
            create();
            return Files.newBufferedWriter(path, cs, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            if (isDirectory())
                throw new RobotIOException("Cannot open Writer to directory...");
            throw new RobotIOException("Error writing to file: " + name(), e);
        }
    }

    public void writeString(String string, boolean append) {
        writeString(string, append, StandardCharsets.UTF_8);
    }

    public void writeString(String string, boolean append, Charset cs) {
        try (Writer writer = writer(append, cs)) {
            writer.write(string);
        } catch (IOException e) {
            throw new RobotIOException("Error writing to file: " + string + " written to " + name(), e);
        }
    }

    public void writeBytes(byte[] bytes, boolean append) {
        try (OutputStream output = write(append)) {
            output.write(bytes);
        } catch (IOException e) {
            throw new RobotIOException("Error writing bytes to file: " + name(), e);
        }
    }

    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        try (OutputStream output = write(append)) {
            output.write(bytes, offset, length);
        } catch (IOException e) {
            throw new RobotIOException("Error writing bytes to file: " + name(), e);
        }
    }

    public RoboFile[] list() {
        try {
            return RoboFile[].class.cast(Files.list(path).map((p) -> new RoboFile(p)).toArray());
        } catch (IOException e) {
            throw new RobotIOException("Failure to read directory! - " + name(), e);
        }
    }

    public RoboFile[] list(Predicate<RoboFile> filter) {
        try {
            return RoboFile[].class.cast(Files.list(path).map((p) -> new RoboFile(p)).filter((rf) -> filter.eval(rf)).toArray());
        } catch (IOException e) {
            throw new RobotIOException("Failure to read directory! - " + name(), e);
        }
    }

    public Stream<RoboFile> listStream() {
        try {
            return Files.list(path).map((p) -> new RoboFile(p));
        } catch (IOException e) {
            throw new RobotIOException("Failure to read directory! - " + name(), e);
        }
    }

    public Stream<RoboFile> listStream(Predicate<RoboFile> filter) {
        try {
            return Files.list(path).map((p) -> new RoboFile(p)).filter((rf) -> filter.eval(rf));
        } catch (IOException e) {
            throw new RobotIOException("Failure to read directory! - " + name(), e);
        }
    }

    public long length() {
        try {
            return Files.size(path);
        } catch (IOException e) {
        }

        return 0L;
    }

    public long lastModified() {
        try {
            Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
        }

        return 0L;
    }

    public boolean isDirectory() {
        return Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS);
    }

    @Override
    public String toString() {
        return path();
    }

    @Override
    public int hashCode() {
        return 31 + (path == null ? 0 : path.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;

        RoboFile other = (RoboFile) o;

        if (this.path == null) {
            if (other.path != null)
                return false;
        } else if (!this.path.equals(other.path))
            return false;

        return true;
    }

    public static void deleteDirectory(RoboFile file) throws IOException {
        if (!file.exists())
            throw new IOException(file.path() + " does not exist!");

        deleteDirectory(file.representation());
    }

    public static void emptyDirectory(RoboFile file) throws IOException {
        if (!file.exists())
            throw new IOException(file.path() + " does not exist!");

        emptyDirectory(file.representation());
    }

    private static void deleteDirectory(Path path) throws IOException {
        emptyDirectory(path);
        Files.delete(path);
    }

    private static void emptyDirectory(Path path) throws IOException {
        Files.list(path).forEach(DELETE_CONSUMER);
    }

    private static void del(Path p) {
        try {
            if (Files.isDirectory(p))
                deleteDirectory(p);
            else
                Files.deleteIfExists(p);
        } catch (IOException e) {
        }
    }

    public static void main(String[] args) {
        RoboFile path = new RoboFile("/user\\Matthew\\Crocco/home.txt");

        System.out.println(path.path());
    }

}
