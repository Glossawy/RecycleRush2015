package org.usfirst.frc.team1554.lib.collect;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A simple Cache implementation making use of a LRU Map and MRU Map to track
 * references and to hold on to only <tt>capacity</tt> number of objects at most as
 * well as any references in the LRU that the GC has not yet collected. <br />
 * <br />
 * The LRU Map is implemented as a {@link WeakHashMap} <br />
 * The MRU Map is implemented as a {@link LinkedHashMap}. <br />
 * <br />
 * This implementation is extremely simple and rudimentary. As such it will be
 * expanded later in an attempt to be the robotic equivalent of Guava's LRU Cache.<br />
 * <br />
 * Any call to this Cache is synchronized to maintain Thread Safety, since this is
 * sub-optimal, this will be made non-synchronized with a separate ConcurrentCache
 * implementation in the future that does not use any <tt>synchronized</tt> blocks.
 * 
 * @author Matthew
 *
 * @param <K>
 *            - Cache Key Type
 * @param <V>
 *            - Cache Value Type
 */
public class Cache<K, V> {

	/*
	 * Those objects in the LRU Cache are up for garbage collection.
	 */

	private Map<K, V> mruCache; // Most Recently Used
	private Map<K, V> lruCache; // Least Recently Used

	public Cache(final int capacity) {
		this.lruCache = new WeakHashMap<>();

		this.mruCache = new LinkedHashMap<K, V>(capacity + 1, 0.6f, true) {

			// Override to remove from MRU and add to LRU
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
				if (size() > capacity) {
					Cache.this.lruCache.put(entry.getKey(), entry.getValue());
					return true;
				}

				return false;
			}
		};
	}

	/**
	 * Retrieve a Value from this cache if it exists. If the reference has been
	 * Garbage Collected or does not exist, this method returns null.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized V get(K key) {
		V value = this.mruCache.get(key);
		if (value != null) return value;

		value = this.lruCache.get(key);
		if (value != null) {
			this.lruCache.remove(key);
			this.mruCache.put(key, value);
		}

		return value;
	}

	/**
	 * Put a Key-Value pair into the Cache, removing it from the LRU if it already
	 * exists and settings it's value in the MRU.
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void put(K key, V value) {
		this.lruCache.remove(key);
		this.mruCache.put(key, value);
	}

	/**
	 * Check if key exists.
	 * 
	 * @param key
	 * @return
	 */
	public synchronized boolean containsKey(K key) {
		return this.lruCache.containsKey(key) || this.mruCache.containsKey(key);
	}

	/**
	 * Check if value exists.
	 * 
	 * @param value
	 * @return
	 */
	public synchronized boolean containsValue(V value) {
		return this.lruCache.containsValue(value) || this.mruCache.containsValue(value);
	}
}
