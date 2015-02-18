package org.usfirst.frc.team1554;

public final class Ref {

	public static final String ROBOT_NAME = "[INSERT NAME HERE]";
	public static final short ROBOT_YEAR = 03737;

	public static final class Buttons {
		public static final int ID_TURBO_DRIVE = 1;
		public static final int ID_SWAP_JOYSTICKS = 10;
		public static final int ID_DISABLE_TWIST = 9;
		
		public static final int ID_ARMS_TOGGLE = 2;
	}

	public static final class Channels {
		public static final int FL_DMOTOR = 0;
		public static final int RL_DMOTOR = 1;
		public static final int FR_DMOTOR = 8;
		public static final int RR_DMOTOR = 9;
		
		public static final int WINCH_MOTOR = 7;
		public static final int DIGITAL_WINCH_UP_LIMIT = 0;
		public static final int DIGITAL_WINCH_DOWN_LIMIT = 1;
		
		public static final int CHANNEL_ARM_FWD = 1;
		public static final int CHANNEL_ARM_BCK = 2;
	}

	public static final class Ports {
		public static final int JOYSTICK_LEFT = 0;
		public static final int JOYSTICK_RIGHT = 1;
	}

	public static final class Values {
		public static final double DRIVE_SCALE_FACTOR = 1.25;
		public static final double TWIST_STICK_DEADBAND = 0.1;
		public static final double MAG_STICK_DEADBAND = 0.05;

		public static final int CONCURRENCY = 2;
	}

}
