package org.usfirst.frc.team1554.lib.math;

import java.util.Random;

public class MathUtils {

    private static final Random rand = new RandomXS128();

    /**
     * Random number between [0, upper]
     *
     * @param upper
     * @return
     */
    public static final int random(int upper) {
        return rand.nextInt(upper + 1);
    }

    /**
     * Random number between [start, end]
     *
     * @param start
     * @param end
     * @return
     */
    public static final int random(int start, int end) {
        return start + rand.nextInt((end - start) + 1);
    }

    /**
     * Random number between [0, range]
     *
     * @param range
     * @return
     */
    public static final long random(long range) {
        return ((RandomXS128) rand).nextLong(range + 1);
    }

    /**
     * Random number between [start, end]
     *
     * @param start
     * @param end
     * @return
     */
    public static final long random(long start, long end) {
        return start + random(end - start);
    }

    /**
     * Random boolean. True or False.
     *
     * @return
     */
    public static final boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     * Random Boolean weighted to one side. 1 being true, 0 being flase.
     *
     * @param chance
     * @return
     */
    public static final boolean nextBoolean(double chance) {
        return random() < chance;
    }

    /**
     * Random decimal between [0, 1.0)
     *
     * @return
     */
    public static final double random() {
        return rand.nextDouble();
    }

    /**
     * Random decimal between [0, range)
     *
     * @param range
     * @return
     */
    public static final double random(double range) {
        return rand.nextDouble() * range;
    }

    /**
     * Random decimal between [start, end)
     *
     * @param start
     * @param end
     * @return
     */
    public static final double random(double start, double end) {
        return start + (rand.nextDouble() * (end - start));
    }

    /**
     * Random Sign, i.e. -1 or 1.
     *
     * @return
     */
    public static final int randomSign() {
        return 1 | (rand.nextInt() >> 31);
    }

    /**
     * Clamp Val between Max and Min
     */
    public static final int clamp(int val, int max, int min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static final long clamp(long val, long max, long min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static final float clamp(float val, float max, float min) {
        return Math.max(min, Math.min(val, max));
    }

    /**
     * Clamp Val between Max and Min
     */
    public static final double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    /**
     * Linearly Interpolate between 'from' and 'to' at 'progress' position.
     */
    public static final double lerp(double from, double to, double progress) {
        return from + ((to - from) * progress);
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

    /**
     * Take the Logarithm of X in Base A. Uses Change of Base.
     *
     * @param a
     * @param x
     * @return
     */
    public static final double log(double a, double x) {
        return Math.log(x) / Math.log(a);
    }

    /**
     * Take the Logarithm of X in base 2.
     *
     * @param x
     * @return
     */
    public static final double log2(double x) {
        return log(2, x);
    }

    public static final int booleanToInt(boolean expression) {
        return expression ? 1 : 0;
    }

}
