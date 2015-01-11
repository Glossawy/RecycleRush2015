package org.usfirst.frc.team1554.lib.collect;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.usfirst.frc.team1554.lib.util.Preconditions;

/**
 * Utilities for Instantiating and Manipulating Maps
 * 
 * @author Matthew
 */
public class Maps {

	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int estimation) {
		return new HashMap<K, V>(estimation);
	}

	public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
		Preconditions.checkNotNull(map);
		return new HashMap<K, V>(map);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
		return new EnumMap<K, V>(type);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
		return new EnumMap<K, V>(map);
	}

	public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(EnumMap<K, ? extends V> map) {
		return new EnumMap<K, V>(map);
	}

	public static <K, V> TreeMap<K, V> newTreeMap() {
		return new TreeMap<K, V>();
	}

	public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map) {
		return new TreeMap<K, V>(map);
	}

	public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
		return new TreeMap<K, V>(comparator);
	}

	public static <V> IntMap<V> newIntMap() {
		return new IntMap<V>();
	}

	public static <V> IntMap<V> newIntMap(int initialCapacity) {
		return new IntMap<V>(initialCapacity);
	}

	public static <V> IntMap<V> newIntMap(int initialCapacity, float loadFactor) {
		return new IntMap<V>(initialCapacity, loadFactor);
	}

	public static <V> IntMap<V> newIntMap(IntMap<V> map) {
		return new IntMap<V>(map);
	}

}
