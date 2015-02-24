package org.usfirst.frc.team1554.lib;

import org.usfirst.frc.team1554.lib.util.RoboUtils;

class RobotExceptionHandler implements Thread.UncaughtExceptionHandler {
    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Console.exception(e, "Uncaught Exception in Thread: " + t.getName());
        RoboUtils.exceptionToDS(e);
    }
}
