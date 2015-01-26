package org.usfirst.frc.team1554.lib.vision;

public enum CameraQuality {

	VERY_LOW(10),
	LOW(25),
	MEDIUM(50),
	HIGH(75),
	VERY_HIGH(90),
	EXTREME(100);
	
	public final int kCompression;
	
	private CameraQuality(int quality) {
		this.kCompression = quality;
	}
	
	@Override
	public String toString() {
		return String.format("cam_qual(%s: %s%%)", name(), kCompression);
	}
	
}
