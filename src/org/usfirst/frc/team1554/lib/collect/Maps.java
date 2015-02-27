package org.usfirst.frc.team1554.lib.collect;

import org.usfirst.frc.team1554.lib.util.Preconditions;

import java.util.*;

/**
 * Utilities for Instantiating and Manipulating Maps
 *
 * @author Matthew
 */
public class Maps {

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int estimation) {
        return new HashMap<>(estimation);
    }

    public static <K, V> HashMap<K, V> newHashMap(Map<K, V> map) {
        Preconditions.checkNotNull(map);
        return new HashMap<>(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type) {
        return new EnumMap<>(type);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(EnumMap<K, ? extends V> map) {
        return new EnumMap<>(map);
    }

    public static <K, V> TreeMap<K, V> newTreeMap() {
        return new TreeMap<>();
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Map<K, V> map) {
        return new TreeMap<>(map);
    }

    public static <K, V> TreeMap<K, V> newTreeMap(Comparator<? super K> comparator) {
        return new TreeMap<>(comparator);
    }

    public static <V> IntMap<V> newIntMap() {
        return new IntMap<>();
    }

    public static <V> IntMap<V> newIntMap(int initialCapacity) {
        return new IntMap<>(initialCapacity);
    }

    public static <V> IntMap<V> newIntMap(int initialCapacity, float loadFactor) {
        return new IntMap<>(initialCapacity, loadFactor);
    }

    public static <V> IntMap<V> newIntMap(IntMap<V> map) {
        return new IntMap<>(map);
    }

    public static <K, V> ObjectMap<K, V> newObjectMap() {
        return new ObjectMap<>();
    }

    public static <K, V> ObjectMap<K, V> newObjectMap(int initialCapacity) {
        return new ObjectMap<>(initialCapacity);
    }

    public static <K, V> ObjectMap<K, V> newObjectMap(int capacity, float loadFactor) {
        return new ObjectMap<>(capacity, loadFactor);
    }

    public static <K, V> ObjectMap<K, V> newObjectMap(ObjectMap<? extends K, ? extends V> map) {
        return new ObjectMap<>(map);
    }

}
