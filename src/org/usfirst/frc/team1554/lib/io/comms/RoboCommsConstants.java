package org.usfirst.frc.team1554.lib.io.comms;

final class RoboCommsConstants {

	private RoboCommsConstants() {

	}

	public static final int MAX_SIZE = 50;
	public static final String ROOT_TABLE = "frc1554-log";
	public static final String DEBUG_TABLE = "sysinfo";

	public static final String VISIBILITY_ENTRY = "$_ignore_$";
	public static final String ALLIANCE_ENTRY = "alliance";
	public static final String TEMPERATURE_ENTRY = "pdp_temp";
	public static final String VOLTAGE_ENTRY = "pdp_voltage";
	public static final String PIE_ENTRY = "pdp_pie_draw";
	public static final String ACCELEROMETER_ENTRY = "accelerometer";

	public static final String LINE_PREFIX = "line_";

}
