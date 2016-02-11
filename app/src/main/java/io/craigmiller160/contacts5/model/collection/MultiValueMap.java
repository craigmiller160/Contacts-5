package io.craigmiller160.contacts5.model.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Craig on 2/10/2016.
 */
public class MultiValueMap<K,V> implements Map<K,Collection<V>> {

    private Map<K, Collection<V>> map;

    public MultiValueMap() {
        map = new HashMap<>();
    }

    /**
     * Factory method that builds a new collection
     * to store the values of this map.
     * <p/>
     * Subclasses can and should override this class
     * to use different collection types to achieve
     * different behaviors and results.
     * <p/>
     * By default, it returns an ArrayList.
     *
     * @return the collection being used to store
     * values in this Map.
     */
    protected Collection<V> getNewCollection() {
        return new ArrayList<>();
    }

    /**
     * Get a count of the number of collections
     * in the map.
     *
     * @return the number of collections in the Map.
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * Get a count of the total number of values
     * in all the collections in the Map.
     *
     * @return the total number of values in all collections
     * in the Map.
     */
    public int fullSize() {
        Set<K> keySet = map.keySet();
        int total = 0;
        for (K k : keySet) {
            total += map.get(k).size();
        }
        return total;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Test if this Map contains the provided value,
     * in any of the collections it contains.
     *
     * @param value the value to test if this Map contains.
     * @return true if the value is within any of the Map's collections.
     */
    @Override
    public boolean containsValue(Object value) {
        Set<K> keySet = map.keySet();
        for (K k : keySet) {
            Collection<V> c = map.get(k);
            if (c.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test if the Map contains a collection that matches
     * the one provided.
     *
     * @param c the colleciton to test if the Map contains.
     * @return true if the map contains a matching collection.
     */
    public boolean containsCollection(Collection<V> c) {
        return map.containsValue(c);
    }

    @Override
    public Collection<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public Collection<V> put(K key, Collection<V> c) {
        Collection<V> values = map.get(key);
        if (values != null) {
            values.addAll(c);
        } else {
            map.put(key, c);
        }
        return c;
    }

    public V putValue(K key, V value) {
        Collection<V> values = map.get(key);
        if (values != null) {
            values.add(value);
            return value;
        }
        return null;
    }

    @Override
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    /**
     * Remove a single value from the collection
     * matching the provided key.
     *
     * @param key   the key of the collection to remove
     *              a value from.
     * @param value the value to remove from the collection.
     * @return true if the value was successfully removed.
     */
    public boolean removeValue(K key, V value) {
        Collection<V> c = map.get(key);
        if (c != null) {
            return c.remove(value);
        }
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        Set<? extends K> keySet = m.keySet();
        Collection<V> template = getNewCollection();
        for (K k : keySet) {
            Collection c = m.get(k);
            if (c.getClass().equals(template.getClass())) {
                put(k, c);
            } else {
                throw new IllegalArgumentException("Map must maintain the same type of Collection as its value. " +
                        "Expected: " + template.getClass().getName() + " Found: " + c.getClass().getName());
            }
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

}
