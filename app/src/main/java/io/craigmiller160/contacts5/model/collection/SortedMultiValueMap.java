package io.craigmiller160.contacts5.model.collection;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Craig on 2/11/2016.
 */
public class SortedMultiValueMap<K,V> extends MultiValueMap<K,V> {

    private Comparator<V> valueComparator;

    private SortedList<K> sortedKeys;

    public SortedMultiValueMap(){
        this(null, null);
    }

    public SortedMultiValueMap(Comparator<K> keyComparator, Comparator<V> valueComparator){
        sortedKeys = new SortedList<>(keyComparator);
        setValueComparator(valueComparator);
    }

    @Override
    protected Collection<V> getNewCollection() {
        //If the comparator is null, SortedList will use its DefaultComparator
        return new SortedList<>(valueComparator);
    }

    public void setKeyComparator(Comparator<K> keyComparator){
        //If this is null, SortedList will use its default comparator instead
        sortedKeys.setComparator(keyComparator);
    }

    public Comparator<K> getKeyComparator(){
        return sortedKeys.getComparator();
    }

    public void setValueComparator(Comparator<V> valueComparator){
        //If this is null, SortedList will use its default comparator instead
        this.valueComparator = valueComparator;

        Set<K> keySet = keySet();
        for(K key : keySet){
            SortedList<V> list = (SortedList<V>) get(key);
            list.setComparator(valueComparator);
        }
    }

    public Comparator<V> getValueComparator(){
        return valueComparator;
    }

    @Override
    public Collection<V> put(K key, Collection<V> c){
        Collection<V> result = super.put(key, c);
        if(!sortedKeys.contains(key)){
            sortedKeys.add(key);
        }
        return result;
    }

    @Override
    public V putValue(K key, V value){
        V result = super.putValue(key, value);
        if(!sortedKeys.contains(key)){
            sortedKeys.add(key);
        }
        return result;
    }

    @Override
    public Collection<V> remove(Object key){
        Collection<V> result = super.remove(key);
        sortedKeys.remove(key);
        return result;
    }

    @Override
    public V removeValue(V value){
        //Leaving this method here so that it's not thought that it's being left out
        //This method will ultimately call remove(Key), and that method will ensure sortedKeys is updated
        return super.removeValue( value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        //Leaving this method here so that it's not thought that it's being left out
        //This method will ultimately call put(...), which will ensure that the sortedKeys gets updated
        super.putAll(m);
    }

    @Override
    public void clear(){
        super.clear();
        sortedKeys.clear();
    }

    public SortedList<K> getSortedKeys(){
        return sortedKeys;
    }

}
