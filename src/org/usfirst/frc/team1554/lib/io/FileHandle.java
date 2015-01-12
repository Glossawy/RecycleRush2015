package org.usfirst.frc.team1554.lib.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

import org.usfirst.frc.team1554.lib.util.IOUtils;

public class FileHandle {

	private final File file;

	public FileHandle(File file) {
		this.file = file;
	}

	public FileHandle(String path) {
		this(new File(path));
	}

	public String path() {
		return this.file.getPath().replace('\\', '/');
	}

	public String name() {
		return this.file.getName();
	}

	public String extension() {
		final String name = this.file.getName();
		final int i = name.lastIndexOf('.');
		if (i == -1) return "";

		return name.substring(i + 1);
	}

	public String nameWithoutExtension() {
		final String name = this.file.getName();
		final int i = name.lastIndexOf('.');
		if (i == -1) return name;

		return name.substring(0, i);
	}

	public String pathWithoutExtension() {
		final String path = path();
		final int i = path.lastIndexOf('.');
		if (i == -1) return path;

		return path.substring(0, i);
	}

	public File file() {
		return this.file;
	}

	public FileHandle parent() {
		if (path().length() == 0) throw new RuntimeException("Cannot get parent of root!");

		final File parent = this.file.getParentFile();
		return new FileHandle(parent == null ? new File("/") : parent);
	}

	public FileHandle sibling(String name) {
		if (path().length() == 0) throw new RuntimeException("Cannot get sibling of root!");

		return new FileHandle(new File(this.file.getParent(), name));
	}

	public FileHandle child(String name) {
		if (!this.file.isDirectory()) throw new RuntimeException("Cannot get child of a file! Must be a directory...");

		return new FileHandle(new File(this.file.getPath(), name));
	}

	public boolean isDirectory() {
		return this.file.isDirectory();
	}

	public void mkdirs() {
		this.file.mkdirs();
	}

	public boolean exists() {
		return this.file.exists();
	}

	public boolean delete() {
		return this.file.delete();
	}

	public boolean deleteDirectory() {
		if (!this.file.isDirectory()) throw new RuntimeException("File is not directory: " + this.file);

		return IOUtils.deleteDirectory(this.file);
	}

	public boolean emptyDirectory(boolean preserve) {
		if (!this.file.isDirectory()) throw new RuntimeException("File is not directory: " + this.file);

		return IOUtils.emptyDirectory(this.file, preserve);
	}

	public InputStream read() {
		try {
			return new FileInputStream(this.file);
		} catch (final Exception ex) {
			if (this.file.isDirectory()) throw new RuntimeException("Cannot open InputStream to directory...");
			throw new RuntimeException("Error reading file: " + this.file, ex);
		}
	}

	public BufferedInputStream read(int bufferSize) {
		return new BufferedInputStream(read(), bufferSize);
	}

	public Reader reader() {
		return new InputStreamReader(read());
	}

	public Reader reader(Charset charset) {
		return new InputStreamReader(read(), charset);
	}

	public Reader reader(String charset) {
		final InputStream is = read();

		try {
			return new InputStreamReader(is, IOUtils.charset(charset));
		} catch (final NoSuchElementException e) {
			IOUtils.closeSilently(is);
			throw new RuntimeException("Error reading file: " + this.file, e);
		}
	}

	public BufferedReader reader(int bufferSize) {
		return new BufferedReader(reader(), bufferSize);
	}

	public BufferedReader reader(int bufferSize, Charset cs) {
		return new BufferedReader(reader(cs), bufferSize);
	}

	public BufferedReader reader(int bufferSize, String cs) {
		final InputStream is = read();

		try {
			final InputStreamReader isr = new InputStreamReader(is, IOUtils.charset(cs));
			return new BufferedReader(isr, bufferSize);
		} catch (final NoSuchElementException e) {
			IOUtils.closeSilently(is);
			throw new RuntimeException("Error reading file: " + this.file, e);
		}
	}

	public String readString() throws IOException {
		return IOUtils.fileToString(this.file);
	}

	public String readString(Charset cs) throws IOException {
		return IOUtils.fileToString(this.file, cs);
	}

	public byte[] readBytes() {
		final int len = (int) length();
		final byte[] bytes = new byte[len != 0 ? len : 512];
		final InputStream is = read();

		try {
			int offset = 0;
			int nByte = 0;
			while ((offset < bytes.length) && ((nByte = is.read(bytes, offset, bytes.length - offset)) >= 0)) {
				offset += nByte;
			}

			if (offset < bytes.length) throw new IOException("Failed to read bytes fully. " + offset + " out of " + bytes.length);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to read bytes fully: " + this.file, e);
		} finally {
			IOUtils.closeSilently(is);
		}

		return bytes;
	}

	public OutputStream write(boolean append) {
		parent().mkdirs();
		try {
			return new FileOutputStream(this.file, append);
		} catch (final IOException ex) {
			if (this.file.isDirectory()) throw new RuntimeException("Cannot open OutputStream to directory...");
			throw new RuntimeException("Error writing file: " + this.file, ex);
		}
	}

	public BufferedOutputStream write(boolean append, int bufferSize) {
		return new BufferedOutputStream(write(append), bufferSize);
	}

	public void write(InputStream in, boolean append) {
		OutputStream out = null;
		try {
			final byte[] buf = new byte[4096];
			out = write(append);

			for (int i = in.read(buf); i != -1; i = in.read(buf)) {
				out.write(buf, 0, i);
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error writing stream to file: " + this.file, e);
		} finally {
			IOUtils.closeSilently(out);
			IOUtils.closeSilently(in);
		}
	}

	public Writer writer(boolean append) {
		return new OutputStreamWriter(write(append));
	}

	public Writer writer(boolean append, Charset cs) {
		parent().mkdirs();

		if (cs == null)
			return new OutputStreamWriter(write(append));
		else
			return new OutputStreamWriter(write(append), cs);
	}

	public void writeString(String string, boolean append) {
		writeString(string, append, null);
	}

	public void writeString(String string, boolean append, Charset cs) {
		Writer writer = null;

		try {
			writer = writer(append, cs);
			writer.write(string);
		} catch (final IOException e) {
			throw new RuntimeException("Error writing file: " + this.file, e);
		} finally {
			IOUtils.closeSilently(writer);
		}
	}

	public void writeBytes(byte[] bytes, boolean append) {
		final OutputStream out = write(append);
		try {
			out.write(bytes);
		} catch (final IOException e) {
			throw new RuntimeException("Error writing bytes to file: " + this.file, e);
		} finally {
			IOUtils.closeSilently(out);
		}
	}

	public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
		final OutputStream out = write(append);

		try {
			out.write(bytes, offset, length);
		} catch (final IOException e) {
			throw new RuntimeException("Error writing bytes to file: " + this.file, e);
		} finally {
			IOUtils.closeSilently(out);
		}
	}

	public FileHandle[] list() {
		final String[] relPaths = this.file.list();
		final FileHandle[] handles = new FileHandle[relPaths.length];

		for (int i = 0; i < handles.length; i++) {
			handles[i] = child(relPaths[i]);
		}

		return handles;
	}

	public FileHandle[] list(FileFilter filter) {
		final String[] relPaths = this.file.list();
		FileHandle[] handles = new FileHandle[relPaths.length];
		int count = 0;

		for (int i = 0; i < relPaths.length; i++) {
			final FileHandle handle = child(relPaths[i]);
			if (!filter.accept(handle.file)) {
				continue;
			}

			handles[count++] = handle;
		}

		if (count < relPaths.length) {
			final FileHandle[] tmpHandles = new FileHandle[count];
			System.arraycopy(handles, 0, tmpHandles, 0, count);
			handles = tmpHandles;
		}

		return handles;
	}

	public FileHandle[] list(FilenameFilter filter) {
		final String[] relPaths = this.file.list();
		FileHandle[] handles = new FileHandle[relPaths.length];
		int count = 0;

		for (int i = 0; i < relPaths.length; i++) {
			final FileHandle handle = child(relPaths[i]);
			if (!filter.accept(this.file, relPaths[i])) {
				continue;
			}

			handles[count++] = handle;
		}

		if (count < relPaths.length) {
			final FileHandle[] tmpHandles = new FileHandle[count];
			System.arraycopy(handles, 0, tmpHandles, 0, count);
			handles = tmpHandles;
		}

		return handles;
	}

	public long length() {
		return this.file.length();
	}

	public long lastModified() {
		return this.file.lastModified();
	}

	@Override
	public String toString() {
		return path();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.file == null) ? 0 : this.file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final FileHandle other = (FileHandle) obj;
		if (this.file == null) {
			if (other.file != null) return false;
		} else if (!this.file.equals(other.file)) return false;
		return true;
	}

}
