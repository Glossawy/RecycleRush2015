package org.usfirst.frc.team1554.lib.util;
 
import java.lang.reflect.Array; 
import java.lang.reflect.Field;

import org.usfirst.frc.team1554.lib.MotorScheme;
import org.usfirst.frc.team1554.lib.meta.RobotExecutionException;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.HALControlWord;

public final class RoboUtils {

	private RoboUtils() {
	}

	/**
	 * Previously Built a RobotDrive, now delegates to
	 * {@link MotorScheme#getRobotDrive() MotorScheme}.
	 * 
	 * @param scheme
	 * @return
	 */
	public static final RobotDrive makeRobotDrive(MotorScheme scheme) {
		return scheme.getRobotDrive();
	}
	
	public static final void writeToDS(String message) {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		if(controlWord.getDSAttached()) {
			FRCNetworkCommunicationsLibrary.HALSetErrorData(message);
		}
	}
	
	public static final void exceptionToDS(Throwable t) {
		StackTraceElement[] stackTrace = t.getStackTrace();
		StringBuilder message = new StringBuilder();
		final String separator = "===\n";
		final Throwable cause = t.getCause();
		
		message.append("Exception of type ").append(t.getClass().getName()).append('\n');
		message.append("Message: ").append(t.getMessage()).append('\n');
		message.append(separator);
		message.append("   ").append(stackTrace[0]).append('\n');
		
		for(int i = 2 ; i < stackTrace.length; i++) {
			message.append(" \t").append(stackTrace[i]).append('\n');
		}
		
		if(cause != null) {
			StackTraceElement[] causeTrace = cause.getStackTrace();
			message.append(" \t\t").append("Caused by ") .append(cause.getClass().getName()).append('\n');
			message.append(" \t\t").append("Because: ").append(cause.getMessage()).append('\n');
			message.append(" \t\t   ").append(causeTrace[0]).append('\n');
			message.append(" \t\t \t").append(causeTrace[2]).append('\n');
			message.append(" \t\t \t").append(causeTrace[3]);
		}

		writeToDS(message.toString());
	}

	// Retrieve Object at Field.
	private static Object retrieveField(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Object item = null;
		final boolean accessible = field.isAccessible();

		if (!accessible) {
			field.setAccessible(!accessible);
		}

		item = field.get(obj);

		if (!accessible) {
			field.setAccessible(accessible);
		}

		return item;
	}

	/**
	 * Retrieve Value from Static Field (Belonging to No Particular Object)
	 * 
	 * @param klass
	 * @param fieldName
	 * @param conversionClass
	 * @return
	 */
	public static <T> T getStaticField(Class<?> klass, String fieldName, Class<T> conversionClass) {
		try {
			final Field field = klass.getDeclaredField(fieldName);
			return conversionClass.cast(retrieveField(field, null));
		} catch (final Exception e) {
			throw new RobotExecutionException("Failure to reflectively obtain Static Field '" + fieldName + "'!", e);
		}
	}

	/**
	 * Retrieve Value from Instance Field (Belonging to the Given Object)
	 * 
	 * @param instance
	 * @param fieldName
	 * @param conversionClass
	 * @return
	 */
	public static <T> T getInstanceField(Class<?> instClass, Object instance, String fieldName, Class<T> conversionClass) {
		try {
			final Field field = instClass.getDeclaredField(fieldName);
			return conversionClass.cast(retrieveField(field, instance));
		} catch (final Exception e) {
			throw new RobotExecutionException("Failure to reflectively obtain Instance Field '" + fieldName + "' from Object '" + instance + "'!", e);
		}
	}

	/**
	 * Create New One Dimensional Array of Some Type
	 * 
	 * @param klass
	 * @param size
	 * @param conversionClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<? extends T> klass, int size) {
		return (T[]) Array.newInstance(klass, size);
	}

	/**
	 * Create new Two Dimensional Array of Some Type
	 * 
	 * @param klass
	 * @param size1
	 * @param size2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[][] newArray2D(Class<? extends T> klass, int size1, int size2) {
		return (T[][]) Array.newInstance(klass, size1, size2);
	}

	// Set Field to Some Value and return the Old Value
	private static <T> Object setField(Field field, T newValue, Object instance) throws IllegalArgumentException, IllegalAccessException {
		Object old = null;
		final boolean accessible = field.isAccessible();

		if (!accessible) {
			field.setAccessible(!accessible);
		}

		old = retrieveField(field, instance);
		field.set(instance, newValue);

		field.setAccessible(accessible);

		return old;
	}

	/**
	 * Set Value of Some Static Field (Not Belonging to Any Particular Object)
	 * 
	 * @param klass
	 * @param fieldName
	 * @param newValue
	 * @param conversionClass
	 * @return
	 */
	public static <T> T setStaticField(Class<?> klass, String fieldName, T newValue, Class<T> conversionClass) {
		try {
			return conversionClass.cast(setField(klass.getDeclaredField(fieldName), newValue, null));
		} catch (final Exception e) {
			throw new RobotExecutionException("Failure to Reflectively Set Static Field '" + fieldName + "' to " + newValue + "!", e);
		}
	}

	/**
	 * Set Value of Some Instance Field (Belonging to A Particular Object)
	 * 
	 * @param instance
	 * @param fieldName
	 * @param newValue
	 * @param conversionClass
	 * @return
	 */
	public static <T> T setInstanceField(Class<?> instClass, Object instance, String fieldName, T newValue, Class<T> conversionClass) {
		try {
			return conversionClass.cast(setField(instClass.getDeclaredField(fieldName), newValue, instance));
		} catch (final Exception e) {
			throw new RobotExecutionException("Failure to Reflectively Set Instance Field '" + fieldName + "' in " + instance + " to " + newValue + "!", e);
		}
	}

}
