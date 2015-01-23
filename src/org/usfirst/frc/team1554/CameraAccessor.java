package org.usfirst.frc.team1554;

import org.usfirst.frc.team1554.Camera.CameraFPS;
import org.usfirst.frc.team1554.Camera.CameraResolution;
import org.usfirst.frc.team1554.Camera.CameraSize;

import edu.wpi.first.wpilibj.image.HSLImage;
import edu.wpi.first.wpilibj.image.NIVisionException;

public interface CameraAccessor {
	
	CameraFPS getFPS();
	CameraResolution getResolution();
	CameraSize getImageSize();
	HSLImage getImage() throws NIVisionException;
	
}
