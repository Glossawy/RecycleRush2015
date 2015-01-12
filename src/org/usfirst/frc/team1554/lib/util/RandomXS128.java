package org.usfirst.frc.team1554.lib.util;

import java.util.Random;

public class RandomXS128 extends Random {

	private static final double DNORMALIZER = 1.0 / (1L << 53);
	private static final double FNORMALIZER = 1.0 / (1L << 24);

	private long seed1, seed2;

	public RandomXS128() {
		setSeed(new Random().nextLong());
	}

	public RandomXS128(long seed) {
		setSeed(seed);
	}

	@Override
	public long nextLong() {
		long s2 = this.seed1;
		final long s1 = this.seed2;

		this.seed1 = s1;
		s2 ^= s2 << 23;
		return (this.seed2 = (s2 ^ s1 ^ (s2 >> 17) ^ (s1 >>> 26))) + s1;
	}

	/**
	 * Returns Random Long
	 * 
	 * @param n
	 * @return
	 */
	public long nextLong(final long n) {
		if (n <= 0) throw new IllegalArgumentException("N must be > 0");

		while (true) {
			final long bits = nextLong() >>> 1;
	final long value = bits % n;
	if (((bits - value) + (n - 1)) >= 0) return value;
		}
	}

	@Override
	protected int next(int bits) {
		return (int) (nextLong() & ((1L << bits) - 1));
	}

	/**
	 * Returns Random Int. <br />
	 * <Br />
	 * Uses {@link #nextLong()}
	 */
	@Override
	public int nextInt() {
		return (int) nextLong();
	}

	/**
	 * Returns a Random Int from [0, n)
	 */
	@Override
	public int nextInt(final int n) {
		return (int) nextLong(n);
	}

	@Override
	public void setSeed(final long seed) {
		final long propSeed = hash(seed == 0 ? Long.MIN_VALUE : seed);
		setState(propSeed, hash(propSeed));
	}

	/**
	 * Random double between [0.0, 1.0)
	 */
	@Override
	public double nextDouble() {
		return (nextLong() >>> 11) * DNORMALIZER;
	}

	/**
	 * Ranbdom float between [0.0, 1.0)
	 */
	@Override
	public float nextFloat() {
		return (float) ((nextLong() >>> 40) * FNORMALIZER);
	}

	@Override
	public boolean nextBoolean() {
		return (nextLong() & 1) != 0;
	}

	@Override
	public void nextBytes(final byte[] bytes) {
		int n = 0;
		int l = bytes.length;

		while (l != 0) {
			n = l < 8 ? l : 8;
			for (long bits = nextLong(); n-- != 0; bits >>= 8) {
				bytes[--l] = (byte) bits;
			}
		}
	}

	public void setState(final long seed1, final long seed2) {
		this.seed1 = seed1;
		this.seed2 = seed2;
	}

	public long getState(int seed) {
		return seed == 0 ? this.seed1 : this.seed2;
	}

	private final static long hash(long x) {
		// murmur hash 3
		x ^= x >>> 33;
		x *= 0xff51afd7ed558ccdL;
		x ^= x >>> 33;
		x *= 0xc4ceb9fe1a85ec53L;
		x ^= x >>> 33;

		return x;
	}

}
