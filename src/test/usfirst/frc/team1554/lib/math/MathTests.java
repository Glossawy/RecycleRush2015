package test.usfirst.frc.team1554.lib.math;

import org.junit.Assert;
import org.junit.Test;
import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.math.RandomXS128;
import org.usfirst.frc.team1554.lib.math.WindowAverage;

public class MathTests {

	@Test
	public void randomTest() {
		try {
			final RandomXS128 rand = new RandomXS128();

			rand.nextInt();
			rand.nextBoolean();
			rand.nextDouble();
			rand.nextFloat();
			rand.nextLong();
		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void windowedAverageTest() {
		final WindowAverage avg = new WindowAverage(500);

		for (int i = 0; i < 2_000; i++) {
			avg.addValue(MathUtils.random(-MathUtils.random(1_000_000), MathUtils.random(1_000_000)));
		}

		System.out.println("Running Calculations");
		System.out.printf("Min: \t%.5f%n", avg.min());
		System.out.printf("Max: \t %.5f%n", avg.max());
		System.out.printf("Mean:\t %.5f%n", avg.mean());
		System.out.printf("Var(x):\t %.5f%n", avg.variance());
		System.out.printf("StDev :\t %.5f", avg.standardDeviation());
	}

}
