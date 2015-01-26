package org.usfirst.frc.team1554.lib.vision;

public enum WhiteBalance {

	AUTO(-1, "auto"),
	INDOORS(3000, "fixed_indoor"),
	OUTDOOR_LIGHT(4000, "fixed_outdoor1"),
	OUTDOOR_HEAVY(5000, "fixed_outdoor2"),
	FLUORESCENT_LIGHT(5100, "fixed_fluor1"),
	FLUORESCENT_HEAVY(5200, "fixed_fluor2");

	private final int value;
	private final String ethernet;
	
	private WhiteBalance(int val, String ethernetName) {
		this.value = val;
		this.ethernet = ethernetName;
	}
	
	public int getWhiteBalance() {
		return value;
	}
	
	public String ethernetName() {
		return ethernet;
	}
	
	public boolean isAuto() {
		return isAuto(this);
	}
	
	public static boolean isAuto(WhiteBalance balance) {
		return balance == AUTO;
	}
	
}
