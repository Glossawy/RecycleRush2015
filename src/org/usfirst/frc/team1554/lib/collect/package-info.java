/**
 * Unique Collections used in RoboLib. These collections are meant to be an
 * improvement over the java.util Collections with the downside of losing out on a
 * common Collection interface. <br />
 * <br />
 * Although {@link org.usfirst.frc.team1554.lib.collect.Array Array} runs like an
 * optimized {@link java.util.ArrayList Java ArrayList}, the primitive collections
 * such as {@link org.usfirst.frc.team1554.lib.collect.IntMap IntMap} and Object Hash
 * implementations such as {@link org.usfirst.frc.team1554.lib.collect.ObjectSet
 * ObjectSet} and {@link org.usfirst.frc.team1554.lib.collect.ObjectMap ObjectMap}
 * typically run in O(1) time for get(), containsX() and remove() while put() may be
 * a bit slower. In the worst case these algorithms run in O(logn). A quick
 * {@link org.usfirst.frc.team1554.lib.collect.Cache MRU/LRU Cache} implementation is
 * provided as well, along with Static methods for creatins maps, lists and sets.
 */
package org.usfirst.frc.team1554.lib.collect;