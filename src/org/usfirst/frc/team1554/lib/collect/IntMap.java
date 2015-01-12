package org.usfirst.frc.team1554.lib.collect;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.usfirst.frc.team1554.lib.util.MathUtils;

// TODO Documentation
/**
 * Implementation of Cuckoo Hash Map with a Stash to store problematic hashes. This
 * is inspired by similar implementations. <br />
 * <br />
 * Runs efficiently while being able to use primitive int keys.
 * 
 * @author Matthew
 *
 * @param <V>
 */
public class IntMap<V> {

	private static final int PRIME2 = 0xb4b82e39;
	private static final int PRIME3 = 0xced1c241;
	private static final int EMPTY = 0;

	public int size;

	int[] keyTable;
	V[] valueTable;
	int capacity, stashSize;
	V zeroValue;
	boolean hasZeroValue;

	private float loadFactor;
	private int hashShift, threshold, mask;
	private int stashCapacity;
	private int pushIterations;

	private Entries<V> entries1, entries2;
	private Values<V> values1, values2;
	private Keys keys1, keys2;

	public IntMap() {
		this(32, 0.8f);
	}

	public IntMap(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	@SuppressWarnings("unchecked")
	public IntMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("Initial Capacity must be >= 0: " + initialCapacity);
		if (initialCapacity > (1 << 30)) throw new IllegalArgumentException("Initial Capcity is too large!: " + initialCapacity);

		this.capacity = MathUtils.nextPowerOfTwo(initialCapacity);

		if (loadFactor <= 0) throw new IllegalArgumentException("Load Factor Can't be <= 0!: " + loadFactor);
		this.loadFactor = loadFactor;

		this.threshold = (int) (this.capacity * loadFactor);
		this.mask = this.capacity - 1;
		this.hashShift = 31 - Integer.numberOfTrailingZeros(this.capacity);
		this.stashCapacity = Math.max(3, (int) Math.ceil(Math.log(this.capacity)) * 2);
		this.pushIterations = Math.max(Math.min(this.capacity, 8), (int) Math.sqrt(this.capacity) / 8);

		this.keyTable = new int[this.capacity + this.stashCapacity];
		this.valueTable = (V[]) new Object[this.keyTable.length];
	}

	public IntMap(IntMap<? extends V> map) {
		this(map.capacity, map.loadFactor);
		this.stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, this.keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, this.valueTable, 0, map.valueTable.length);
		this.size = map.size;
		this.zeroValue = map.zeroValue;
		this.hasZeroValue = map.hasZeroValue;
	}

	public V put(int key, V value) {
		if (key == 0) {
			final V oldValue = this.zeroValue;
			this.zeroValue = value;
			if (!this.hasZeroValue) {
				this.hasZeroValue = true;
				this.size++;
			}
			return oldValue;
		}

		final int[] keyTable = this.keyTable;

		// Check Existing Key Hashes
		final int i1 = key & this.mask;
		final int k1 = keyTable[i1];
		if (k1 == key) {
			final V oldValue = this.valueTable[i1];
			this.valueTable[i1] = value;
			return oldValue;
		}

		final int i2 = hash2(key);
		final int k2 = keyTable[i2];
		if (k2 == key) {
			final V oldValue = this.valueTable[i2];
			this.valueTable[i2] = value;
			return oldValue;
		}

		final int i3 = hash3(key);
		final int k3 = keyTable[i3];
		if (k3 == key) {
			final V oldValue = this.valueTable[i3];
			this.valueTable[i3] = value;
			return oldValue;
		}

		// Update Stash
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++) {
			if (keyTable[i] == key) {
				final V oldValue = this.valueTable[i];
				this.valueTable[i] = value;
				return oldValue;
			}
		}

		// Empty Bucket
		if (k1 == EMPTY) {
			keyTable[i1] = key;
			this.valueTable[i1] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return null;
		}

		if (k2 == EMPTY) {
			keyTable[i2] = key;
			this.valueTable[i2] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return null;
		}

		if (k3 == EMPTY) {
			keyTable[i3] = key;
			this.valueTable[i3] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return null;
		}

		push(key, value, i1, k1, i2, k2, i3, k3);
		return null;
	}

	public void putAll(IntMap<V> map) {
		for (final Entry<V> entry : map.entries()) {
			put(entry.key, entry.value);
		}
	}

	public V get(int key, V defaultVal) {
		if (key == 0) {
			if (!this.hasZeroValue) return defaultVal;
			return this.zeroValue;
		}

		int i = key & this.mask;
		if (this.keyTable[i] != key) {
			i = hash2(key);
			if (this.keyTable[i] != key) {
				i = hash3(key);
				if (this.keyTable[i] != key) return getStash(key, defaultVal);
			}
		}

		return this.valueTable[i];
	}

	public V remove(int key) {
		if (key == 0) {
			if (!this.hasZeroValue) return null;

			final V oldValue = this.zeroValue;
			this.zeroValue = null;
			this.hasZeroValue = false;
			this.size--;
			return oldValue;
		}

		int i = key & this.mask;
		if (this.keyTable[i] == key) {
			this.keyTable[i] = EMPTY;
			final V oldValue = this.valueTable[i];
			this.valueTable[i] = null;
			this.size--;
			return oldValue;
		}

		i = hash2(key);
		if (this.keyTable[i] == key) {
			this.keyTable[i] = EMPTY;
			final V oldValue = this.valueTable[i];
			this.valueTable[i] = null;
			this.size--;
			return oldValue;
		}

		i = hash3(key);
		if (this.keyTable[i] == key) {
			this.keyTable[i] = EMPTY;
			final V oldValue = this.valueTable[i];
			this.valueTable[i] = null;
			this.size--;
			return oldValue;
		}

		return removeStash(key);
	}

	public void shrink(int maxCap) {
		// Negative Capacity?
		if (maxCap < 0) throw new IllegalArgumentException("Capacity < 0: " + maxCap);

		// We can't shirnk less than our size... Madness
		if (this.size > maxCap) {
			maxCap = this.size;
		}

		// Why shrink if we are already smaller?
		if (this.capacity <= maxCap) return;

		maxCap = MathUtils.nextPowerOfTwo(maxCap);
		resize(maxCap);
	}

	public void clear(int maxCap) {
		if (this.capacity <= maxCap) {
			clear();
			return;
		}

		this.zeroValue = null;
		this.hasZeroValue = false;
		this.size = 0;
		resize(maxCap);
	}

	public void clear() {
		if (this.size == 0) return;

		final int[] kt = this.keyTable;
		final V[] vt = this.valueTable;
		for (int i = this.capacity + this.stashSize; i > 0; i--) {
			kt[i] = EMPTY;
			vt[i] = null;
		}

		this.size = 0;
		this.stashSize = 0;
		this.zeroValue = null;
		this.hasZeroValue = false;
	}

	public boolean containsValue(Object val, boolean identity) {
		final V[] vt = this.valueTable;

		if (val == null) {
			if (this.hasZeroValue && (this.zeroValue == null)) return true;

			final int[] kt = this.keyTable;
			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if ((kt[i] != EMPTY) && (vt[i] == null)) return true;
		} else if (identity) {
			if (val == this.zeroValue) return true;

			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if (vt[i] == val) return true;
		} else {
			if (this.hasZeroValue && val.equals(this.zeroValue)) return true;

			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if (val.equals(vt[i])) return true;
		}

		return false;
	}

	public boolean containsKey(int key) {
		if (key == 0) return this.hasZeroValue;

		final int[] kt = this.keyTable;
		int i = key & this.mask;

		if (kt[i] != key) {
			i = hash2(key);
			if (kt[i] != key) {
				i = hash3(key);
				if (kt[i] != key) return containsKeyStash(key);
			}
		}

		return true;
	}

	public int findKey(Object val, boolean identity, int notFound) {
		final V[] vt = this.valueTable;
		final int[] kt = this.keyTable;

		if (val == null) {
			if (this.hasZeroValue && (this.zeroValue == null)) return 0;

			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if ((kt[i] != EMPTY) && (vt[i] == null)) return kt[i];
		} else if (identity) {
			if (val == this.zeroValue) return 0;

			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if (vt[i] == val) return kt[i];
		} else {
			if (this.hasZeroValue && val.equals(this.zeroValue)) return 0;

			for (int i = this.capacity + this.stashSize; i > 0; i--)
				if (val.equals(vt[i])) return kt[i];
		}

		return notFound;
	}

	public void ensureCapacity(int addCap) {
		final int reqSize = this.size + addCap;
		if (reqSize >= this.threshold) {
			resize(MathUtils.nextPowerOfTwo((int) (reqSize / this.loadFactor)));
		}
	}

	public Iterator<Entry<V>> iterator() {
		return entries();
	}

	public Entries<V> entries() {
		if (this.entries1 == null) {
			this.entries1 = new Entries<V>(this);
			this.entries2 = new Entries<V>(this);
		}

		if (!this.entries1.valid) {
			this.entries1.reset();
			this.entries1.valid = true;
			this.entries2.valid = false;
			return this.entries1;
		}

		this.entries2.reset();
		this.entries2.valid = true;
		this.entries1.valid = false;
		return this.entries2;
	}

	public Values<V> values() {
		if (this.values1 == null) {
			this.values1 = new Values<V>(this);
			this.values2 = new Values<V>(this);
		}

		if (!this.values1.valid) {
			this.values1.reset();
			this.values1.valid = true;
			this.values2.valid = false;
			return this.values1;
		}
		this.values2.reset();
		this.values2.valid = true;
		this.values1.valid = false;
		return this.values2;
	}

	@SuppressWarnings("unchecked")
	public Keys keys() {
		if (this.keys1 == null) {
			this.keys1 = new Keys(this);
			this.keys2 = new Keys(this);
		}
		if (!this.keys1.valid) {
			this.keys1.reset();
			this.keys1.valid = true;
			this.keys2.valid = false;
			return this.keys1;
		}

		this.keys2.reset();
		this.keys2.valid = true;
		this.keys1.valid = false;
		return this.keys2;
	}

	private void putResize(int key, V value) {
		if (key == 0) {
			this.zeroValue = value;
			this.hasZeroValue = true;
			return;
		}

		// Check for empty buckets.
		final int index1 = key & this.mask;
		final int key1 = this.keyTable[index1];
		if (key1 == EMPTY) {
			this.keyTable[index1] = key;
			this.valueTable[index1] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		final int index2 = hash2(key);
		final int key2 = this.keyTable[index2];
		if (key2 == EMPTY) {
			this.keyTable[index2] = key;
			this.valueTable[index2] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		final int index3 = hash3(key);
		final int key3 = this.keyTable[index3];
		if (key3 == EMPTY) {
			this.keyTable[index3] = key;
			this.valueTable[index3] = value;
			if (this.size++ >= this.threshold) {
				resize(this.capacity << 1);
			}
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	private void push(int insKey, V insVal, int i1, int k1, int i2, int k2, int i3, int k3) {
		final int[] kt = this.keyTable;
		final V[] vt = this.valueTable;

		int evictKey;
		V evictVal;
		int i = 0;
		final int pushIter = this.pushIterations;

		do {
			switch (MathUtils.random(2)) {
			case 0:
				evictKey = k1;
				evictVal = vt[i1];
				kt[i1] = insKey;
				vt[i1] = insVal;
				break;
			case 1:
				evictKey = k2;
				evictVal = vt[i2];
				kt[i2] = insKey;
				vt[i2] = insVal;
				break;
			default:
				evictKey = k3;
				evictVal = vt[i3];
				kt[i3] = insKey;
				vt[i3] = insVal;
				break;
			}

			i1 = evictKey & this.mask;
			k1 = kt[i1];
			if (k1 == EMPTY) {
				kt[i1] = evictKey;
				vt[i1] = evictVal;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			i2 = hash2(evictKey);
			k2 = this.keyTable[i2];
			if (k2 == EMPTY) {
				this.keyTable[i2] = evictKey;
				this.valueTable[i2] = evictVal;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			i3 = hash3(evictKey);
			k3 = this.keyTable[i3];
			if (k3 == EMPTY) {
				this.keyTable[i3] = evictKey;
				this.valueTable[i3] = evictVal;
				if (this.size++ >= this.threshold) {
					resize(this.capacity << 1);
				}
				return;
			}

			if (++i == pushIter) {
				break;
			}

			insKey = evictKey;
			insVal = evictVal;
		} while (true);

		putStash(evictKey, evictVal);
	}

	private void putStash(int key, V value) {
		if (this.stashSize == this.stashCapacity) {
			// Too Many Pushes, Resize
			resize(this.capacity << 1);
			put(key, value);
			return;
		}

		// Store in Stash
		final int index = this.capacity + this.stashSize;
		this.keyTable[index] = key;
		this.valueTable[index] = value;
		this.stashSize++;
		this.size++;
	}

	private V getStash(int key, V defaultVal) {
		final int[] kt = this.keyTable;
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
			if (kt[i] == key) return this.valueTable[i];

		return defaultVal;
	}

	private V removeStash(int key) {
		final int[] kt = this.keyTable;
		for (int i = this.capacity, n = i + this.stashSize; i < n; i++) {
			if (kt[i] == key) {
				final V oldValue = this.valueTable[i];
				removeStashIndex(i);
				this.size--;
				return oldValue;
			}
		}

		return null;
	}

	private void removeStashIndex(int index) {
		// Move Entry to Removed Location if Not Last

		this.stashSize--;
		final int last = this.capacity + this.stashSize;
		if (index < last) {
			this.keyTable[index] = this.keyTable[last];
			this.valueTable[index] = this.valueTable[last];
			this.valueTable[last] = null;
		} else {
			this.valueTable[index] = null;
		}
	}

	private boolean containsKeyStash(int key) {
		final int[] kt = this.keyTable;

		for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
			if (kt[i] == key) return true;

		return false;
	}

	@SuppressWarnings("unchecked")
	private void resize(int newSize) {
		final int prevEnd = this.capacity + this.stashSize;

		this.capacity = newSize;
		this.threshold = (int) (newSize * this.loadFactor);
		this.mask = newSize - 1;
		this.hashShift = 32 - Integer.numberOfTrailingZeros(newSize);
		this.stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		this.pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		final int[] prevKT = this.keyTable;
		final V[] prevVT = this.valueTable;

		this.keyTable = new int[newSize + this.stashCapacity];
		this.valueTable = (V[]) new Object[this.keyTable.length];

		final int oldSize = this.size;
		this.size = this.hasZeroValue ? 1 : 0;
		this.stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < prevEnd; i++) {
				final int key = prevKT[i];
				if (key != EMPTY) {
					putResize(key, prevVT[key]);
				}
			}
		}
	}

	private int hash2(int h) {
		h *= PRIME2;
		return (h ^ (h >>> this.hashShift)) & this.mask;
	}

	private int hash3(int h) {
		h *= PRIME3;
		return (h ^ (h >>> this.hashShift)) & this.mask;
	}

	@Override
	public String toString() {
		if (this.size == 0) return "[]";

		final StringBuilder sb = new StringBuilder(32);
		sb.append('[');
		final int[] kt = this.keyTable;
		final V[] vt = this.valueTable;
		int len = kt.length;

		if (this.hasZeroValue) {
			sb.append("0=").append(this.zeroValue);
		} else {
			while (len-- > 0) {
				final int key = kt[len];
				if (key == EMPTY) {
					continue;
				}

				sb.append(key).append('=').append(vt[len]);
				break;
			}
		}

		while (len-- > 0) {
			final int key = kt[len];
			if (key == EMPTY) {
				continue;
			}
			sb.append(", ").append(key).append('=').append(vt[len]);
		}

		sb.append(']');
		return sb.toString();
	}

	public static class Entry<V> {
		public int key;
		public V value;

		@Override
		public String toString() {
			return this.key + "=" + String.valueOf(this.value);
		}

	}

	private static class MapIterator<V> {
		protected static final int INDEX_ILLEGAL = -2;
		protected static final int INDEX_ZERO = -1;

		public boolean hasNext;

		protected final IntMap<V> map;
		protected int nextIndex, curIndex;
		protected boolean valid = true;

		public MapIterator(IntMap<V> map) {
			this.map = map;
			reset();
		}

		public void reset() {
			this.curIndex = INDEX_ILLEGAL;
			this.nextIndex = INDEX_ZERO;
			if (this.map.hasZeroValue) {
				this.hasNext = true;
			} else {
				findNextIndex();
			}
		}

		protected void findNextIndex() {
			this.hasNext = false;
			final int[] kt = this.map.keyTable;
			for (final int n = this.map.capacity + this.map.stashSize; ++this.nextIndex < n;) {
				if (kt[this.nextIndex] != EMPTY) {
					this.hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if ((this.curIndex == INDEX_ZERO) && this.map.hasZeroValue) {
				this.map.zeroValue = null;
				this.map.hasZeroValue = false;
			} else if (this.curIndex < 0)
				throw new IllegalStateException("next must be positive");
			else if (this.curIndex >= this.map.capacity) {
				this.map.removeStashIndex(this.curIndex);
				this.nextIndex = this.curIndex - 1;
				findNextIndex();
			} else {
				this.map.keyTable[this.curIndex] = EMPTY;
				this.map.valueTable[this.curIndex] = null;
			}

			this.curIndex = INDEX_ILLEGAL;
			this.map.size--;
		}
	}

	private static class Entries<V> extends MapIterator<V> implements Iterable<Entry<V>>, Iterator<Entry<V>> {

		private final Entry<V> entry = new Entry<V>();

		public Entries(IntMap<V> map) {
			super(map);
		}

		@Override
		public boolean hasNext() {
			if (!this.valid) throw new RuntimeException("#iterator() can not be used nested");
			return this.hasNext;
		}

		@Override
		public Entry<V> next() {
			if (!this.hasNext) throw new NoSuchElementException();
			if (!this.valid) throw new RuntimeException("#iterator() can not be used nested");

			if (this.nextIndex == INDEX_ZERO) {
				this.entry.key = 0;
				this.entry.value = this.map.zeroValue;
			} else {
				this.entry.key = this.map.keyTable[this.nextIndex];
				this.entry.value = this.map.valueTable[this.nextIndex];
			}
			this.curIndex = this.nextIndex;
			findNextIndex();
			return this.entry;
		}

		@Override
		public Iterator<Entry<V>> iterator() {
			return this;
		}
	}

	public static class Values<V> extends MapIterator<V> implements Iterable<V>, Iterator<V> {

		public Values(IntMap<V> map) {
			super(map);
		}

		@Override
		public boolean hasNext() {
			if (!this.valid) throw new RuntimeException("#iterator() can not be used nested");

			return this.hasNext;
		}

		@Override
		public V next() {
			if (!this.hasNext) throw new NoSuchElementException();
			if (!this.valid) throw new RuntimeException("#iterator() can not be used nested");

			V value;
			if (this.nextIndex == INDEX_ZERO) {
				value = this.map.zeroValue;
			} else {
				value = this.map.valueTable[this.nextIndex];
			}
			this.curIndex = this.nextIndex;
			findNextIndex();
			return value;
		}

		@Override
		public Iterator<V> iterator() {
			return this;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static class Keys extends MapIterator {

		public Keys(IntMap map) {
			super(map);
		}

		public int next() {
			if (!this.hasNext) throw new NoSuchElementException();
			if (!this.valid) throw new RuntimeException("#iterator() can not be used nested");

			final int key = this.nextIndex == INDEX_ZERO ? 0 : this.map.keyTable[this.nextIndex];
			this.curIndex = this.nextIndex;
			findNextIndex();
			return key;
		}
	}

}
