package org.usfirst.frc.team1554.lib.util.memory;

/**
 * Created by Matthew on 2/23/2015.
 */
public final class MemoryUtils {

    public static void free(Freeable freeable) {
        if (freeable != null)
            freeable.free();
    }

    public static void freeAll(Freeable... freeables) {
        for (Freeable f : freeables)
            free(f);
    }

}
