package org.usfirst.frc.team1554.lib.natives;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Set;

import org.usfirst.frc.team1554.lib.collect.Sets;
import org.usfirst.frc.team1554.lib.util.OS;

/**
 * A Centralized Natives Loading class. Offers rudimentary handler methods for
 * loading in-jar Natives as well as any Library File InputStreams. Uses Unique
 * Identifiers (Basic, Undecorated, libnames) to keep track of libraries that have
 * been loaded.
 * 
 * @author Matthew
 *
 */
public final class Native {

	private Native() {
	}

	private static Set<String> loadedLibraries = Sets.newHashSet();

	static {
		try {
			// Natives to be loaded by default
			loadLibrary("robolibJNI");
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static synchronized boolean isLibraryLoaded(String libname) {
		return loadedLibraries.contains(libname);
	}

	/**
	 * Takes the libname as an identifier and a File Stream to load a library. The
	 * libname is simply an identifier since the InputStream is already being
	 * provided. It does not need the addiiton decorations and so
	 * 'librobolibJNI64.so' can be identified in this method as 'robolibJNI'. <br />
	 * <br />
	 * If the Library Identifier is detected as already loaded then this method does
	 * not attempt to load again and simply returns true.
	 * 
	 * @param libname
	 * @param libStream
	 * @return
	 * @throws IOException
	 */
	public static synchronized boolean loadLibrary(String identifier, File libStore) throws IOException {
		if (loadedLibraries.contains(identifier)) return true;

		final String libname = libStore.getName();
		final String name = libname.substring(0, libname.lastIndexOf('.'));
		final String suffix = libname.substring(libname.lastIndexOf('.'));
		final File libFile = File.createTempFile(name, suffix);
		libFile.deleteOnExit();

		final byte[] buf = new byte[8192];
		try (OutputStream os = new FileOutputStream(libFile); InputStream is = new FileInputStream(libStore)) {
			for (int c = is.read(buf); c != -1; c = is.read(buf)) {
				os.write(buf, 0, c);
			}
		} catch (final IOException e) {
			throw e;
		}

		System.load(libFile.getAbsolutePath());
		return loadedLibraries.add(identifier);
	}

	/**
	 * Loads a Library from the RoboLib specific Resource Location and determines
	 * name decoration. <br />
	 * <br />
	 * If the libname is librobolibJNI64.so then all you need to put is 'robolibJNI'
	 * and the rest will be determined. This is designed specifically for the syntax
	 * gnerated by the RoboNatives-Builder application which uses JNIGen by BadLogic.
	 * 
	 * @param libname
	 * @return
	 * @throws IOException
	 */
	public static synchronized boolean loadLibrary(String libname) throws IOException {
		final OS sys = OS.get();
		final OS.Bit bits = OS.getArchJVM();
		String filename = libname;

		if (sys == OS.WINDOWS)
			if (bits == OS.Bit.BIT_32) {
				filename += ".dll";
			} else {
				filename += "64.dll";
			}
		else {
			filename = "lib" + libname;
			if (bits == OS.Bit.BIT_32) {
				filename += ".so";
			} else {
				filename += "64.so";
			}
		}

		try {
			return loadLibrary(libname, new File(Native.class.getResource(filename).toURI()));
		} catch (final URISyntaxException e) {
			if(bits == OS.Bit.BIT_64)
				return loadLibrary_32(libname);
			throw new IOException(e);
		}
	}
	
	// Attempts to find Library File as 32-bit Library if 64-bit library not found.
	// This is likely to fail even on 64-bit systems. But we might as well try.
	private static synchronized boolean loadLibrary_32(String libname) throws IOException {
		final OS sys = OS.get();
		String filename = libname;

		if (sys == OS.WINDOWS)
			filename += ".dll";
		else {
			filename = "lib" + libname + ".so";
		}
		
		try {
			return loadLibrary(libname, new File(Native.class.getResource(filename).toURI()));
		} catch (final URISyntaxException e) {
			throw new IOException(e);
		}
	}

}
