package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.util.Preconditions;

public final class TeamInfo {

	private TeamInfo() {
	}

	private static String teamName;
	private static Integer teamNumber;

	public static String teamName() {
		return teamName;
	}

	public static int teamNumber() {
		return teamNumber.intValue();
	}

	public static synchronized void set(String name, int number) {
		Preconditions.checkState(teamName == null, "Attempting to change Team Name! But it's already set! Please use the EnhancedRobot Constructors!");
		Preconditions.checkState(teamNumber == null, "Attempting to change Team Number! But it's already set! Please use the EnahcnedRobot Constructors!");

		teamName = name;
		teamNumber = number;
	}

}
