package org.usfirst.frc.team1554.lib.vision;

public enum Exposure {

	AUTO(-1, "auto"), NONE(0, "hold"), LOW(25, "flickerfree50"), MEDIUM(50, "flickerfree50"), HIGH(75, "flickerfree60"), MAX(100, "flickerfree60");

	private final int value;
	private final String ethernet;

	private Exposure(int val, String ethernet) {
		this.value = val;
		this.ethernet = ethernet;
	}

	public int getExposure() {
		return this.value;
	}

	public String ethernetName() {
		return this.ethernet;
	}

	public boolean isAuto() {
		return isAuto(this);
	}

	public static boolean isAuto(Exposure exposure) {
		return exposure == AUTO;
	}

}
