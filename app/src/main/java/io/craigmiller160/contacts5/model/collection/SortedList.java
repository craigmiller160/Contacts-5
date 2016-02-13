package io.craigmiller160.contacts5.model.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A special implementation of the List interface
 * that maintains a sort order at all times. The side
 * effects of this are inconsistent index values (positions
 * are constantly changing every time the contents of
 * the list changes) and the inability to insert elements
 * at specific positions. However, if a sorted collection
 * that can be accessed by index number is needed, this
 * is a great option.
 *
 * The sort order is maintained by a comparator. By default,
 * the comparator used checks if the values in the list
 * implement Comparable, and uses compareTo(...) if they do.
 * If they don't, it compares the toString() values of
 * the items in the collection. However, this is only used
 * if a custom comparator is not provided, and it is very
 * recommended to do so based on how this list is going
 * to be used.
 *
 * Created by Craig on 2/10/2016.
 */
public class SortedList<T> implements List<T> {

    /**
     * An underlying ArrayList that this class wraps around.
     */
    private List<T> list;

    /**
     * A comparator that, when it is provided, is used to
     * define the sort order of the collection.
     */
    private Comparator<T> comparator;

    public SortedList(){
        this(null, null);
    }

    public SortedList(Collection<T> collection){
        this(collection, null);
    }

    public SortedList(Comparator<T> comparator){
        this(null, comparator);
    }

    public SortedList(Collection<T> collection, Comparator<T> comparator){
        this.list = new ArrayList<>();
        setComparator(comparator);

        if(collection != null){
            addAll(collection);
        }
    }

    public void setComparator(Comparator<T> comparator){
        if(comparator != null){
            this.comparator = comparator;
        }
        else{
            this.comparator = new DefaultComparator();
        }

        Collections.sort(list, this.comparator);
    }

    public Comparator<T> getComparator(){
        return comparator;
    }

    /**
     * This is a special and powerful method that bypasses
     * the sorting process of this collection and simply
     * places the contents of the list into the underlying
     * list unchanged. This should only be done if you are
     * certain that the list being set is already in the
     * same sort order that this collection already maintains.
     *
     * The main use case for this method is when retrieving a
     * large amount of pre-sorted data that will then be
     * dumped into this list. In that case, the extra sorting
     * would hinder performance, and is only valuable for maintaining
     * the order while the data is being manipulated.
     *
     * Because this method could break the contract of this class,
     * namely that all items will always maintain the specified
     * sort order, it should be used very carefully and very
     * sparingly.
     *
     * Lastly, when invoked, all previous entries in the list will
     * be erased and the new data will replace it.
     *
     * @param preSortedList the pre-sorted list to add
     */
    public void setPreSortedList(List<T> preSortedList){
        if(preSortedList != null){
            this.list = preSortedList;
        }
        else{
            this.list = new ArrayList<>();
        }
    }

    /**
     * This method is redundant in this class, as items
     * are placed in the List at locations according
     * to their orders. As a result, this method just
     * called the standard add(T) method and ignores
     * the location argument.
     *
     * @param location the location argument to be ignored.
     * @param object the object to add to the collection.
     */
    @Override
    public void add(int location, T object) {
        add(object);
    }

    @Override
    public boolean add(T object) {
        int position = getSortedPosition(object);
        list.add(position, object);
        return true;
    }

    private int getSortedPosition(T object){
        int result = Collections.binarySearch(list, object, comparator);
        if (result < 0) {
            return (result + 1) * -1;
        }
        return result;
    }

    /**
     * This method is redundant in this class, as items
     * are placed in the List at locations according
     * to their orders. As a result, this method just
     * called the standard addAll(Collection) method and ignores
     * the location argument.
     *
     * @param location the location argument to be ignored.
     * @param collection the collection to add to this collection.
     */
    @Override
    public boolean addAll(int location, Collection<? extends T> collection) {
        return addAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean result = list.addAll(collection);
        if(result){
            Collections.sort(list, comparator);
        }
        return result;
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object object) {
        return list.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return list.containsAll(collection);
    }

    @Override
    public T get(int location) {
        return list.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return list.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return list.lastIndexOf(object);
    }

    //TODO ultimately upgrade the listIterators to support UnsupportedOperationException

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("List iterator not supported by SortedList");
    }

    @Override
    public ListIterator<T> listIterator(int location) {
        throw new UnsupportedOperationException("List iterator not supported by SortedList");
    }

    @Override
    public T remove(int location) {
        return list.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return list.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return list.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return list.retainAll(collection);
    }

    @Override
    public T set(int location, T object) {
        if(object != null){
            int newPosition = getSortedPosition(object);
            list.remove(location);
            list.add(newPosition, object);
        }
        return object;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public List<T> subList(int start, int end) {
        return list.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        return list.toArray(array);
    }

    private class DefaultComparator implements Comparator<T>{

        @Override
        public int compare(Object o1, Object o2) {
            if(o1 instanceof Comparable && o2 instanceof Comparable){
                return ((Comparable) o1).compareTo(o2);
            }
            return o1.toString().compareTo(o2.toString());
        }
    }
}
