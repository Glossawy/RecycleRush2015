package org.usfirst.frc.team1554.lib.vision;

public enum CameraFPS {
    MINIMAL(10), LOW(20), NORMAL(30);

    public final int kFPS;

    private CameraFPS(int fps) {
        this.kFPS = fps;
    }

    @Override
    public String toString() {
        return String.format("cam_fps(%s: %s FPS)", name(), this.kFPS);
    }
}
