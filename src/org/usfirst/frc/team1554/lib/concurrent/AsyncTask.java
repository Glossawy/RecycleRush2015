package org.usfirst.frc.team1554.lib.concurrent;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface AsyncTask<T> extends Callable<T> {

	@Override
	T call() throws Exception;

}
