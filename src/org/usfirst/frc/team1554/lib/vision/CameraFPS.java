package org.usfirst.frc.team1554.lib.vision;

public enum CameraFPS {
	MINIMAL(5), LOW(15), NORMAL(30), HIGH(60);

	public final int kFPS;

	private CameraFPS(int fps) {
		this.kFPS = fps;
	}

	@Override
	public String toString() {
		return String.format("cam_fps(%s: %s FPS)", name(), this.kFPS);
	}
}
