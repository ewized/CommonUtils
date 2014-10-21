package com.archeinteractive.dev.commonutils.ref;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * An extension of an HashSet that stores values as WeakReferences,
 * with convenience methods to automatically wrap and unwrap values.
 * 
 * @param <T> The desired type of elements to store in this Set.
 */
public class WeakSet<T> extends HashSet<WeakReference<T>> implements WeakCollection<T> {

    private static final long serialVersionUID = -2350740622831881249L;
    
    /**
     * Construct a new WeakSet with the elements from the given WeakSet.
     * 
     * @param weakset The WeakSet to copy elements from.
     */
    public WeakSet(WeakSet<T> weakset) {
        addAll(weakset);
    }
    
    /**
     * Construct a new WeakSet with the elements from the given Collection.
     * 
     * @param collection A collection of an assignable type to copy elements from.
     */
    public WeakSet(Collection<? extends T> collection) {
        for (T value : collection) {
            addValue(value);
        }
    }
    
    /**
     * Construct a new WeakSet with the given elements.
     * 
     * @param values An array of WeakReferences of the desired type.
     */
    public WeakSet(WeakReference<? extends T>[] values) {
        for (WeakReference<? extends T> ref : values) {
            if (ref == null || ref.get() == null)
                continue;
            
            addValue(ref.get());
        }
    }
    
    /**
     * Construct a new WeakSet.
     */
    public WeakSet() {}
    
    
    public boolean add(WeakReference<T> add) {
        if (add == null || add.get() == null)
            throw new IllegalArgumentException("Cannot add a null value to this collection!");
        
        return super.add(add);
    }
    
    /**
     * Automatically wraps the given value as a WeakReference and adds it to
     * this Set.
     * 
     * @param add The element to be appeneded.
     * @return <b>true</b> if this Set did not contain the element and it was added.
     */
    public boolean addValue(T add) {
        return add(new WeakReference<T>(add));
    }
    
    public boolean addAll(Collection<? extends WeakReference<T>> collection) {
        if (collection instanceof WeakSet) {
            return super.addAll(collection);
        } else {
            collection = new HashSet<WeakReference<T>>(collection);
            Iterator<? extends WeakReference<T>> it = collection.iterator();
            
            while (it.hasNext()) {
                WeakReference<T> ref = it.next();
                
                if (ref == null || ref.get() == null) {
                    it.remove();
                }
            }
            
            return super.addAll(collection);
        }
    }
    
    /**
     * Automatically wraps the given values as WeakReferences and adds all of
     * them to this Set. The behavior of this operation is undefined if the
     * specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this list, and this list is nonempty.)
     * 
     * @param collection The Collection of values to add.
     * @return <b>true</b> if this list changed as a result of the call.
     */
    public boolean addAllValues(Collection<T> collection) {
        WeakSet<T> weakset = new WeakSet<T>(collection);
        return addAll(weakset);
    }
    
    public boolean contains(Object object) {
        boolean contains = false;
        Iterator<WeakReference<T>> it = iterator();
        
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            
            if (ref == null || ref.get() == null) {
                it.remove();
            }
            
            if (equalReferences(ref, object)) {
                contains = true;
            }
        }
        
        return contains;
    }
    
    public boolean containsAll(Collection<?> collection) {
        boolean contains = true;
        
        for (Object value : collection) {
            contains &= contains(value);
        }
        
        return contains;
    }
    
    public boolean remove(Object object) {
        boolean found = false;
        Iterator<WeakReference<T>> it = iterator();
        
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            
            if (ref == null || ref.get() == null) {
                it.remove();
                continue;
            }
            
            if (equalReferences(ref, object)) {
                it.remove();
                found = true;
            }
        }
        
        return found;
    }
    
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        
        for (Object value : collection) {
            changed |= remove(value);
        }
        
        return changed;
    }
    
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        Iterator<WeakReference<T>> it = iterator();
        
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            
            if (collection.contains(ref) || collection.contains(ref.get())) {
                continue;
            }
            
            it.remove();
            changed = true;
        }
        
        return changed;
    }
    
    /**
     * Get a HashSet that represents the values contained within this WeakSet.
     * Any invalid references are automatically cleaned in the returned Set.
     * 
     * @return A HashSet containing the values of the remaining valid references
     *         from this WeakSet.
     */
    public HashSet<T> values() {
        HashSet<T> values = new HashSet<T>();
        Iterator<WeakReference<T>> it = iterator();
        
        while (it.hasNext()) {
            WeakReference<T> ref = it.next();
            
            if (ref == null || ref.get() == null) {
                it.remove();
                continue;
            }
            
            values.add(ref.get());
        }
        
        return values;
    }
    
    /**
     * Get an array that represents the values contained within this WeakSet.
     * Any invalid references will have been cleaned from the returned Set.
     * 
     * @return An array containing the values of the remaining valid references
     *         from this WeakSet.
     */
    public T[] valueArray(T[] array) {
        return values().toArray(array);
    }
    
    
    /**
     * Check for equality between a Reference and another object. All shallow cases
     * are accounted for, including if the given Object is also a Reference.
     * 
     * @param alpha The Reference to test.
     * @param beta  The other Object to test.
     * @return <b>true</b> if the given Reference is equal to- or the object
     *         that the Reference refers to is equal to- the given object or the
     *         object that it refers to (if applicable), <b>false</b> otherwise.
     */
    public static boolean equalReferences(Reference<?> alpha, Object beta) {
        if (alpha == null || beta == null) {
            return alpha == null && beta == null;
        } else if (alpha.get() == null) {
            return beta instanceof Reference && ((Reference<?>) beta).get() == null;
        }
        
        if (beta instanceof Reference) {
            Reference<?> gamma = (Reference<?>) beta;
            return alpha.equals(gamma) || alpha.equals(gamma.get()) ||
                    alpha.get().equals(gamma) || alpha.get().equals(gamma.get());
        } else {
            return beta.equals(alpha) || beta.equals(alpha.get());
        }
    }
}
