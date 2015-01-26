package org.usfirst.frc.team1554.lib.concurrent;

import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.usfirst.frc.team1554.lib.Disposable;
import org.usfirst.frc.team1554.lib.meta.Author;
import org.usfirst.frc.team1554.lib.meta.Beta;
import org.usfirst.frc.team1554.lib.meta.TimingException;

@Beta
@Author(name = "Matthew Crocco", msg = "matthewcrocco@gmail.com")
public class AsyncExecutor implements Disposable {

	private final ExecutorService executor;

	public AsyncExecutor(int maxConcurrent) {
		this.executor = Executors.newFixedThreadPool(maxConcurrent, new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				final Thread t = new Thread(r, "RoboAsync");
				t.setDaemon(true);
				return t;
			}
		});
	}

	public <T> AsyncResult<T> submit(AsyncTask<T> task) {
		if (isShutdown()) throw new TimingException("Cannot Execute Async Task on a Shutdown Executor!");

		return new AsyncResult<T>(this.executor.submit(task));
	}

	public boolean isShutdown() {
		return this.executor.isShutdown();
	}

	@Override
	public void dispose() {
		this.executor.shutdown();
	}

}
