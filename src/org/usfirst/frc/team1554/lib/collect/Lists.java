package org.usfirst.frc.team1554.lib.collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.usfirst.frc.team1554.lib.util.Preconditions;

/**
 * Utilities for Instantiating and Manipulating Lists
 * 
 * @author Matthew
 */
public final class Lists {

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	@SafeVarargs
	public static <E> ArrayList<E> newArrayList(E... initial) {
		Preconditions.checkNotNull(initial);
		final ArrayList<E> list = new ArrayList<E>(initial.length);
		Collections.addAll(list, initial);

		return list;
	}

	public static <E> ArrayList<E> newArrayList(Iterable<E> elements) {
		Preconditions.checkNotNull(elements);

		return (elements instanceof Collection) ? new ArrayList<E>(cast(elements)) : newArrayList(elements.iterator());
	}

	public static <E> ArrayList<E> newArrayList(Iterator<E> elements) {
		Preconditions.checkNotNull(elements);

		final ArrayList<E> list = new ArrayList<E>();
		CollectionsUtil.addAll(list, elements);

		return list;
	}

	public static <E> ArrayList<E> newArrayListWithCapacity(int capacity) {
		return new ArrayList<E>(capacity);
	}

	public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimation) {
		return new ArrayList<E>(estimation);
	}

	private static <T> Collection<T> cast(Iterable<T> iter) {
		return (Collection<T>) iter;
	}

}