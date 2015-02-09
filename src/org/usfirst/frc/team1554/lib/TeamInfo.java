package org.usfirst.frc.team1554.lib;

public final class TeamInfo {

	private TeamInfo(){}
	
	private static String teamName = "Not Provided";
	private static int teamNumber = -1;
	
	public static synchronized String teamName() {
		return teamName;
	}
	
	public static synchronized int teamNumber() {
		return teamNumber;
	}
	
	public static synchronized void set(String teamName, int teamNumber) {
		TeamInfo.teamName = teamName;
		TeamInfo.teamNumber = teamNumber;
	}
	
}
