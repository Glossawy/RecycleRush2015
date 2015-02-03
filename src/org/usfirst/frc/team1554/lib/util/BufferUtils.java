package org.usfirst.frc.team1554.lib.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.usfirst.frc.team1554.lib.collect.Array;

/**
 * Small Handler Class for quickening up Buffer usage. All ByteOrder's are
 * ByteOrder.LITTLE_ENDIAN since the RoboRIO uses a Little Endian architecture.
 * 
 * @author Matthew
 *
 */
public final class BufferUtils {

	private static final Array<ByteBuffer> unsafeBuffers = new Array<ByteBuffer>();
	private static int unsafeAllocated = 0;

	/**
	 * Copies numFloats floats from src starting at offset to dst. Dst is assumed to
	 * be a direct {@link Buffer}. The method will crash if that is not the case. The
	 * position and limit of the buffer are ignored, the copy is placed at position 0
	 * in the buffer. After the copying process the position of the buffer is set to
	 * 0 and its limit is set to numFloats * 4 if it is a ByteBuffer and numFloats if
	 * it is a FloatBuffer. In case the Buffer is neither a ByteBuffer nor a
	 * FloatBuffer the limit is not set. This is an expert method, use at your own
	 * risk.
	 * 
	 * @param src
	 *            the source array
	 * @param dst
	 *            the destination buffer, has to be a direct Buffer
	 * @param numFloats
	 *            the number of floats to copy
	 * @param offset
	 *            the offset in src to start copying from
	 */
	public static void copy(float[] src, Buffer dst, int floatCount, int offset) {
		copyJNI(src, dst, floatCount, offset);
		dst.position(0);

		if (dst instanceof ByteBuffer) {
			dst.limit(floatCount << 2);
		} else if (dst instanceof FloatBuffer) {
			dst.limit(floatCount);
		}
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(byte[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset, dst, positionInBytes(dst), numElements);
		dst.limit(dst.position() + bytesToElements(dst, numElements));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(short[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 1, dst, positionInBytes(dst), numElements << 1);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position and limit
	 * will stay the same. <b>The Buffer must be a direct Buffer with native byte
	 * order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param numElements
	 *            the number of elements to copy.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 */
	public static void copy(char[] src, int srcOffset, int numElements, Buffer dst) {
		copyJNI(src, srcOffset << 1, dst, positionInBytes(dst), numElements << 1);
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position and limit
	 * will stay the same. <b>The Buffer must be a direct Buffer with native byte
	 * order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param numElements
	 *            the number of elements to copy.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 */
	public static void copy(int[] src, int srcOffset, int numElements, Buffer dst) {
		copyJNI(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position and limit
	 * will stay the same. <b>The Buffer must be a direct Buffer with native byte
	 * order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param numElements
	 *            the number of elements to copy.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 */
	public static void copy(long[] src, int srcOffset, int numElements, Buffer dst) {
		copyJNI(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position and limit
	 * will stay the same. <b>The Buffer must be a direct Buffer with native byte
	 * order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param numElements
	 *            the number of elements to copy.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 */
	public static void copy(float[] src, int srcOffset, int numElements, Buffer dst) {
		copyJNI(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position and limit
	 * will stay the same. <b>The Buffer must be a direct Buffer with native byte
	 * order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param numElements
	 *            the number of elements to copy.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 */
	public static void copy(double[] src, int srcOffset, int numElements, Buffer dst) {
		copyJNI(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(char[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 1, dst, positionInBytes(dst), numElements << 1);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 1));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(int[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(long[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(float[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 2, dst, positionInBytes(dst), numElements << 2);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 2));
	}

	/**
	 * Copies the contents of src to dst, starting from src[srcOffset], copying
	 * numElements elements. The {@link Buffer} instance's {@link Buffer#position()}
	 * is used to define the offset into the Buffer itself. The position will stay
	 * the same, the limit will be set to position + numElements. <b>The Buffer must
	 * be a direct Buffer with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source array.
	 * @param srcOffset
	 *            the offset into the source array.
	 * @param dst
	 *            the destination Buffer, its position is used as an offset.
	 * @param numElements
	 *            the number of elements to copy.
	 */

	public static void copy(double[] src, int srcOffset, Buffer dst, int numElements) {
		copyJNI(src, srcOffset << 3, dst, positionInBytes(dst), numElements << 3);
		dst.limit(dst.position() + bytesToElements(dst, numElements << 3));
	}

	/**
	 * Copies the contents of src to dst, starting from the current position of src,
	 * copying numElements elements (using the data type of src, no matter the
	 * datatype of dst). The dst {@link Buffer#position()} is used as the writing
	 * offset. The position of both Buffers will stay the same. The limit of the src
	 * Buffer will stay the same. The limit of the dst Buffer will be set to
	 * dst.position() + numElements, where numElements are translated to the number
	 * of elements appropriate for the dst Buffer data type. <b>The Buffers must be
	 * direct Buffers with native byte order. No error checking is performed</b>.
	 * 
	 * @param src
	 *            the source Buffer.
	 * @param dst
	 *            the destination Buffer.
	 * @param numElements
	 *            the number of elements to copy.
	 */
	public static void copy(Buffer src, Buffer dst, int numElements) {
		final int numBytes = elementsToBytes(src, numElements);
		copyJNI(src, positionInBytes(src), dst, positionInBytes(dst), numBytes);
		dst.limit(dst.position() + bytesToElements(dst, numBytes));
	}

	private static int positionInBytes(Buffer dst) {
		if (dst instanceof ByteBuffer)
			return dst.position();
		else if (dst instanceof ShortBuffer)
			return dst.position() << 1;
		else if (dst instanceof CharBuffer)
			return dst.position() << 1;
		else if (dst instanceof IntBuffer)
			return dst.position() << 2;
		else if (dst instanceof LongBuffer)
			return dst.position() << 3;
		else if (dst instanceof FloatBuffer)
			return dst.position() << 2;
		else if (dst instanceof DoubleBuffer)
			return dst.position() << 3;
		else
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
	}

	private static int bytesToElements(Buffer dst, int bytes) {
		if (dst instanceof ByteBuffer)
			return bytes;
		else if (dst instanceof ShortBuffer)
			return bytes >>> 1;
		else if (dst instanceof CharBuffer)
			return bytes >>> 1;
		else if (dst instanceof IntBuffer)
			return bytes >>> 2;
		else if (dst instanceof LongBuffer)
			return bytes >>> 3;
		else if (dst instanceof FloatBuffer)
			return bytes >>> 2;
		else if (dst instanceof DoubleBuffer)
			return bytes >>> 3;
		else
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
	}

	private static int elementsToBytes(Buffer dst, int elements) {
		if (dst instanceof ByteBuffer)
			return elements;
		else if (dst instanceof ShortBuffer)
			return elements << 1;
		else if (dst instanceof CharBuffer)
			return elements << 1;
		else if (dst instanceof IntBuffer)
			return elements << 2;
		else if (dst instanceof LongBuffer)
			return elements << 3;
		else if (dst instanceof FloatBuffer)
			return elements << 2;
		else if (dst instanceof DoubleBuffer)
			return elements << 3;
		else
			throw new RuntimeException("Can't copy to a " + dst.getClass().getName() + " instance");
	}

	public static FloatBuffer newFloatBuffer(int numFloats) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numFloats * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asFloatBuffer();
	}

	public static DoubleBuffer newDoubleBuffer(int numDoubles) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numDoubles * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asDoubleBuffer();
	}

	public static ByteBuffer newByteBuffer(int numBytes) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numBytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer;
	}

	public static ShortBuffer newShortBuffer(int numShorts) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numShorts * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asShortBuffer();
	}

	public static CharBuffer newCharBuffer(int numChars) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numChars * 2);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asCharBuffer();
	}

	public static IntBuffer newIntBuffer(int numInts) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numInts * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asIntBuffer();
	}

	public static LongBuffer newLongBuffer(int numLongs) {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(numLongs * 8);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		return buffer.asLongBuffer();
	}

	// @off
	/*JNI
	 #include <stdio.h>
	 #include <stdlib.h>
	 #include <string.h>
	 */

	public static void disposeUnsafeBuffer(ByteBuffer buffer) {
		final int size = buffer.capacity();
		boolean success = false;
		synchronized(unsafeBuffers) {
			for(int i = 0; i < unsafeBuffers.size; i++){
				if(unsafeBuffers.get(i) == buffer){
					success = true;
					break;
				}
			}
		}

		if(!success)
			throw new IllegalArgumentException("Buffer Not Allocated with newUnsafeByteBuffer() or already disposed!");

		unsafeAllocated -= size;
		freeMemory(buffer);
	}

	public static ByteBuffer newUnsafeByteBuffer(int numBytes) {
		final ByteBuffer buffer = newDisposableByteBuffer(numBytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		unsafeAllocated += numBytes;
		synchronized(unsafeBuffers){
			unsafeBuffers.add(buffer);
		}

		return buffer;
	}

	public static long getUnsafeBufferAddress(Buffer buffer) {
		return getBufferAddress(buffer) + buffer.position();
	}

	public static ByteBuffer newUnsafeByteBuffer(ByteBuffer buffer) {
		unsafeAllocated += buffer.capacity();
		synchronized(unsafeBuffers) {
			unsafeBuffers.add(buffer);
		}

		return buffer;
	}

	public static int getAllocatedBytesUnsafe() {
		return unsafeAllocated;
	}

	private static native ByteBuffer newDisposableByteBuffer(int numBytes);/*
		return env->NewDirectByteBuffer((char*)malloc(numBytes), numBytes);
	 */

	private static native long getBufferAddress(Buffer buffer);/*
		return (jlong)buffer;
	 */

	private static native void freeMemory(ByteBuffer buf); /*
		free(buf);
	 */

	public static native void clear(ByteBuffer buffer, int bytes); /*
		memset(buffer, 0, bytes);
	 */

	private static native void copyJNI(float[] src, Buffer dst, int floatCount, int offset);/*
		memcpy(dst, src + offset, floatCount << 2);
	 */

	private static native void copyJNI(byte[] src, int srcOffset, Buffer dst, int dstOffset, int bytes);/*
		memcpy(dst + dstOffset, src + srcOffset, bytes);
	 */

	private static native void copyJNI(char[] src, int srcOffset, Buffer dst, int dstOffset, int chars);/*
		memcpy(dst + dstOffset, src + srcOffset, chars);
	 */

	private static native void copyJNI(short[] src, int srcOffset, Buffer dst, int dstOffset, int shorts);/*
		memcpy(dst + dstOffset, src + srcOffset, shorts);
	 */

	private static native void copyJNI(int[] src, int srcOffset, Buffer dst, int dstOffset, int ints);/*
		memcpy(dst + dstOffset, src + srcOffset, ints);
	 */

	private static native void copyJNI(long[] src, int srcOffset, Buffer dst, int dstOffset, int longs);/*
		memcpy(dst + dstOffset, src + srcOffset, longs);
	 */

	private static native void copyJNI(float[] src, int srcOffset, Buffer dst, int dstOffset, int floats);/*
		memcpy(dst + dstOffset, src + srcOffset, floats);
	 */

	private static native void copyJNI(double[] src, int srcOffset, Buffer dst, int dstOffset, int doubles);/*
		memcpy(dst + dstOffset, src + srcOffset, doubles);
	 */

	private static native void copyJNI(Buffer src, int srcOffset, Buffer dst, int dstOffset, int length);/*
		memcpy(dst + dstOffset, src + srcOffset, length);
	 */
}
