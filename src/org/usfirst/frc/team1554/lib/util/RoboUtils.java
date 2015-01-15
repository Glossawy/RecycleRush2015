package org.usfirst.frc.team1554.lib.util;

import java.lang.reflect.Array; 
import java.lang.reflect.Field;

import org.usfirst.frc.team1554.lib.MotorScheme;
import org.usfirst.frc.team1554.lib.meta.RobotExecutionException;

import edu.wpi.first.wpilibj.RobotDrive;

public final class RoboUtils {

	private RoboUtils() {
	}

	public static final RobotDrive makeRobotDrive(MotorScheme scheme) {
		return scheme.getRobotDrive();
	}
	
	private static Object retrieveField(Field field, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Object item = null;
		boolean accessible = field.isAccessible();
		
		if(!accessible)
			field.setAccessible(!accessible);
		
		item = field.get(obj);
		
		if(!accessible)
			field.setAccessible(accessible);
		
		return item;
	}
	
	public static <T> T getStaticField(Class<?> klass, String fieldName, Class<T> conversionClass) {
		try{
			Field field = klass.getDeclaredField(fieldName);
			return conversionClass.cast(retrieveField(field, null));
		} catch(Exception e) {
			throw new RobotExecutionException("Failure to reflectively obtain Static Field '" + fieldName + "'!", e);
		}
	}
	
	public static <T> T getInstanceField(Object instance, String fieldName, Class<T> conversionClass) {
		try {
			Field field = instance.getClass().getDeclaredField(fieldName);
			return conversionClass.cast(retrieveField(field, instance));
		} catch(Exception e) {
			throw new RobotExecutionException("Failure to reflectively obtain Instance Field '" + fieldName + "' from Object '" + instance + "'!", e);
		}
	}

	public static <T> T[] newArray(Class<? extends T> klass, int size, Class<? extends T[]> conversionClass) {
		return conversionClass.cast(Array.newInstance(klass, size));
	}
	
	public static <T> T[][] newArray2D(Class<? extends T> klass, int size1, int size2, Class<? extends T[][]> conversionClass) {
		return conversionClass.cast(Array.newInstance(klass, size1, size2));
	}
	
//	private static <T> T setField(Field field, T newValue, Object instance) {
//		if(field)
//		
//		T val = field.get(instance);
//	}
//	
//	public static <T> T setStaticField(Class<?> klass, String fieldName, T newValue) {
//		
//	}

}
