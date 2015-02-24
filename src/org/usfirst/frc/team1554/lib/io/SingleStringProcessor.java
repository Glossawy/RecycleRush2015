package org.usfirst.frc.team1554.lib.io;

import java.io.IOException;

public class SingleStringProcessor implements LineProcessor<String> {

    private static final String SEP = System.getProperty("line.separator");
    private final StringBuilder sb = new StringBuilder();

    @Override
    public boolean processLine(String line) throws IOException {
        if (this.sb.length() == 0) {
            this.sb.append(line);
        } else {
            this.sb.append(SEP).append(line);
        }

        return true;
    }

    @Override
    public String result() {
        return this.sb.toString();
    }

}
