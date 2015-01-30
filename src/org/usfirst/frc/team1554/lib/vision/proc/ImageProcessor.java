package org.usfirst.frc.team1554.lib.vision.proc;

import edu.wpi.first.wpilibj.image.ImageBase;

@FunctionalInterface
public interface ImageProcessor<T extends ImageBase> {

	public boolean process(T img);

}
