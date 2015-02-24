package org.usfirst.frc.team1554.lib.util;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team1554.lib.collect.Lists;
import org.usfirst.frc.team1554.lib.collect.Maps;
import org.usfirst.frc.team1554.lib.collect.ObjectMap;
import org.usfirst.frc.team1554.lib.io.LineProcessor;
import org.usfirst.frc.team1554.lib.io.SingleStringProcessor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * A Collection of Utilities dedicated to the try-catch mess that is java.io. Some java.nio utilities are provided. <br />
 * <br />
 * Most of these methods are general use.
 *
 * @author Matthew
 */
public class IOUtils {

    /**
     * Number of Seconds between Log Messages relating to Download Rate and State.
     */
    public static final int DOWNLOAD_LOG_DELAY = 5;

    /**
     * Default Buffer Size for any IOUtils created Byte[] or Char[] buffers. <br />
     * By default this is 4096. Java's Default is 8192.
     *
     * @see java.io.BufferedReader
     */
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    private static final ObjectMap<String, Charset> charsetMap = Maps.newObjectMap();

    static {
        for (final Entry<String, Charset> cEntry : Charset.availableCharsets().entrySet()) {
            final String canonicalKey = cEntry.getKey();
            final String displayKey = cEntry.getValue().displayName();
            final Charset cs = cEntry.getValue();

            charsetMap.put(canonicalKey, cs);
            charsetMap.put(displayKey, cs);
            System.out.println("Mapped Charset: " + canonicalKey + " & " + displayKey + " => " + cs);
        }
    }

    /**
     * Send Properties to a File from a Properties object
     *
     * @param from
     * @param to
     * @param comments
     */
    public static final void saveProperties(Properties from, File to, String comments) {
        Preconditions.checkNotNull(from, "Properties is Null!");
        Preconditions.checkNotNull(to, "File to Save to is Null!");

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(to, false);
            from.store(fos, comments);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fos);
        }
    }

    /**
     * Load Properties from a File into a Properties object.
     *
     * @param to
     * @param from
     */
    public static final void loadProperties(Properties to, File from) {
        Preconditions.checkNotNull(to, "Properties is Null!");
        Preconditions.checkNotNull(from, "File to Load From is Null!");

        FileInputStream fis = null;

        try {
            fis = new FileInputStream(from);
            to.load(fis);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fis);
        }
    }

    /**
     * Loads Properties from a Properties File and stores them in a Properties object <br />
     * which is then returned.
     *
     * @param file
     * @return Properties Object
     */
    public static final Properties getPropertiesFromFile(File file) {
        Preconditions.checkNotNull(file, "File Can't Be Null!");
        Preconditions.checkExpression(file.exists(), "File Does Not Exist! - " + file.getPath());

        Properties properties = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properties = new Properties();
            properties.load(fis);
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            closeSilently(fis);
        }

        return properties;
    }

    /**
     * Write a String to a File.
     *
     * @param file
     * @param string
     */
    public static final void write(File file, String string) {
        try {
            final FileWriter writer = new FileWriter(file);
            final StringReader reader = new StringReader(string);
            write(reader, writer, true);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    // -- START READER->WRITER METHODS --//

    /**
     * Writer data from Reader to Writer. Do Not Close Streams when Done!<br />
     * <br />
     * <b><i><u>WILL NOT CLOSE STREAMS BY DEFAULT!</u></i></b>
     *
     * @param reader
     * @param writer
     */
    public static final void write(Reader reader, Writer writer) {
        write(reader, writer, false);
    }

    /**
     * Write data from Reader to Writer.
     *
     * @param reader
     * @param writer
     * @param closeStreams - Close Streams when Finished?
     */
    public static final void write(Reader reader, Writer writer, boolean closeStreams) {
        write(reader, closeStreams, writer, closeStreams);
    }

    /**
     * Completely write all data from a Reader object into the given Writer object. <br />
     * Can close writer and reader to help clean up try-catch blocks.
     *
     * @param reader      - Reader Object to Read from
     * @param closeReader - Close Reader when done?
     * @param writer      - Writer Object to Write to
     * @param closeWriter - Close Writer when done?
     */
    public static final void write(Reader reader, boolean closeReader, Writer writer, boolean closeWriter) {
        if (closeReader && closeWriter) {
            write(reader, writer, true);
        } else {
            try {
                doWrite(reader, writer);
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (closeReader) {
                    closeSilently(reader);
                }
                if (closeWriter) {
                    closeSilently(writer);
                }
            }
        }
    }

    // -- END READER->WRITER METHODS -- //

    // -- START InputStream->OutputStream METHODS -- //

    /**
     * Writer data from Reader to Writer. Do Not Close Streams when Done!<br />
     * <br />
     * <b><i><u>WILL NOT CLOSE STREAMS BY DEFAULT!</u></i></b>
     *
     * @param input
     * @param output
     */
    public static final void write(InputStream input, OutputStream output) {
        write(input, output, false);
    }

    /**
     * Write data from Reader to Writer.
     *
     * @param input
     * @param output
     * @param closeStreams - Close Streams when Finished?
     */
    public static final void write(InputStream input, OutputStream output, boolean closeStreams) {
        write(input, closeStreams, output, closeStreams);
    }

    /**
     * Completely write all data from an InputStream into the given OutputStream. <br />
     * Can close writer and reader to help clean up try-catch blocks.
     *
     * @param input             - Input
     * @param closeInputStream  - Close InputStream when done?
     * @param output            - Output
     * @param closeOutputStream - Close OutputStream when done?
     */
    public static final void write(InputStream input, boolean closeInputStream, OutputStream output, boolean closeOutputStream) {
        if (closeInputStream && closeOutputStream) {
            write(input, output, true);
        } else {
            try {
                doStreamWrite(input, output);
            } catch (final IOException e) {
                e.printStackTrace();
            } finally {
                if (closeInputStream) {
                    closeSilently(input);
                }
                if (closeOutputStream) {
                    closeSilently(output);
                }
            }
        }
    }

    // -- END InputStream->OutputStream METHODS -- //

    // Handles Simply Moving Data from Reader to Writer using a Char Buffer
    private static final void doWrite(Reader reader, Writer writer) throws IOException {
        final char[] cbuf = new char[DEFAULT_BUFFER_SIZE];
        for (int c = reader.read(cbuf); c != -1; c = reader.read(cbuf)) {
            writer.write(cbuf, 0, c);
        }
    }

    // Handles Simply Moving Data from InputStream to OutputStream using a Byte
    // Buffer
    private static final void doStreamWrite(InputStream is, OutputStream os) throws IOException {
        final byte[] b = new byte[DEFAULT_BUFFER_SIZE];
        for (int c = is.read(b); c != -1; c = is.read(b)) {
            os.write(b, 0, c);
        }
    }

    /**
     * Split a String into a String Iterable made up of it's individual lines.
     *
     * @param source
     * @return
     */
    public static final Iterable<String> getLines(String source) {
        return Lists.newArrayList(source.split("\r?\n"));
    }

    /**
     * Read in the entirety of a file and return an Iterable made up of all of <br />
     * the individual lines.
     *
     * @param source
     * @return
     */
    public static final Iterable<String> getLines(File source, Charset cs) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        String result = null;

        try {
            final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
            fis = new FileInputStream(source);
            bos = new ByteArrayOutputStream();

            for (int c = fis.read(buf); c != -1; c = fis.read(buf)) {
                bos.write(buf, 0, c);
            }

            result = bos.toString(cs.displayName());
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeSilently(fis);
            IOUtils.closeSilently(bos);
        }

        if (result == null) throw new IllegalStateException("Failed to Parse Lines of File!");

        return getLines(result);
    }

    /**
     * Convert File's contents to an {@link Iterable} of Strings.
     *
     * @param source
     * @return
     */
    public static final Iterable<String> getLines(File source) {
        return getLines(source, Charset.defaultCharset());
    }

    /**
     * Converts a File's contents to some object represented by T. This is done by the appropriate {@link LineProcessor}. This method uses the given {@link Charset} to read the File's InputStream. The lines are then separated and passed to the LineProcessor one-by-one. At the end a result is returned by the processor via {@link LineProcessor#result()}.
     *
     * @param file      - File to read
     * @param cs        - Charset to use for reading
     * @param processor - Processor to process lines one-by-one
     * @return
     * @throws IOException - Required by {@link LineProcessor#processLine(String) processLine}.
     */
    public static final <T> T readLines(File file, Charset cs, LineProcessor<T> processor) throws IOException {
        final Iterator<String> lines = getLines(file, cs).iterator();
        boolean process = true;

        while (lines.hasNext() && process) {
            process = processor.processLine(lines.next());
        }

        return processor.result();
    }

    /**
     * Converts a File's Contents to some object represented by T. This is done by the appropriate {@link LineProcessor}. This method will use {@link Charset#defaultCharset() the default charset} and delegate to {@link #readLines(File, Charset, LineProcessor) readLines}.
     *
     * @param file
     * @param processor
     * @return
     * @throws IOException
     */
    public static final <T> T readLines(File file, LineProcessor<T> processor) throws IOException {
        return readLines(file, Charset.defaultCharset(), processor);
    }

    /**
     * Recursively Empties then Deletes a Folder. <br />
     * <br />
     * The boolean this method returns depends on whether or not the Directory passed is deleted. <br />
     * If the directory can not be deleted then it returns false, though some of its contents may have been deleted anyway. <br />
     *
     * @param directory
     * @return Whether or Not the Directory passed was Deleted
     */
    public static final boolean deleteDirectory(File directory) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is NOT a Directory!");

        final File[] entries = directory.listFiles();

        for (final File file : entries) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }

        return directory.delete();
    }

    public static final boolean emptyDirectory(File directory, boolean preserve) {
        if (!directory.isDirectory())
            throw new IllegalArgumentException(directory.getAbsolutePath() + " is NOT a Directory!");

        final File[] entries = directory.listFiles();

        for (final File file : entries) {
            if (file.isDirectory()) {
                if (preserve) {
                    emptyDirectory(file, preserve);
                } else {
                    deleteDirectory(file);
                }
            } else {
                file.delete();
            }
        }

        return true;
    }

    /**
     * Copy Src File to Dest, if Dest Exists it will be Overwritten
     *
     * @param src
     * @param dest
     * @return Success/Failure
     */
    public static final boolean copyFile(File src, File dest) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean success = true;

        if (!src.exists()) throw new IllegalStateException("Source File does Not Exist!");
        if (!dest.exists()) {
            try {
                dest.getParentFile().mkdirs(); // Ensure Trunk of Path Exists
                dest.createNewFile(); // Create File
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        try {
            final byte[] buffer = new byte[8192];
            fis = new FileInputStream(src);
            fos = new FileOutputStream(dest);

            for (int c = fis.read(buffer); c > 0; c = fis.read(buffer)) {
                fos.write(buffer, 0, c);
            }
        } catch (final IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            closeSilently(fis);
            closeSilently(fos);
        }

        return success;
    }

    public static final String getSuffix(File file) {
        final String name = file.getName();

        if (!name.contains("."))
            return "";
        else
            return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Closes any Closeable, meant for Streams to clean up finally blocks.
     *
     * @param stream
     */
    public static final void closeSilently(Closeable stream) {
        if (stream == null) return;

        try {
            stream.close();
        } catch (final IOException e) {
        }
    }

    public static File copyTo(File f, File archivesDir) throws IOException {
        final File target = new File(archivesDir, f.getName());

        IOUtils.copyFile(f, target);
        return target;
    }

    /**
     * Small Utility to consume the InterruptedException associated with Thread.sleep. <br />
     * <br />
     * Deprecated for FRC use. Please use {@link Timer#delay(double) Timer.delay}.
     *
     * @param l
     */
    @Deprecated
    public static void sleep(long ms) {
        // try {
        // Thread.sleep(l);
        // } catch (final InterruptedException e) {
        // }
        Timer.delay(ms / 1000.0d);
    }

    /**
     * Given a StackTrace, print out similarly to {@link Exception#printStackTrace()} .
     *
     * @param stackTrace
     */
    public static void printStackTrace(StackTraceElement[] stackTrace) {
        for (int i = 2; i < stackTrace.length; i++) {
            System.err.println(stackTrace[i]);
        }
    }

    /**
     * Converts a File's contrents into a singular String using the given {@link Charset} via the use of {@link SingleStringProcessor} and delegates to {@link #readLines(File, Charset, LineProcessor) readLines}.
     *
     * @param file
     * @param charset
     * @return
     * @throws IOException
     */
    public static String fileToString(File file, Charset charset) throws IOException {
        return IOUtils.readLines(file, charset, new SingleStringProcessor());
    }

    /**
     * Converts a File's contents into a singular String using {@link Charset#defaultCharset() the default charset} by default. This delegates to {@link IOUtils#readLines(File, Charset, LineProcessor) readLines} .
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String fileToString(File file) throws IOException {
        return fileToString(file, Charset.defaultCharset());
    }

    /**
     * Attempts to establish a connection to the given URL and listens for a success code (HTTP Response Code 200). If a response of 200 is received, then it is a success. Any other code is a failure.
     *
     * @param url
     * @return true if Response Code == 200, else false
     * @throws IOException if a connection can not be established
     */
    public static boolean checkConnectionSuccess(URL url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");

        final int responseCode = conn.getResponseCode();
        conn.disconnect();

        return responseCode == 200;
    }

    public static boolean checkConnectionSuccess(String url) throws IOException {
        return checkConnectionSuccess(new URL(url));
    }

    /**
     * Returns the canonical name of the Charset. <br />
     * <br />
     * Delegates to {@link Charset#name()}.
     *
     * @param cs
     * @return
     */
    public static String charset(Charset cs) {
        return cs.name();
    }

    /**
     * Determines a Charset from either it's Canonical Name or Display Name saved to a lookup table in IOUtils. An exception is thrown if none is found.
     *
     * @param name
     * @return
     * @throws NoSuchElementException No Charset is tied to the given name.
     */
    public static Charset charset(String name) throws NoSuchElementException {
        final Charset cs = charsetMap.get(name);

        if (cs != null)
            return cs;
        else
            throw new NoSuchElementException("Charset '" + name + "' not found!");
    }
}
