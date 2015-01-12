package org.usfirst.frc.team1554.lib.io;

import java.io.IOException;

public interface LineProcessor<T> {

	boolean processLine(String line) throws IOException;

	T result();

}
