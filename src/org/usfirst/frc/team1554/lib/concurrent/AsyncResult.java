package org.usfirst.frc.team1554.lib.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.usfirst.frc.team1554.lib.meta.RobotExecutionException;

public class AsyncResult<T> {

	private final Future<T> future;

	public AsyncResult(Future<T> future) {
		this.future = future;
	}

	public boolean isDone() {
		return this.future.isDone();
	}

	public T get() {
		try {
			return this.future.get();
		} catch (final InterruptedException e) {
			return null;
		} catch (final ExecutionException e) {
			throw new RobotExecutionException(e.getMessage(), e.getCause());
		}
	}

}
