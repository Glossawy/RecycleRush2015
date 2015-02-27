package org.usfirst.frc.team1554.lib.meta;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/23/2015 at 10:06 PM
 */
public interface Identifier {

    public static Identifier newBasicID(String teamName, int teamNumber) {
        return new BasicIdentifier(teamName, teamNumber);
    }

    String teamName();

    int teamNumber();

    default String teamNumberAsString() {
        String number = String.valueOf(teamNumber());
        while (number.length() < 4) number = "0" + number;

        return number;
    }

}
