package org.usfirst.frc.team1554.lib.util;

import java.util.Iterator;

public interface Predicate<T> {

	boolean eval(T arg);

	public class PredicateIterator<T> implements Iterator<T> {

		Iterator<T> iterator;
		Predicate<T> predicate;
		boolean end = false;
		boolean peeked = false;
		T next = null;

		public PredicateIterator(Iterable<T> iter, Predicate<T> predicate) {
			this(iter.iterator(), predicate);
		}

		public PredicateIterator(Iterator<T> iter, Predicate<T> predicate) {
			set(iter, predicate);
		}

		public void set(Iterator<T> iter, Predicate<T> predicate) {
			this.iterator = iter;
			this.predicate = predicate;
		}

		@Override
		public boolean hasNext() {
			if (end) return false;
			if (next != null) return true;

			peeked = true;
			while (iterator.hasNext()) {
				final T n = iterator.next();
				if (predicate.eval(n)) {
					next = n;
					return true;
				}
			}
			end = true;
			return false;
		}

		@Override
		public T next() {
			if ((next == null) && !hasNext()) return null;

			final T res = next;
			next = null;
			peeked = false;
			return res;
		}

		@Override
		public void remove() {
			if (peeked) throw new RuntimeException("Cannot Remove between hasNext() and next()!");

			iterator.remove();
		}
	}

	public static class PredicateIterable<T> implements Iterable<T> {
		Iterable<T> iterable;
		Predicate<T> predicate;
		PredicateIterator<T> iterator;

		public PredicateIterable(Iterable<T> iter, Predicate<T> predicate) {
			set(iter, predicate);
		}

		public void set(Iterable<T> iter, Predicate<T> predicate) {
			this.iterable = iter;
			this.predicate = predicate;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator == null) {
				iterator = new PredicateIterator<>(iterable.iterator(), predicate);
			} else {
				iterator.set(iterable.iterator(), predicate);
			}

			return iterator;
		}
	}

}
