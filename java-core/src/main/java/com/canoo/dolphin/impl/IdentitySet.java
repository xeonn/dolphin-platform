package com.canoo.dolphin.impl;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hendrikebbers on 20.01.16.
 */
public class IdentitySet<E> implements Set<E> {

    private IdentityHashMap<E, E> internalMap;

    public IdentitySet() {
        internalMap = new IdentityHashMap<>();
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internalMap.containsKey(o);
    }

    @Override
    public Iterator<E> iterator() {
        return internalMap.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return internalMap.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return internalMap.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        boolean ret = internalMap.keySet().contains(e);
        internalMap.put(e, e);
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        boolean ret = internalMap.keySet().contains(o);
        internalMap.remove(o);
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internalMap.keySet().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean ret = false;
        for(E elem : c) {
            boolean currentRet = add(elem);
            if(currentRet) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new RuntimeException("Not yet implemented!");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = false;
        for(Object elem : c) {
            boolean currentRet = remove(elem);
            if(currentRet) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public void clear() {
        internalMap.clear();
    }
}
