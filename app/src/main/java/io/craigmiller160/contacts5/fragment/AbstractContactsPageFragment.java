package io.craigmiller160.contacts5.fragment;

/**
 * Created by craig on 6/12/16.
 */
public abstract class AbstractContactsPageFragment<T> extends AbstractContactsFragment<T> implements Comparable<AbstractContactsPageFragment<T>> {

    public abstract int getPageTitleResId();

    public abstract int getPageIndex();

    @Override
    public int compareTo(AbstractContactsPageFragment<T> another) {
        Integer thisIndex = getPageIndex();
        Integer otherIndex = another.getPageIndex();
        return thisIndex.compareTo(otherIndex);
    }
}
