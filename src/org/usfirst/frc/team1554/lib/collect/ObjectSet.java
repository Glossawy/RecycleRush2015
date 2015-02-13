package org.usfirst.frc.team1554.lib.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.util.Preconditions;

public class ObjectSet<T> implements Iterable<T> {

	private static final int PRIME1 = 0xB4B82E39;
	private static final int PRIME2 = 0xCED1C241;

	@SafeVarargs
	public static <T> ObjectSet<T> with(T... array) {
		final ObjectSet<T> set = new ObjectSet<>();
		set.addAll(array);
		return set;
	}

	public int size;

	T[] keyTable;
	int capacity, stashSize;

	private float loadFactor;
	private int hashShift, mask, threshold;
	private int stashCapacity;
	private int pushIterations;

	private ObjectSetIterator<T> iterOne, iterTwo;

	/**
	 * Creates a new set with an initial capacity of 32 and a load factor of 0.8. This set will hold 25 items before growing the backing table.
	 */
	public ObjectSet() {
		this(32, 0.8f);
	}

	/**
	 * Creates a new set with a load factor of 0.8. This set will hold initialCapacity * 0.8 items before growing the backing table.
	 */
	public ObjectSet(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/**
	 * Creates a new set with the specified initial capacity and load factor. This set will hold initialCapacity * loadFactor items before growing the backing table.
	 */
	@SuppressWarnings("unchecked")
	public ObjectSet(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		if (initialCapacity > 1 << 30)
			throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		this.capacity = MathUtils.nextPowerOfTwo(initialCapacity);

		if (loadFactor <= 0)
			throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
		this.loadFactor = loadFactor;

		this.threshold = (int) (this.capacity * loadFactor);
		this.mask = this.capacity - 1;
		this.hashShift = 31 - Integer.numberOfTrailingZeros(this.capacity);
		this.stashCapacity = Math.max(3, (int) Math.ceil(Math.log(this.capacity)) * 2);
		this.pushIterations = Math.max(Math.min(this.capacity, 8), (int) Math.sqrt(this.capacity) / 8);

		this.keyTable = (T[]) new Object[this.capacity + this.stashCapacity];
	}

	/** Creates a new set identical to the specified set. */
	public ObjectSet(ObjectSet<? extends T> set) {
		this(set.capacity, set.loadFactor);
		this.stashSize = set.stashSize;
		System.arraycopy(set.keyTable, 0, this.keyTable, 0, set.keyTable.length);
		this.size = set.size;
	}

	/**
	 * Returns true if the key was not already in the set. If this set already contains the key, the call leaves the set unchanged and returns false.
	 */
	public boolean add(T key) {
		Preconditions.checkNotNull(key, "Key Cannot Be Null!");

		// Check for existing keys.
		final int hashCode = key.hashCode();
		final int index1 = hashCode & this.mask;
		final T key1 = this.keyTable[index1];
		if (key.equals(key1))
			return false;

		final int index2 = hash(hashCode);
		final T key2 = this.keyTable[index2];
		if (key.equals(key2))
			return false;

		final int index3 = hash2(hashCode);
		final T key3 = this.keyTable[index3];
		if (key.equals(key3))
			return false;

		// Find key in the stash.
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
			if (key.equals(this.keyTable[i]))
				return false;

		// Check for empty buckets.
		if (key1 == null) {
			this.keyTable[index1] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return true;
		}

		if (key2 == null) {
			this.keyTable[index2] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return true;
		}

		if (key3 == null) {
			this.keyTable[index3] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return true;
		}

		push(key, index1, key1, index2, key2, index3, key3);
		return true;
	}

	public void addAll(Array<? extends T> array) {
		addAll(array, 0, array.size);
	}

	public void addAll(Array<? extends T> array, int offset, int length) {
		if (offset + length > array.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + array.size);
		addAll(array.items, offset, length);
	}

	public void addAll(@SuppressWarnings("unchecked") T... array) {
		addAll(array, 0, array.length);
	}

	public void addAll(T[] array, int offset, int length) {
		ensureCapacity(length);
		for (int i = offset, n = i + length; i < n; i++) {
			add(array[i]);
		}
	}

	public void addAll(ObjectSet<T> set) {
		ensureCapacity(set.size);
		for (final T key : set) {
			add(key);
		}
	}

	/** Skips checks for existing keys. */
	private void addResize(T key) {
		// Check for empty buckets.
		final int hashCode = key.hashCode();
		final int index1 = hashCode & this.mask;
		final T key1 = this.keyTable[index1];
		if (key1 == null) {
			this.keyTable[index1] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		final int index2 = hash(hashCode);
		final T key2 = this.keyTable[index2];
		if (key2 == null) {
			this.keyTable[index2] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		final int index3 = hash2(hashCode);
		final T key3 = this.keyTable[index3];
		if (key3 == null) {
			this.keyTable[index3] = key;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		push(key, index1, key1, index2, key2, index3, key3);
	}

	private void push(T insertKey, int index1, T key1, int index2, T key2, int index3, T key3) {
		// Push keys until an empty bucket is found.
		T evictedKey;
		int i = 0;
		final int pushIterations = this.pushIterations;
		do {
			// Replace the key and value for one of the hashes.
			switch (MathUtils.random(2)) {
			case 0:
				evictedKey = key1;
				this.keyTable[index1] = insertKey;
				break;
			case 1:
				evictedKey = key2;
				this.keyTable[index2] = insertKey;
				break;
			default:
				evictedKey = key3;
				this.keyTable[index3] = insertKey;
				break;
			}

			// If the evicted key hashes to an empty bucket, put it there and stop.
			final int hashCode = evictedKey.hashCode();
			index1 = hashCode & this.mask;
			key1 = this.keyTable[index1];
			if (key1 == null) {
				this.keyTable[index1] = evictedKey;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			index2 = hash(hashCode);
			key2 = this.keyTable[index2];
			if (key2 == null) {
				this.keyTable[index2] = evictedKey;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			index3 = hash2(hashCode);
			key3 = this.keyTable[index3];
			if (key3 == null) {
				this.keyTable[index3] = evictedKey;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			if (++i == pushIterations) {
				break;
			}

			insertKey = evictedKey;
		} while (true);

		addStash(evictedKey);
	}

	private void addStash(T key) {
		if (this.stashSize == this.stashCapacity) {
			// Too many pushes occurred and the stash is full, increase the table
			// size.
			resize(this.capacity << 1);
			add(key);
			return;
		}
		// Store key in the stash.
		final int index = this.capacity + this.stashSize;
		this.keyTable[index] = key;
		this.stashSize++;
		this.size++;
	}

	/** Returns true if the key was removed. */
	public boolean remove(T key) {
		final int hashCode = key.hashCode();
		int index = hashCode & this.mask;
		if (key.equals(this.keyTable[index])) {
			this.keyTable[index] = null;
			this.size--;
			return true;
		}

		index = hash(hashCode);
		if (key.equals(this.keyTable[index])) {
			this.keyTable[index] = null;
			this.size--;
			return true;
		}

		index = hash2(hashCode);
		if (key.equals(this.keyTable[index])) {
			this.keyTable[index] = null;
			this.size--;
			return true;
		}

		return removeStash(key);
	}

	boolean removeStash(T key) {
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++) {
			if (key.equals(this.keyTable[i])) {
				removeStashIndex(i);
				this.size--;
				return true;
			}
		}
		return false;
	}

	void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed
		// location.
		this.stashSize--;
		final int lastIndex = this.capacity + this.stashSize;
		if (index < lastIndex) {
			this.keyTable[index] = this.keyTable[lastIndex];
		}
	}

	/**
	 * Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less, nothing is done. If the map contains more items than the specified capacity, the next highest power of two capacity is used instead.
	 */
	public void shrink(int maximumCapacity) {
		Preconditions.checkExpression(maximumCapacity >= 0, "maximumCapacity must be >= 0: " + maximumCapacity);

		if (this.size > maximumCapacity) {
			maximumCapacity = this.size;
		}

		if (this.capacity <= maximumCapacity)
			return;

		maximumCapacity = MathUtils.nextPowerOfTwo(maximumCapacity);
		resize(maximumCapacity);
	}

	/**
	 * Clears the map and reduces the size of the backing arrays to be the specified capacity if they are larger.
	 */
	public void clear(int maximumCapacity) {
		if (this.capacity <= maximumCapacity) {
			clear();
			return;
		}
		this.size = 0;
		resize(maximumCapacity);
	}

	public void clear() {
		if (this.size == 0)
			return;

		for (int i = this.capacity + this.stashSize; i-- > 0;) {
			this.keyTable[i] = null;
		}
		this.size = 0;
		this.stashSize = 0;
	}

	public boolean contains(T key) {
		final int hashCode = key.hashCode();
		int index = hashCode & this.mask;
		if (!key.equals(this.keyTable[index])) {
			index = hash(hashCode);
			if (!key.equals(this.keyTable[index])) {
				index = hash2(hashCode);
				if (!key.equals(this.keyTable[index]))
					return containsKeyStash(key);
			}
		}
		return true;
	}

	private boolean containsKeyStash(T key) {
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
			if (key.equals(this.keyTable[i]))
				return true;
		return false;
	}

	public T first() {
		for (int i = 0, n = this.capacity + this.stashSize; i < n; i++)
			if (this.keyTable[i] != null)
				return this.keyTable[i];
		throw new IllegalStateException("IntSet is empty.");
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many items to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additionalCapacity) {
		final int required = this.size + additionalCapacity;
		if (required >= this.threshold) {
			resize(MathUtils.nextPowerOfTwo((int) (required / this.loadFactor)));
		}
	}

	@SuppressWarnings("unchecked")
	private void resize(int newSize) {
		final int oldEndIndex = this.capacity + this.stashSize;

		this.capacity = newSize;
		this.threshold = (int) (newSize * this.loadFactor);
		this.mask = newSize - 1;
		this.hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		this.stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		this.pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		final T[] oldKeyTable = this.keyTable;

		this.keyTable = (T[]) new Object[newSize + this.stashCapacity];

		final int oldSize = this.size;
		this.size = 0;
		this.stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				final T key = oldKeyTable[i];
				if (key != null) {
					addResize(key);
				}
			}
		}
	}

	private int hash(int h) {
		h *= PRIME1;
		return (h ^ h >>> this.hashShift) & this.mask;
	}

	private int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> this.hashShift) & this.mask;
	}

	@Override
	public String toString() {
		return '{' + toString(", ") + '}';
	}

	public String toString(String separator) {
		if (this.size == 0)
			return "";
		final StringBuilder buffer = new StringBuilder(32);
		int i = this.keyTable.length;

		while (i-- > 0) {
			final T key = this.keyTable[i];
			if (key == null) {
				continue;
			}
			buffer.append(key);
			break;
		}
		while (i-- > 0) {
			final T key = this.keyTable[i];
			if (key == null) {
				continue;
			}
			buffer.append(separator);
			buffer.append(key);
		}
		return buffer.toString();
	}

	/**
	 * Returns an iterator for the keys in the set. Remove is supported. Note that the same iterator instance is returned each time this method is called. Use the {@link ObjectSetIterator} constructor for nested or multithreaded iteration.
	 */
	@Override
	public ObjectSetIterator<T> iterator() {
		if (this.iterOne == null) {
			this.iterOne = new ObjectSetIterator<T>(this);
			this.iterTwo = new ObjectSetIterator<T>(this);
		}
		if (!this.iterOne.valid) {
			this.iterOne.reset();
			this.iterOne.valid = true;
			this.iterTwo.valid = false;
			return this.iterOne;
		}
		this.iterTwo.reset();
		this.iterTwo.valid = true;
		this.iterOne.valid = false;
		return this.iterTwo;
	}

	public static class ObjectSetIterator<K> implements Iterable<K>, Iterator<K> {
		public boolean hasNext;

		final ObjectSet<K> set;
		int nextIndex, currentIndex;
		boolean valid = true;

		public ObjectSetIterator(ObjectSet<K> set) {
			this.set = set;
			reset();
		}

		public void reset() {
			this.currentIndex = -1;
			this.nextIndex = -1;
			findNextIndex();
		}

		void findNextIndex() {
			this.hasNext = false;
			final K[] keyTable = this.set.keyTable;
			for (final int n = this.set.capacity + this.set.stashSize; ++this.nextIndex < n;) {
				if (keyTable[this.nextIndex] != null) {
					this.hasNext = true;
					break;
				}
			}
		}

		@Override
		public void remove() {
			if (this.currentIndex < 0)
				throw new IllegalStateException("next must be called before remove.");
			if (this.currentIndex >= this.set.capacity) {
				this.set.removeStashIndex(this.currentIndex);
				this.nextIndex = this.currentIndex - 1;
				findNextIndex();
			} else {
				this.set.keyTable[this.currentIndex] = null;
			}
			this.currentIndex = -1;
			this.set.size--;
		}

		@Override
		public boolean hasNext() {
			Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");
			return this.hasNext;
		}

		@Override
		public K next() {
			if (!this.hasNext)
				throw new NoSuchElementException();
			Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

			final K key = this.set.keyTable[this.nextIndex];
			this.currentIndex = this.nextIndex;
			findNextIndex();
			return key;
		}

		@Override
		public ObjectSetIterator<K> iterator() {
			return this;
		}

		/** Adds the remaining values to the array. */
		public Array<K> toArray(Array<K> array) {
			while (this.hasNext) {
				array.add(next());
			}
			return array;
		}

		/** Returns a new array containing the remaining values. */
		public Array<K> toArray() {
			return toArray(new Array<K>(true, this.set.size));
		}
	}

}
