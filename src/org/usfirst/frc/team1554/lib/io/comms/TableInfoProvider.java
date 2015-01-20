package org.usfirst.frc.team1554.lib.io.comms;

import edu.wpi.first.wpilibj.tables.ITable;

@FunctionalInterface
public interface TableInfoProvider {

	void updateTable(ITable table);

}
