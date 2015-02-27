package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Utilities for Instantiating and Manipulating Lists
 *
 * @author Matthew
 */
public final class Lists {

    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    @SafeVarargs
    public static <E> ArrayList<E> newArrayList(E... initial) {
        Preconditions.checkNotNull(initial);
        final ArrayList<E> list = new ArrayList<>(initial.length);
        Collections.addAll(list, initial);

        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterable<E> elements) {
        Preconditions.checkNotNull(elements);

        return elements instanceof Collection ? new ArrayList<>(cast(elements)) : newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<E> elements) {
        Preconditions.checkNotNull(elements);

        final ArrayList<E> list = new ArrayList<>();
        CollectionsUtil.addAll(list, elements);

        return list;
    }

    public static <E> ArrayList<E> newArrayListWithCapacity(int capacity) {
        return new ArrayList<>(capacity);
    }

    public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimation) {
        return new ArrayList<>(estimation);
    }

    private static <T> Collection<T> cast(Iterable<T> iter) {
        return (Collection<T>) iter;
    }

}
