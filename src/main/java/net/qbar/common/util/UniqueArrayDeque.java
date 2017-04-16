package net.qbar.common.util;

import java.util.ArrayDeque;

public class UniqueArrayDeque<E> extends ArrayDeque<E>
{
    public boolean add(E e)
    {
        if (this.contains(e))
            return false;
        return super.add(e);
    }
}
