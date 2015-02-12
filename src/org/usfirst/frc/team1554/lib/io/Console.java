package org.usfirst.frc.team1554.lib.io;

import static java.util.logging.Level.ALL;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

public class Console {

	private static final Logger logger;
	public static final String LOG_PATTERN = "%1$tH:%1$tM:%1$tS - [%2$-5s][%3$-5s]: %4$s %n";

	static {

		logger = Logger.getLogger("FRC1554");

		Thread.currentThread().setName("FRC1554");
		logger.setUseParentHandlers(false);
		logger.setLevel(ALL);

		final ConsoleHandler cons = new ConsoleHandler();

		try {
			final Method meth = StreamHandler.class.getDeclaredMethod("setOutputStream", OutputStream.class);
			meth.invoke(cons, System.out);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			Console.exception(e);
		}

		cons.setFormatter(new LogFormatter());

		logger.addHandler(cons);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			close(cons);

		}));

	}

	/**
	 * Does the same as {@link #log(Object, Level)} but goes further by taking a
	 * Throwable and <br />
	 * passing it to {@link Logger#log(org.apache.log4j.Priority, Object, Throwable)}
	 * to be parsed. <br />
	 * <br />
	 * The Throwable is parsed independently by each Appender. <br />
	 * In a normal use case this is ConsoleAppender and RollingFileAppender. <br />
	 * 
	 * @param msg
	 * @param t
	 * @param level
	 */
	public static void log(Object msg, Throwable t, Level level) {
		synchronized (logger) {
			logger.log(level, String.valueOf(msg), t);
		}
	}

	/**
	 * Takes an Object as a message and obtains its String Value <br />
	 * via {@link String#valueOf(Object)} which is null-safe. <br />
	 * <br />
	 * The String Message and Level are passed to
	 * {@link Logger#log(org.apache.log4j.Priority, Object)} <br />
	 * to be printed to the Console and Log File (If created) <br />
	 * 
	 * @param msg
	 * @param level
	 */
	public static void log(Object msg, Level level) {
		synchronized (logger) {
			logger.log(level, String.valueOf(msg));
		}
	}

	/**
	 * Indicates a certain point has been reached successfully and may <br />
	 * print state information. <br />
	 * <br />
	 * This is the Level that should be generally used in normal cases. <br />
	 * 
	 * @param msg
	 */
	public static void info(Object msg) {
		log(msg, Level.INFO);
	}

	/**
	 * Indicates a Debug Message meant for the Programmer alone.
	 * 
	 * @param msg
	 */
	public static void debug(Object msg) {
		log(msg, FINE);
	}

	/**
	 * Write a trace to the log with no extra information.
	 */
	public static void trace() {
		trace("");
	}

	/**
	 * Write a Stack Trace to the log with the given additional message. Writes 16
	 * Stack Trace Elements at most.
	 * 
	 * @param msg
	 */
	public static void trace(Object msg) {
		int lines = 0;
		final Thread current = Thread.currentThread();
		final StackTraceElement[] trace = current.getStackTrace();
		final String border = "========================================";

		debug("");
		debug(border);
		debug(String.format("|| Detailed Stack Trace of %s[%s] Thread", current.getName(), current.getId()));
		debug("|| Trace Message: " + String.valueOf(msg));
		debug(border);
		for (int i = 2; (i < 18) && (i < trace.length); i++) {
			debug(String.format("||   at %s%s", trace[i].toString(), (i < 15) && (i < (trace.length - 1)) ? "..." : ""));
			lines++;
		}
		debug(border);
		debug("|| Resolved " + lines + " elements...");
		debug(border);
		debug("");
	}

	/**
	 * Does the same as {@link #bigWarning(Object)} but prints "null" as the message.
	 */
	public static void bigWarning() {
		bigWarning("");
	}

	/**
	 * Prints a very noticeable warning that is bordered. <br />
	 * <br />
	 * Indicates the same thing as {@link #warn(Object)} but also prints<br />
	 * a 6 line Stack Trace (will not include the call to this method). <br />
	 * <br />
	 * <code>
	 * **************************************** <br/>
	 * * Message Here<br/>
	 * *  at trace(class:line)<br/>
	 * *  at trace(class:line)<br/>
	 * *  at trace(class:line)<br/>
	 * *  at trace(class:line)<br/>
	 * *  at trace(class:line)<br/>
	 * *  at trace(class:Line)<br/>
	 * **************************************** <br/>
	 * </code>Much Thanks to the Minecraft Forge Team for the idea!
	 * 
	 * @param msg
	 */
	public static void bigWarning(Object msg) {
		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		final String border = "****************************************";
		warn("");
		warn(border);
		warn("* Warning! - " + String.valueOf(msg));
		warn(border);
		for (int i = 2; (i < 8) && (i < trace.length); i++) {
			warn(String.format("*   at %s%s", trace[i].toString(), (i < 7) && (i < (trace.length - 1)) ? "..." : ""));
		}
		warn(border);
		warn("");
	}

	/**
	 * Indicates that although the program can continue as expected, <br />
	 * the program may act unexpectedly due to receiving a valid, but <br />
	 * unexpected result or value.
	 * 
	 * @param msg
	 */
	public static void warn(Object msg) {
		log(msg, WARNING);
	}

	/**
	 * Indicates an Error that is recoverable but should be noted to the <br />
	 * user or programmer since this is likely a programmer error. <br />
	 * 
	 * @param msg
	 */
	public static void error(Object msg) {
		log(msg, SEVERE);
	}

	/**
	 * Indicates a Fatal Error that has caused the program to terminate <br />
	 * since the error is unrecoverable.
	 * 
	 * @param msg
	 */
	public static void fatal(Object msg) {
		log(msg, SEVERE);
	}

	/**
	 * Write an Exception to the Log with the full Stack Trace.
	 * 
	 * @param e
	 */
	public static void exception(Throwable e) {
		exception(e, null);
	}

	/**
	 * Write an Exception to the Log with the full Stack Trace and the given details.
	 * 
	 * @param e
	 * @param details
	 */
	public static void exception(Throwable e, Object details) {
		final StackTraceElement[] elements = e.getStackTrace();
		final String header = "===============EXCEPTION===============";
		final String separator = "=======================================";
		final Throwable t = e.getCause();

		error(header);
		error("Exception of type " + e.getClass().getName() + " caught!");
		error("Error Message: " + e.getLocalizedMessage());
		if (details != null) {
			error(String.valueOf(details));
		}
		error(separator);
		error("Stack Trace: ");
		error("   " + elements[0]);
		for (int i = 1; i < elements.length; i++) {
			error(" \t" + elements[i]);
		}

		if (t != null) {
			cause(t, t.getLocalizedMessage() + " causing a " + e.getClass().getName(), 1);
		}

		error(header);
	}

	private static String repeat(String str, int times) {
		final StringBuilder sb = new StringBuilder();

		while (times-- > 0) {
			sb.append(str);
		}

		return sb.toString();
	}

	private static void cause(Throwable t, String details, int depth) {
		final StackTraceElement[] elements = t.getStackTrace();
		final String tabs = repeat("\t", depth);
		final String causer = tabs + "===============CAUSED BY===============";
		final String separator = tabs + "=======================================";
		final Throwable cause = t.getCause();

		error(causer);
		error(tabs + "Exception of type " + t.getClass().getName() + "!");
		error(tabs + "Error Message: " + t.getLocalizedMessage());
		if (details != null) {
			error(tabs + String.valueOf(details));
		}
		error(separator);
		error(tabs + "Stack Trace: ");
		error(tabs + "   " + elements[0]);
		for (int i = 2; i < elements.length; i++) {
			error(" " + tabs + "\t" + elements[i]);
		}

		if (cause != null) {
			cause(cause, cause.getLocalizedMessage() + " causing a " + t.getClass().getName(), ++depth);
		}

		error(causer);
	}

	/**
	 * Add an Appender as a another Logging Target in addition to <br />
	 * the Console and Log File.
	 * 
	 * @param appender
	 */
	public static synchronized void addLogTarget(Handler appender) {
		logger.addHandler(appender);
	}

	private static final void close(Handler handler) {
		if (handler == null) return;

		try {
			handler.close();
		} catch (final Exception ignore) {
		}
	}

	/**
	 * Custom formatter for Log Messages.
	 * 
	 * @author Matthew
	 */
	private static class LogFormatter extends Formatter {

		private final String format = LOG_PATTERN;
		private final Date date = new Date();

		@Override
		public String format(LogRecord record) {
			this.date.setTime(record.getMillis());
			final String msg = formatMessage(record);
			final Level lev = record.getLevel();

			return String.format(this.format, this.date, record.getLoggerName(), lev == WARNING ? "WARN" : lev == FINE ? "DEBUG" : lev == SEVERE ? "ERROR" : lev.getName(), msg);
		}

	}

}
