package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.Collection;
import java.util.Iterator;

/**
 * Various Utilities for handling Java Collections. Akin to Google Guava.
 *
 * @author Matthew
 */
public class CollectionsUtil {

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iter) {
        Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(iter);

        boolean modified = false;
        while (iter.hasNext()) {
            modified |= addTo.add(iter.next());
        }

        return modified;
    }

    @SafeVarargs
    public static <T> boolean addAll(Collection<T> addTo, T... arr) {
        Preconditions.checkNotNull(addTo);
        Preconditions.checkNotNull(arr);

        boolean modified = false;
        for (final T item : arr) {
            modified |= addTo.add(item);
        }

        return modified;
    }

    /**
     * Compute Capacity for least wasteful re-hashing.
     *
     * @param size
     * @return
     */
    public static int computeCapacity(int size) {
        if (size < 3)
            return size + 1;
        else if (size < 1073741824)
            return size + (size / 3);
        else
            return Integer.MAX_VALUE;
    }

    public static <T> Collection<T> cast(Iterable<T> iter) {
        return (Collection<T>) iter;
    }

}
