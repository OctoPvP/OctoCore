package net.octopvp.octocore.common.object;

import java.util.ArrayList;
import java.util.Collection;

public class FixedList<T> extends ArrayList<T> {
    private int maxSize;
    public FixedList(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            remove(0);
        }
        return super.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (size() + c.size() > maxSize) {
            removeRange(0, size() + c.size() - maxSize);
        }
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (!System.getProperty("fixedlist.allowIndexAddAll", "false").equals("true")) {
            throw new UnsupportedOperationException("Index addAll is not supported, set the property fixedlist.allowIndexAddAll to true to enable it");
        }
        return super.addAll(index, c);
    }

    @Override
    public void add(int index, T element) {
        if (!System.getProperty("fixedlist.allowIndexAdd", "false").equals("true")) {
            throw new UnsupportedOperationException("Index add is not supported, set the property fixedlist.allowIndexAdd to true to enable it");
        }
        super.add(index, element);
    }
}
