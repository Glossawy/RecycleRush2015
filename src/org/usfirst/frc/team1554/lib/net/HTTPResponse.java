package org.usfirst.frc.team1554.lib.net;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface HTTPResponse {
	byte[] result();

	String resultAsString();

	InputStream resultAsStream();

	HTTPStatus status();

	String header();

	Map<String, List<String>> headers();
}
