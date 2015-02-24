package org.usfirst.frc.team1554.lib.util;

import org.usfirst.frc.team1554.lib.RobotExecutionException;
import org.usfirst.frc.team1554.lib.RobotReflectionException;

import java.lang.reflect.*;

/**
 * Created by Matthew on 2/22/2015.
 */
public class ReflectionHelper {

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
            return conversionClass.cast(retrieveField(klass.getDeclaredField(fieldName), null));
        } catch (final Exception e) {
            if (klass.getSuperclass() == null)
                throw new RobotExecutionException("Failure to reflectively obtain Static Field '" + fieldName + "'!", e);
        }

        return getStaticField(klass.getSuperclass(), fieldName, conversionClass);
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
            return conversionClass.cast(retrieveField(instClass.getDeclaredField(fieldName), instance));
        } catch (final Exception e) {
            if (instClass.getSuperclass() == null)
                throw new RobotExecutionException("Failure to reflectively obtain Instance Field '" + fieldName + "' from Object '" + instance + "'!", e);
        }

        return getInstanceField(instClass.getSuperclass(), instance, fieldName, conversionClass);
    }

    /**
     * Create New One Dimensional Array of Some Type
     *
     * @param klass
     * @param size
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
            if (klass.getSuperclass() == null)
                throw new RobotExecutionException("Failure to Reflectively Set Static Field '" + fieldName + "' to " + newValue + "!", e);
        }

        return setStaticField(klass.getSuperclass(), fieldName, newValue, conversionClass);
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
    public static <T> T setInstanceField(Class<?> instClass, Object instance, String fieldName, T newValue, Class<? extends T> conversionClass) {

        try {
            return conversionClass.cast(setField(instClass.getDeclaredField(fieldName), newValue, instance));
        } catch (final Exception e) {
            if (instClass.getSuperclass() == null)
                throw new RobotExecutionException("Failure to Reflectively Set Instance Field '" + fieldName + "' in " + instance + " to " + newValue + "!", e);
        }

        return setInstanceField(instClass.getSuperclass(), instance, fieldName, newValue, conversionClass);
    }

    private static Object invokeMethod(Method meth, Object inst, Object[] args) throws IllegalAccessException, InvocationTargetException {

        boolean accessible = meth.isAccessible();

        if (!accessible)
            meth.setAccessible(!accessible);

        Object res = meth.invoke(inst, args);

        meth.setAccessible(accessible);
        return res;
    }

    public static <T> T invokeStaticMethod(Class<?> klass, CallParameters params, Class<T> conversionClass) {
        try {

            T res = null;
            if (conversionClass != null)
                res = conversionClass.cast(invokeMethod(klass.getDeclaredMethod(params.name, params.callTypes), null, params.callArguments));

            return res;
        } catch (Exception e) {
            if (klass.getSuperclass() == null)
                throw new RobotExecutionException("Failed to invoke static method '" + params.name + "' with parameters: " + params + "!", e);
        }

        return invokeStaticMethod(klass.getSuperclass(), params, conversionClass);
    }

    public static void invokeStaticMethod(Class<?> klass, CallParameters params) {
        invokeStaticMethod(klass, params, null);
    }

    public static <T> T invokeInstanceMethod(Class<?> klass, Object inst, CallParameters params, Class<T> conversionClass) {
        try {

            T res = null;
            if (conversionClass != null)
                res = conversionClass.cast(invokeMethod(klass.getDeclaredMethod(params.name, params.callTypes), inst, params.callArguments));

            return res;
        } catch (Exception e) {
            if (klass.getSuperclass() == null)
                throw new RobotExecutionException("Failed to invoke instance method '" + params.name + "' on instance " + inst + " with parameters: " + params, e);
        }

        return invokeInstanceMethod(klass.getSuperclass(), inst, params, conversionClass);
    }

    public static void invokeInstanceMethod(Class<?> klass, Object inst, CallParameters params) {
        invokeInstanceMethod(klass, inst, params, null);
    }

    private static <T> T invokeConstructor(Constructor<? extends T> cons, Object... args) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        T res;
        boolean access = cons.isAccessible();

        if (!access)
            cons.setAccessible(!access);

        res = cons.newInstance(args);

        cons.setAccessible(access);
        return res;
    }

    public static <T> T newInstance(Class<? extends T> klass) {
        try {
            return klass.newInstance();
        } catch (Exception e) {
            throw new RobotReflectionException("Failed to Reflectively Create instance of " + klass.getName(), e);
        }
    }

    public static <T> T newInstance(Class<? extends T> klass, CallParameters params) {
        try {
            return invokeConstructor(klass.getDeclaredConstructor(params.callTypes), params.callArguments);
        } catch (Exception e) {
            throw new RobotReflectionException("Failed to Reflectively Create Instance of " + klass.getName() + " with parameters: " + params, e);
        }
    }

    public static <T> T newUnknownInstance(Class<?> klass, CallParameters params) {
        try {
            return (T) klass.getDeclaredConstructor(params.callTypes).newInstance(params.callArguments);
        } catch (Exception e) {
            throw new RobotReflectionException("Failed to Reflectively Create Instance of " + klass.getName() + " with parameters: " + params, e);
        }
    }

    /**
     * Useful abstraction of the details required to make a call to a Constructor or Method. <br />
     * Supports Parameters, Parameter Type Guessing and Null Parameters.
     */
    public static class CallParameters {

        /**
         * Name Descriptor to Call (Typically Constructor or Method Name)
         */
        public final String name;
        private Object[] callArguments = new Object[0];
        private Class<?>[] callTypes = new Class<?>[0];
        private int size = 0;

        private CallParameters(String methodName) {
            this.name = methodName;
        }

        public static CallParameters createEmptyMethodCall(String name, Class<?>... paramTypes) {
            CallParameters params = createMethodCall(name);

            for (Class<?> klass : paramTypes)
                params.addNull(klass);

            return params;
        }

        public static CallParameters createEmptyConstructorCall(Class<?>... paramTypes) {
            CallParameters params = createConstructorCall();

            for (Class<?> klass : paramTypes)
                params.addNull(klass);

            return params;
        }

        public static CallParameters createMethodCall(String methodName) {
            return new CallParameters(methodName);
        }

        public static CallParameters createConstructorCall() {
            return new CallParameters("Constructor");
        }

        public CallParameters add(Object arg, Class<?> argType) {
            append(arg, argType);

            return this;
        }

        public CallParameters addGuess(Object arg) {
            append(arg, arg.getClass());

            return this;
        }

        public CallParameters addNull(Class<?> argType) {
            append(null, argType);

            return this;
        }

        private void append(Object obj, Class<?> klass) {
            int oldSize = size;
            if (size + 1 > callArguments.length)
                resize(size + 1);

            callArguments[oldSize] = obj;
            callTypes[oldSize] = klass;
        }

        private void resize(int newSize) {
            if (newSize <= size)
                return;

            Object[] newArgs = new Object[newSize];
            Class<?>[] newTypes = new Class<?>[newSize];
            System.arraycopy(callArguments, 0, newArgs, 0, callArguments.length);
            System.arraycopy(callTypes, 0, newTypes, 0, callTypes.length);

            callArguments = newArgs;
            callTypes = newTypes;
            size = newSize;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            boolean element = false;

            sb.append(name).append(":{");
            for (int i = 0; i < callArguments.length; i++) {
                element = true;
                sb.append(String.valueOf(callArguments[i]))
                        .append(": ").append(callTypes[i].getSimpleName())
                        .append(", ");
            }

            if (element)
                sb.replace(sb.length() - 2, sb.length(), "").trimToSize();
            return sb.append('}').toString();
        }
    }

}
