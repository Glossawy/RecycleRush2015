package org.usfirst.frc.team1554.lib;

public final class Version {

	private Version() {
	}

	public static final String VERSION = "1.0.0";

	public static final int MAJOR, MINOR, REVISION;

	static {
		try {
			final String[] parts = VERSION.split("\\.");
			MAJOR = parts.length < 1 ? 0 : Integer.parseInt(parts[0]);
			MINOR = parts.length < 2 ? 0 : Integer.parseInt(parts[1]);
			REVISION = parts.length < 3 ? 0 : Integer.parseInt(parts[2]);
		} catch (final Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static boolean isEqual(int major, int minor, int revision) {
		return (MAJOR == major) && (MINOR == minor) && (REVISION == revision);
	}

	public static boolean isHigher(int major, int minor, int revision) {
		return isHigherOrEqualTo(major, minor, revision + 1);
	}

	public static boolean isHigherOrEqualTo(int major, int minor, int revision) {
		if (MAJOR != major)
			return major < MAJOR;
		else if (MINOR != minor) return minor < MINOR;

		return REVISION >= revision;
	}

	public static boolean isLower(int major, int minor, int revision) {
		return isLowerOrEqualTo(major, minor, revision - 1);
	}

	public static boolean isLowerOrEqualTo(int major, int minor, int revision) {
		if (MAJOR != major)
			return major > MAJOR;
			else if (MINOR != minor) return minor > MINOR;

		return REVISION <= revision;
	}

}
