package ru.ifmo.rain.pevnev.arrayset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.*;

public class ArraySet<E>
        extends AbstractSet<E>
        implements SortedSet<E> {

    private List<E> list;
    private Comparator<? super E> cmp;

    private ArraySet(List<? extends E> list, Comparator<? super E> cmp) {
        this.list = unmodifiableList(list);
        this.cmp = cmp;
    }

    public ArraySet() {
        this(emptyList(), null);
    }

    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    public ArraySet(Collection<? extends E> collection, Comparator<? super E> cmp) {
        this(new ArrayList<>(getTreeSet(collection, cmp)), cmp);
    }

    @NotNull
    private static <E> TreeSet<E> getTreeSet(Collection<? extends E> collection, Comparator<? super E> cmp) {
        TreeSet<E> treeSet = new TreeSet<>(cmp);
        treeSet.addAll(collection);
        return treeSet;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Nullable
    @Override
    public Comparator<? super E> comparator() {
        return cmp;
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public boolean contains(Object o) {
        return binarySearch(list, (E) o, cmp) >= 0;
    }

    @NotNull
    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        return view(
                insertionPoint(fromElement),
                insertionPoint(toElement)
        );
    }

    @NotNull
    @Override
    public SortedSet<E> headSet(E toElement) {
        return view(0, insertionPoint(toElement));
    }

    @NotNull
    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return view(insertionPoint(fromElement), size());
    }

    private int insertionPoint(E element) {
        int insPoint = binarySearch(list, element, cmp);

        return insPoint < 0 ? -insPoint - 1 : insPoint;
    }

    private SortedSet<E> view(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }

        return new ArraySet<>(list.subList(fromIndex, toIndex), cmp);
    }

    @Override
    public E first() {
        return getFirstOrLast(0);
    }

    @Override
    public E last() {
        return getFirstOrLast(size() - 1);
    }

    private E getFirstOrLast(int i) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        return list.get(i);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }
}