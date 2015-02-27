package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.math.MathUtils;
import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ObjectMap<K, V> implements Iterable<ObjectMap.Entry<K, V>> {

    private static final int PRIME1 = 0xB4B82E39;
    private static final int PRIME2 = 0xCED1C241;

    public int size;

    K[] keyTable;
    V[] valueTable;
    int capacity, stashSize;

    private float loadFactor;
    private int hashShift, mask, threshold;
    private int stashCapacity;
    private int pushIterations;

    private Entries<K, V> entries1, entries2;
    private Values<V> values1, values2;
    private Keys<K> keys1, keys2;

    /**
     * Creates a new map with an initial capacity of 32 and a load
     * factor of 0.8. This map will hold 25 items before growing the
     * backing table.
     */
    public ObjectMap() {
        this(32, 0.8f);
    }

    /**
     * Creates a new map with a load factor of 0.8. This map will hold
     * initialCapacity * 0.8 items before growing the backing table.
     */
    public ObjectMap(int initialCapacity) {
        this(initialCapacity, 0.8f);
    }

    /**
     * Creates a new map with the specified initial capacity and load
     * factor. This map will hold initialCapacity * loadFactor items
     * before growing the backing table.
     */
    @SuppressWarnings("unchecked")
    public ObjectMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
        if (initialCapacity > (1 << 30))
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

        this.keyTable = (K[]) new Object[this.capacity + this.stashCapacity];
        this.valueTable = (V[]) new Object[this.keyTable.length];
    }

    /**
     * Creates a new map identical to the specified map.
     */
    public ObjectMap(ObjectMap<? extends K, ? extends V> map) {
        this(map.capacity, map.loadFactor);
        this.stashSize = map.stashSize;
        System.arraycopy(map.keyTable, 0, this.keyTable, 0, map.keyTable.length);
        System.arraycopy(map.valueTable, 0, this.valueTable, 0, map.valueTable.length);
        this.size = map.size;
    }

    /**
     * Returns the old value associated with the specified key, or
     * null.
     */
    public V put(K key, V value) {
        if (key == null)
            throw new IllegalArgumentException("key cannot be null.");
        return put_internal(key, value);
    }

    private V put_internal(K key, V value) {
        final K[] keyTable = this.keyTable;

        // Check for existing keys.
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final K key1 = keyTable[index1];
        if (key.equals(key1)) {
            final V oldValue = this.valueTable[index1];
            this.valueTable[index1] = value;
            return oldValue;
        }

        final int index2 = hash2(hashCode);
        final K key2 = keyTable[index2];
        if (key.equals(key2)) {
            final V oldValue = this.valueTable[index2];
            this.valueTable[index2] = value;
            return oldValue;
        }

        final int index3 = hash3(hashCode);
        final K key3 = keyTable[index3];
        if (key.equals(key3)) {
            final V oldValue = this.valueTable[index3];
            this.valueTable[index3] = value;
            return oldValue;
        }

        // Update key in the stash.
        for (int i = this.capacity, n = i + this.stashSize; i < n; i++) {
            if (key.equals(keyTable[i])) {
                final V oldValue = this.valueTable[i];
                this.valueTable[i] = value;
                return oldValue;
            }
        }

        // Check for empty buckets.
        if (key1 == null) {
            keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        }

        if (key2 == null) {
            keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        }

        if (key3 == null) {
            keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return null;
        }

        push(key, value, index1, key1, index2, key2, index3, key3);
        return null;
    }

    public void putAll(ObjectMap<K, V> map) {
        ensureCapacity(map.size);
        for (final Entry<K, V> entry : map) {
            put(entry.key, entry.value);
        }
    }

    /**
     * Skips checks for existing keys.
     */
    private void putResize(K key, V value) {
        // Check for empty buckets.
        final int hashCode = key.hashCode();
        final int index1 = hashCode & this.mask;
        final K key1 = this.keyTable[index1];
        if (key1 == null) {
            this.keyTable[index1] = key;
            this.valueTable[index1] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return;
        }

        final int index2 = hash2(hashCode);
        final K key2 = this.keyTable[index2];
        if (key2 == null) {
            this.keyTable[index2] = key;
            this.valueTable[index2] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return;
        }

        final int index3 = hash3(hashCode);
        final K key3 = this.keyTable[index3];
        if (key3 == null) {
            this.keyTable[index3] = key;
            this.valueTable[index3] = value;
            if (this.size++ >= this.threshold) {
                resize(this.capacity << 1);
            }
            return;
        }

        push(key, value, index1, key1, index2, key2, index3, key3);
    }

    private void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
        final K[] keyTable = this.keyTable;
        final V[] valueTable = this.valueTable;
        final int mask = this.mask;

        // Push keys until an empty bucket is found.
        K evictedKey;
        V evictedValue;
        int i = 0;
        final int pushIterations = this.pushIterations;
        do {
            // Replace the key and value for one of the hashes.
            switch (MathUtils.random(2)) {
                case 0:
                    evictedKey = key1;
                    evictedValue = valueTable[index1];
                    keyTable[index1] = insertKey;
                    valueTable[index1] = insertValue;
                    break;
                case 1:
                    evictedKey = key2;
                    evictedValue = valueTable[index2];
                    keyTable[index2] = insertKey;
                    valueTable[index2] = insertValue;
                    break;
                default:
                    evictedKey = key3;
                    evictedValue = valueTable[index3];
                    keyTable[index3] = insertKey;
                    valueTable[index3] = insertValue;
                    break;
            }

            // If the evicted key hashes to an empty bucket, put it there and stop.
            final int hashCode = evictedKey.hashCode();
            index1 = hashCode & mask;
            key1 = keyTable[index1];
            if (key1 == null) {
                keyTable[index1] = evictedKey;
                valueTable[index1] = evictedValue;
                if (this.size++ >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return;
            }

            index2 = hash2(hashCode);
            key2 = keyTable[index2];
            if (key2 == null) {
                keyTable[index2] = evictedKey;
                valueTable[index2] = evictedValue;
                if (this.size++ >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return;
            }

            index3 = hash3(hashCode);
            key3 = keyTable[index3];
            if (key3 == null) {
                keyTable[index3] = evictedKey;
                valueTable[index3] = evictedValue;
                if (this.size++ >= this.threshold) {
                    resize(this.capacity << 1);
                }
                return;
            }

            if (++i == pushIterations) {
                break;
            }

            insertKey = evictedKey;
            insertValue = evictedValue;
        } while (true);

        putStash(evictedKey, evictedValue);
    }

    private void putStash(K key, V value) {
        if (this.stashSize == this.stashCapacity) {
            // Too many pushes occurred and the stash is full, increase the table
            // size.
            resize(this.capacity << 1);
            put_internal(key, value);
            return;
        }
        // Store key in the stash.
        final int index = this.capacity + this.stashSize;
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        this.stashSize++;
        this.size++;
    }

    public V get(K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index]))
                    return getStash(key);
            }
        }
        return this.valueTable[index];
    }

    private V getStash(K key) {
        final K[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
            if (key.equals(keyTable[i])) return this.valueTable[i];
        return null;
    }

    /**
     * Returns the value for the specified key, or the default value
     * if the key is not in the map.
     */
    public V get(K key, V defaultValue) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index]))
                    return getStash(key, defaultValue);
            }
        }
        return this.valueTable[index];
    }

    private V getStash(K key, V defaultValue) {
        final K[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
            if (key.equals(keyTable[i])) return this.valueTable[i];
        return defaultValue;
    }

    public V remove(K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            this.size--;
            return oldValue;
        }

        index = hash2(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            this.size--;
            return oldValue;
        }

        index = hash3(hashCode);
        if (key.equals(this.keyTable[index])) {
            this.keyTable[index] = null;
            final V oldValue = this.valueTable[index];
            this.valueTable[index] = null;
            this.size--;
            return oldValue;
        }

        return removeStash(key);
    }

    V removeStash(K key) {
        final K[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; i++) {
            if (key.equals(keyTable[i])) {
                final V oldValue = this.valueTable[i];
                removeStashIndex(i);
                this.size--;
                return oldValue;
            }
        }
        return null;
    }

    void removeStashIndex(int index) {
        // If the removed location was not last, move the last tuple to the removed
        // location.
        this.stashSize--;
        final int lastIndex = this.capacity + this.stashSize;
        if (index < lastIndex) {
            this.keyTable[index] = this.keyTable[lastIndex];
            this.valueTable[index] = this.valueTable[lastIndex];
            this.valueTable[lastIndex] = null;
        } else {
            this.valueTable[index] = null;
        }
    }

    /**
     * Reduces the size of the backing arrays to be the specified
     * capacity or less. If the capacity is already less, nothing is
     * done. If the map contains more items than the specified
     * capacity, the next highest power of two capacity is used
     * instead.
     */
    public void shrink(int maximumCapacity) {
        if (maximumCapacity < 0)
            throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
        if (this.size > maximumCapacity) {
            maximumCapacity = this.size;
        }
        if (this.capacity <= maximumCapacity) return;
        maximumCapacity = MathUtils.nextPowerOfTwo(maximumCapacity);
        resize(maximumCapacity);
    }

    /**
     * Clears the map and reduces the size of the backing arrays to be
     * the specified capacity if they are larger.
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
        if (this.size == 0) return;
        final K[] keyTable = this.keyTable;
        final V[] valueTable = this.valueTable;
        for (int i = this.capacity + this.stashSize; i-- > 0; ) {
            keyTable[i] = null;
            valueTable[i] = null;
        }
        this.size = 0;
        this.stashSize = 0;
    }

    /**
     * Returns true if the specified value is in the map. Note this
     * traverses the entire map and compares every value, which may be
     * an expensive operation.
     *
     * @param identity If true, uses == to compare the specified value
     *                 with values in the map. If false, uses {@link
     *                 #equals(Object)}.
     */
    public boolean containsValue(Object value, boolean identity) {
        final V[] valueTable = this.valueTable;
        if (value == null) {
            final K[] keyTable = this.keyTable;
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if ((keyTable[i] != null) && (valueTable[i] == null))
                    return true;
        } else if (identity) {
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if (valueTable[i] == value) return true;
        } else {
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if (value.equals(valueTable[i])) return true;
        }
        return false;
    }

    public boolean containsKey(K key) {
        final int hashCode = key.hashCode();
        int index = hashCode & this.mask;
        if (!key.equals(this.keyTable[index])) {
            index = hash2(hashCode);
            if (!key.equals(this.keyTable[index])) {
                index = hash3(hashCode);
                if (!key.equals(this.keyTable[index]))
                    return containsKeyStash(key);
            }
        }
        return true;
    }

    private boolean containsKeyStash(K key) {
        final K[] keyTable = this.keyTable;
        for (int i = this.capacity, n = i + this.stashSize; i < n; i++)
            if (key.equals(keyTable[i])) return true;
        return false;
    }

    /**
     * Returns the key for the specified value, or null if it is not
     * in the map. Note this traverses the entire map and compares
     * every value, which may be an expensive operation.
     *
     * @param identity If true, uses == to compare the specified value
     *                 with values in the map. If false, uses {@link
     *                 #equals(Object)}.
     */
    public K findKey(Object value, boolean identity) {
        final V[] valueTable = this.valueTable;
        if (value == null) {
            final K[] keyTable = this.keyTable;
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if ((keyTable[i] != null) && (valueTable[i] == null))
                    return keyTable[i];
        } else if (identity) {
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if (valueTable[i] == value) return this.keyTable[i];
        } else {
            for (int i = this.capacity + this.stashSize; i-- > 0; )
                if (value.equals(valueTable[i]))
                    return this.keyTable[i];
        }
        return null;
    }

    /**
     * Increases the size of the backing array to accommodate the
     * specified number of additional items. Useful before adding many
     * items to avoid multiple backing array resizes.
     */
    public void ensureCapacity(int additionalCapacity) {
        final int sizeNeeded = this.size + additionalCapacity;
        if (sizeNeeded >= this.threshold) {
            resize(MathUtils.nextPowerOfTwo((int) (sizeNeeded / this.loadFactor)));
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

        final K[] oldKeyTable = this.keyTable;
        final V[] oldValueTable = this.valueTable;

        this.keyTable = (K[]) new Object[newSize + this.stashCapacity];
        this.valueTable = (V[]) new Object[newSize + this.stashCapacity];

        final int oldSize = this.size;
        this.size = 0;
        this.stashSize = 0;
        if (oldSize > 0) {
            for (int i = 0; i < oldEndIndex; i++) {
                final K key = oldKeyTable[i];
                if (key != null) {
                    putResize(key, oldValueTable[i]);
                }
            }
        }
    }

    private int hash2(int h) {
        h *= PRIME1;
        return (h ^ (h >>> this.hashShift)) & this.mask;
    }

    private int hash3(int h) {
        h *= PRIME2;
        return (h ^ (h >>> this.hashShift)) & this.mask;
    }

    public String toString(String separator) {
        return toString(separator, false);
    }

    @Override
    public String toString() {
        return toString(", ", true);
    }

    private String toString(String separator, boolean braces) {
        if (this.size == 0) return braces ? "{}" : "";
        final StringBuilder buffer = new StringBuilder(32);
        if (braces) {
            buffer.append('{');
        }
        final K[] keyTable = this.keyTable;
        final V[] valueTable = this.valueTable;
        int i = keyTable.length;
        while (i-- > 0) {
            final K key = keyTable[i];
            if (key == null) {
                continue;
            }
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
            break;
        }
        while (i-- > 0) {
            final K key = keyTable[i];
            if (key == null) {
                continue;
            }
            buffer.append(separator);
            buffer.append(key);
            buffer.append('=');
            buffer.append(valueTable[i]);
        }
        if (braces) {
            buffer.append('}');
        }
        return buffer.toString();
    }

    @Override
    public Entries<K, V> iterator() {
        return entries();
    }

    /**
     * Returns an iterator for the entries in the map. Remove is
     * supported. Note that the same iterator instance is returned
     * each time this method is called. Use the {@link Entries}
     * constructor for nested or multithreaded iteration.
     */
    public Entries<K, V> entries() {
        if (this.entries1 == null) {
            this.entries1 = new Entries<>(this);
            this.entries2 = new Entries<>(this);
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

    /**
     * Returns an iterator for the values in the map. Remove is
     * supported. Note that the same iterator instance is returned
     * each time this method is called. Use the {@link Values}
     * constructor for nested or multithreaded iteration.
     */
    public Values<V> values() {
        if (this.values1 == null) {
            this.values1 = new Values<>(this);
            this.values2 = new Values<>(this);
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

    /**
     * Returns an iterator for the keys in the map. Remove is
     * supported. Note that the same iterator instance is returned
     * each time this method is called. Use the {@link Keys}
     * constructor for nested or multithreaded iteration.
     */
    public Keys<K> keys() {
        if (this.keys1 == null) {
            this.keys1 = new Keys<>(this);
            this.keys2 = new Keys<>(this);
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

    static public class Entry<K, V> {
        public K key;
        public V value;

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    static private abstract class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
        public boolean hasNext;

        final ObjectMap<K, V> map;
        int nextIndex, currentIndex;
        boolean valid = true;

        public MapIterator(ObjectMap<K, V> map) {
            this.map = map;
            reset();
        }

        public void reset() {
            this.currentIndex = -1;
            this.nextIndex = -1;
            findNextIndex();
        }

        void findNextIndex() {
            this.hasNext = false;
            final K[] keyTable = this.map.keyTable;
            for (final int n = this.map.capacity + this.map.stashSize; ++this.nextIndex < n; ) {
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
            if (this.currentIndex >= this.map.capacity) {
                this.map.removeStashIndex(this.currentIndex);
                this.nextIndex = this.currentIndex - 1;
                findNextIndex();
            } else {
                this.map.keyTable[this.currentIndex] = null;
                this.map.valueTable[this.currentIndex] = null;
            }
            this.currentIndex = -1;
            this.map.size--;
        }
    }

    static public class Entries<K, V> extends MapIterator<K, V, Entry<K, V>> {
        Entry<K, V> entry = new Entry<>();

        public Entries(ObjectMap<K, V> map) {
            super(map);
        }

        /**
         * Note the same entry instance is returned each time this
         * method is called.
         */
        @Override
        public Entry<K, V> next() {
            if (!this.hasNext) throw new NoSuchElementException();
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            final K[] keyTable = this.map.keyTable;
            this.entry.key = keyTable[this.nextIndex];
            this.entry.value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return this.entry;
        }

        @Override
        public boolean hasNext() {
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");
            return this.hasNext;
        }

        @Override
        public Entries<K, V> iterator() {
            return this;
        }
    }

    static public class Values<V> extends MapIterator<Object, V, V> {
        @SuppressWarnings("unchecked")
        public Values(ObjectMap<?, V> map) {
            super((ObjectMap<Object, V>) map);
        }

        @Override
        public boolean hasNext() {
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");
            return this.hasNext;
        }

        @Override
        public V next() {
            if (!this.hasNext) throw new NoSuchElementException();
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            final V value = this.map.valueTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return value;
        }

        @Override
        public Values<V> iterator() {
            return this;
        }

        /**
         * Returns a new array containing the remaining values.
         */
        public Array<V> toArray() {
            return toArray(new Array<>(true, this.map.size));
        }

        /**
         * Adds the remaining values to the specified array.
         */
        public Array<V> toArray(Array<V> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

    static public class Keys<K> extends MapIterator<K, Object, K> {
        @SuppressWarnings("unchecked")
        public Keys(ObjectMap<K, ?> map) {
            super((ObjectMap<K, Object>) map);
        }

        @Override
        public boolean hasNext() {
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");
            return this.hasNext;
        }

        @Override
        public K next() {
            if (!this.hasNext) throw new NoSuchElementException();
            Preconditions.checkState(this.valid, "#iterator() cannot be used nested.");

            final K key = this.map.keyTable[this.nextIndex];
            this.currentIndex = this.nextIndex;
            findNextIndex();
            return key;
        }

        @Override
        public Keys<K> iterator() {
            return this;
        }

        /**
         * Returns a new array containing the remaining keys.
         */
        public Array<K> toArray() {
            return toArray(new Array<>(true, this.map.size));
        }

        /**
         * Adds the remaining keys to the array.
         */
        public Array<K> toArray(Array<K> array) {
            while (this.hasNext) {
                array.add(next());
            }
            return array;
        }
    }

}
