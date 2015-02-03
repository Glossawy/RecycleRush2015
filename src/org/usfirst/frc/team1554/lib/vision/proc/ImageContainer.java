package org.usfirst.frc.team1554.lib.vision.proc;

import org.usfirst.frc.team1554.lib.collect.Array;
import org.usfirst.frc.team1554.lib.concurrent.AsyncResult;
import org.usfirst.frc.team1554.lib.math.MathUtils;

import edu.wpi.first.wpilibj.image.ImageBase;

class ImageContainer<T extends ImageBase> {

	private final T image;
	private final Array<ImageProcessor<? super T>> processors;
	private final Array<AsyncResult<Boolean>> futures = new Array<>();

	private boolean processing = false;

	@SafeVarargs
	public ImageContainer(T img, ImageProcessor<? super T>... processors) {
		this.image = img;
		this.processors = new Array<>(true, processors, 0, processors.length);
	}

	public void process(ImageEngine engine) {
		this.futures.clear();
		this.processing = true;

		for (final ImageProcessor<? super T> processor : this.processors) {
			if (processor instanceof AsyncImageProcessor) {
				synchronized (this.futures) {
					final AsyncImageProcessor<? super T> proc = (AsyncImageProcessor<? super T>) processor;
					proc.img = this.image;
					this.futures.add(engine.executor.submit(proc));
				}
			} else {
				processor.process(this.image);
			}
		}

		this.processing = false;
	}

	public final T getImage() {
		return this.image;
	}

	public int finish() {
		int faults = 0;

		for (final AsyncResult<Boolean> result : this.futures) {
			faults += MathUtils.booleanToInt(!result.get());
		}

		this.futures.clear();
		return faults;
	}

	public boolean isComplete() {
		if (this.processing) return false;

		checkFutures();
		return this.futures.size == 0;
	}

	private void checkFutures() {
		synchronized (this.futures) {
			final Array<AsyncResult<Boolean>> killed = new Array<>();

			for (final AsyncResult<Boolean> future : this.futures) {
				if (future.isDone()) {
					killed.add(future);
				}
			}

			this.futures.removeAll(killed, true);
		}
	}
}
