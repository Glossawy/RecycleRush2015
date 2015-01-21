package org.usfirst.frc.team1554.lib.concurrent;

import org.usfirst.frc.team1554.lib.io.Console;
import org.usfirst.frc.team1554.lib.meta.Author;
import org.usfirst.frc.team1554.lib.meta.Noteworthy;

@Author(name = "Matthew Crocco", msg = "matthewcrocco@gmail.com")
@Noteworthy("Multi-threaded Logic. Refrain from Editing!")
public class StateThread extends Thread {

	private final Runnable runnable;
	private boolean paused = false;
	private boolean exit = false;

	public StateThread(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				try {
					while (this.paused) {
						wait();
					}
				} catch (final InterruptedException e) {
					Console.exception(e);
				}

				if (this.exit) return;

				this.runnable.run();
			}
		}
	}

	public void pauseExecution() {
		this.paused = true;
	}

	public void resumeExecution() {
		synchronized (this) {
			this.paused = false;
			notifyAll();
		}
	}

	public boolean isPaused() {
		return this.paused;
	}

	public boolean isExited() {
		return this.exit;
	}

	public void exitThread() {
		this.exit = true;
		if (this.paused) {
			resumeExecution();
		}
	}
}
