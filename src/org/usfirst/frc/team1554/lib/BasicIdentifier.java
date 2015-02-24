package org.usfirst.frc.team1554.lib;

/**
 * Needs Documentation
 *
 * @author Matthew
 *         Created 2/23/2015 at 10:27 PM
 */
public final class BasicIdentifier implements Identifier {

    private final String teamName;
    private final int teamNumber;

    public BasicIdentifier(String teamName, int teamNumber) {
        this.teamName = teamName;
        this.teamNumber = teamNumber;
    }

    @Override
    public String teamName() {
        return teamName;
    }

    @Override
    public int teamNumber() {
        return teamNumber;
    }

}
