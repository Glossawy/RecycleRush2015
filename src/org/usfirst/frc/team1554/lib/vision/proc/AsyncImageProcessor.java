package org.usfirst.frc.team1554.lib.vision.proc;

import org.usfirst.frc.team1554.lib.concurrent.AsyncTask;

import edu.wpi.first.wpilibj.image.ImageBase;

public abstract class AsyncImageProcessor<T extends ImageBase> implements ImageProcessor<T>, AsyncTask<Boolean> {

	protected T img;

	@Override
	public final Boolean call() throws Exception {
		if (this.img == null) return false;

		final boolean res = process(this.img);
		this.img = null;

		return res;
	}

	@Override
	abstract public boolean process(T img);
}
