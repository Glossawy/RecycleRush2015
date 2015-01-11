package org.usfirst.frc.team1554.lib.collect;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Utilities for Instantiating and Manipulating Sets
 * 
 * @author Matthew
 */
public final class Sets {

	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	@SafeVarargs
	public static <E> HashSet<E> newHashSet(E... elements) {
		final HashSet<E> set = newHashSetWithExpectedSize(elements.length);

		Collections.addAll(set, elements);
		return set;
	}

	public static <E> HashSet<E> newHashSet(Iterable<E> elements) {
		return (elements instanceof Collection) ? new HashSet<E>(CollectionsUtil.cast(elements)) : newHashSet(elements.iterator());
	}

	public static <E> HashSet<E> newHashSet(Iterator<E> iter) {
		final HashSet<E> set = newHashSet();
		CollectionsUtil.addAll(set, iter);
		return set;
	}

	public static <E> HashSet<E> newHashSetWithExpectedSize(int estimation) {
		return new HashSet<E>(CollectionsUtil.computeCapacity(estimation));
	}

	public static <E> TreeSet<E> newTreeSet() {
		return new TreeSet<E>();
	}

	@SafeVarargs
	public static <E> TreeSet<E> newTreeSet(E... initial) {
		final TreeSet<E> set = newTreeSet();

		Collections.addAll(set, initial);
		return set;
	}

	public static <E> TreeSet<E> newTreeSet(Iterable<E> elements) {
		if (elements instanceof Collection)
			return new TreeSet<E>(CollectionsUtil.cast(elements));
		else
			return newTreeSet(elements.iterator());
	}

	public static <E> TreeSet<E> newTreeSet(Iterator<E> iter) {
		final TreeSet<E> set = newTreeSet();

		CollectionsUtil.addAll(set, iter);
		return set;
	}

	public static <E> TreeSet<E> newTreeSet(Comparator<? super E> comparator) {
		return new TreeSet<E>(comparator);
	}

	public static BitSet newBitSet() {
		return new BitSet();
	}

	public static BitSet newBitSet(int nbits) {
		return new BitSet(nbits);
	}

	public static BitSet newBitSet(BitSet bitset) {
		final BitSet set = newBitSet();

		for (int i = 0; i < bitset.size(); i++) {
			set.set(i, bitset.get(i));
		}

		return set;
	}

	public static BitSet newBitSet(String bits) {
		final BitSet set = newBitSet(bits.length());

		for (int i = 0; i < bits.length(); i++)
			if (bits.charAt(i) == '1') {
				set.set(i);
			}

		return set;
	}

	public static BitSet newBitSetOf(int bits) {
		return newBitSet(Integer.toString(bits, 2));
	}

	public static BitSet newBitSetOf(long bits) {
		return newBitSet(Long.toString(bits, 2));
	}

}
