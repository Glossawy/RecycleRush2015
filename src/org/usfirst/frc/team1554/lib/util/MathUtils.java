package org.usfirst.frc.team1554.lib.util;

import java.util.Random;

public class MathUtils {

	private static final Random rand = new Random(System.nanoTime());

	public static final int random(int upper) {
		return rand.nextInt(upper);
	}

	public static final int nextPowerOfTwo(int n) {
		n--;
		n |= n >> 1;
		n |= n >> 2;
		n |= n >> 4;
		n |= n >> 8;
		n |= n >> 16;
		n++;
		return n;
	}

}
