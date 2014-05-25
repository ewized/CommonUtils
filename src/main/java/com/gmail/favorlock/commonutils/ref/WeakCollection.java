package com.gmail.favorlock.commonutils.ref;

import java.util.Collection;

public interface WeakCollection<T> {

    /**
     * Get a Collection that represents the values contained within this
     * WeakCollection.
     * 
     * @return A Collection containing the values of the references from this
     *         WeakCollection.
     */
    public Collection<T> values();
    
    /**
     * Get an array that represents the values contained within this
     * WeakCollection.
     * 
     * @return An array containing the values of the references from this
     *         WeakSet.
     */
    public T[] valueArray(T[] array);
}
