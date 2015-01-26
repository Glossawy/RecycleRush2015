package org.usfirst.frc.team1554.lib.vision;

public enum CameraSize {

	SMALL(0, 160, 120),
	MEDIUM(1, 320, 240),
	LARGE(2, 640, 480);
	
	public final int kSize;
	public final int WIDTH;
	public final int HEIGHT;
	
	private CameraSize(int i, int width, int height) {
		this.kSize = i;
		this.WIDTH = width;
		this.HEIGHT = height;
	}
	
	public int getSize_WPILib() {
		return kSize;
	}
	
	@Override
	public String toString() {
		return String.format("cam_size(%s - %s | %sx%s)", kSize, name(), WIDTH, HEIGHT);
	}
	
}
