package org.usfirst.frc.team1554;

import org.usfirst.frc.team1554.Camera.CameraFPS;
import org.usfirst.frc.team1554.Camera.CameraResolution;
import org.usfirst.frc.team1554.Camera.CameraSize;

public final class Ref {

	public static final String ROBOT_NAME = "[INSERT NAME HERE]";
	public static final short ROBOT_YEAR = 03737; // 2015
	
	public static final String CAM_NAME = "RoboCam";

	public static final class Buttons {
		public static final int ID_TURBO_DRIVE = 1;
		public static final int ID_SWAP_JOYSTICKS = 10;
		public static final int ID_DISABLE_TWIST = 9;
	}

	public static final class Channels {
		public static final int FL_DMOTOR = 0;
		public static final int RL_DMOTOR = 1;
		public static final int FR_DMOTOR = 8;
		public static final int RR_DMOTOR = 9;
	}

	public static final class Ports {
		public static final int JOYSTICK_LEFT = 0;
		public static final int JOYSTICK_RIGHT = 1;
	}

	public static final class Values {
		public static final double DRIVE_SCALE_FACTOR = 1.275;
		public static final double TWIST_STICK_DEADBAND = 0.1;
		public static final double MAG_STICK_DEADBAND = 0.05;
		
		public static final Camera.CameraResolution CAM_RES = CameraResolution.HIGH;
		public static final Camera.CameraSize CAM_SIZE = CameraSize.MEDIUM;
		public static final Camera.CameraFPS CAM_FPS = CameraFPS.HIGH;
	}

}
