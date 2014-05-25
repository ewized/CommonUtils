package com.gmail.favorlock.commonutils.ref;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An extension of an ArrayList that stores values as WeakReferences,
 * with convenience methods to automatically wrap and unwrap values.
 * 
 * @param <T> The desired type of elements to store in this List.
 */
public class WeakList<T> extends ArrayList<WeakReference<T>> implements WeakCollection<T> {

    private static final long serialVersionUID = 488462089918650743L;
    
    private boolean clean;
    
    /**
     * Construct a new WeakList with the elements from the given WeakList.
     * 
     * @param weaklist The WeakList to copy elements from.
     */
    public WeakList(WeakList<T> weaklist) {
        this.clean = weaklist.clean;
        addAll(weaklist);
    }
    
    /**
     * Construct a new WeakList with the elements from the given Collection.
     * 
     * @param collection A Collection of an assignable type to copy elements from.
     */
    public WeakList(Collection<? extends T> collection) {
        this.clean = true;
        
        for (T value : collection) {
            addValue(value);
        }
    }
    
    /**
     * Construct a new WeakList with the given elements.
     * 
     * @param values An array of WeakReferences of the desired type.
     */
    public WeakList(WeakReference<? extends T>[] values) {
        this.clean = true;
        
        for (WeakReference<? extends T> ref : values) {
            if (ref == null || ref.get() == null)
                continue;
            
            addValue(ref.get());
        }
    }
    
    /**
     * Construct a new WeakList.
     */
    public WeakList() {
        this.clean = true;
    }
    
    
    /**
     * Set whether or not this WeakList should clean its values during existing
     * linear traversals of the represented list.
     * 
     * @param clean <b>true</b> to clean automatically, <b>false</b> to only
     *              clean when {@link WeakList#clean()} is called explicitly.
     */
    public WeakList<T> setClean(boolean clean) {
        this.clean = clean;
        return this;
    }
    
    /**
     * Clean the values of this WeakList. This operation will search through the
     * list in O(n) time, removing any values whose references are no longer
     * valid.
     * <p>
     * This differs from this WeakList's automatic cleaning flag (which was
     * defined at construction) in the way that the automatic cleaning will
     * never perform a linear traversal of the values in order to clean, it
     * will only take advantage of existing traversals when they happen.
     */
    public void clean() {
        for (int i = 0; i < size(); i++) {
            WeakReference<T> value = get(i);
            
            if (value == null || value.get() == null) {
                remove(i);
                i--;
            }
        }
    }
    
    public boolean add(WeakReference<T> add) {
        if (add == null || add.get() == null)
            throw new IllegalArgumentException("Cannot add a null value to this collection!");
        
        return super.add(add);
    }
    
    public void add(int index, WeakReference<T> add) {
        if (add == null || add.get() == null)
            throw new IllegalArgumentException("Cannot add a null value to this collection!");
        
        super.add(index, add);
    }
    
    /**
     * Automatically wraps the given value as a WeakReference and appends it
     * to the end of this list.
     * 
     * @param add The element to be appeneded.
     * @return <b>true</b>.
     */
    public boolean addValue(T add) {
        return add(new WeakReference<T>(add));
    }
    
    /**
     * Automatically wraps the given value as a WeakReference and inserts it at
     * the specified position in this list. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (adds one to
     * their indices).
     * 
     * @param index The index at which the specified element is to be inserted.
     * @param add   The element to be inserted.
     */
    public void addValue(int index, T add) {
        add(index, new WeakReference<T>(add));
    }
    
    public boolean addAll(Collection<? extends WeakReference<T>> collection) {
        if (collection instanceof WeakList) {
            return super.addAll(collection);
        } else {
            collection = new ArrayList<WeakReference<T>>(collection);
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
    
    public boolean addAll(int index, Collection<? extends WeakReference<T>> collection) {
        if (collection instanceof WeakList) {
            return super.addAll(index, collection);
        } else {
            collection = new ArrayList<WeakReference<T>>(collection);
            Iterator<? extends WeakReference<T>> it = collection.iterator();
            
            while (it.hasNext()) {
                WeakReference<T> ref = it.next();
                
                if (ref == null || ref.get() == null) {
                    it.remove();
                }
            }
            
            return super.addAll(index, collection);
        }
    }
    
    /**
     * Automatically wraps the given values as WeakReferences and appends all of
     * them to the end of this list, in the order that they are returned by the
     * specified collection's Iterator. The behavior of this operation is
     * undefined if the specified collection is modified while the operation is
     * in progress. (This implies that the behavior of this call is undefined if
     * the specified collection is this list, and this list is nonempty.)
     * 
     * @param collection The Collection of values to add.
     * @return <b>true</b> if this list changed as a result of the call.
     */
    public boolean addAllValues(Collection<T> collection) {
        WeakList<T> weaklist = new WeakList<T>(collection);
        return addAll(weaklist);
    }
    
    /**
     * Automatically wraps the given values as WeakReferences and Inserts all of
     * them into this list, starting at the specified position. Shifts the
     * element currently at that position (if any) and any subsequent elements
     * to the right (increases their indices). The new elements will appear in
     * the list in the order that they are returned by the specified
     * collection's iterator.
     * 
     * @param index      The index at which to insert the first element from the
     *                   specified Collection.
     * @param collection The Collection of values to add.
     * @return <b>true</b> if this list changed as a result of the call.
     */
    public boolean addAllValues(int index, Collection<T> collection) {
        WeakList<T> weaklist = new WeakList<T>(collection);
        return addAll(index, weaklist);
    }
    
    public boolean contains(Object object) {
        boolean contains = false;
        
        for (int i = 0; i < size(); i++) {
            WeakReference<T> ref = get(i);
            
            if (clean && (ref == null || ref.get() == null)) {
                remove(i);
                i--;
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
    
    /**
     * Get the value of the reference at the given index.
     * 
     * @param index The index of the desired value.
     * @return The value that the reference at the given index refers to.
     */
    public T getValue(int index) {
        WeakReference<T> ref = get(index);
        
        if (ref == null)
            return null;
        
        return ref.get();
    }
    
    public int indexOf(Object object) {
        for (int i = 0; i < size(); i++) {
            WeakReference<T> value = get(i);
            
            if (equalReferences(value, object)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public int lastIndexOf(Object object) {
        for (int i = size() - 1; i >= 0; i--) {
            WeakReference<T> value = get(i);
            
            if (equalReferences(value, object)) {
                return i;
            }
        }
        
        return -1;
    }
    
    public boolean remove(Object object) {
        int index = indexOf(object);
        
        if (index >= 0) {
            remove(index);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Automatically resolves the value of the reference returned.
     * 
     * @param index The index of the element to be removed.
     * @return The previous value referred to by the element that was at the index.
     */
    public T removeValue(int index) {
        WeakReference<T> ref = remove(index);
        
        if (ref == null)
            return null;
        
        return ref.get();
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
        Iterator<WeakReference<T>> it = this.iterator();
        
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
    
    public WeakReference<T> set(int index, WeakReference<T> value) {
        if (value == null || value.get() == null)
            throw new IllegalArgumentException("Cannot add a null value to this collection!");
        
        return super.set(index, value);
    }
    
    /**
     * Automatically wraps the given value as a WeakReference and replaces the
     * element at the specified position in this list with the specified
     * element.
     * 
     * @param index The index of the element to replace.
     * @param value The element to be stored at the specified position.
     * @return The element previously at the given position.
     */
    public WeakReference<T> setValue(int index, T value) {
        return set(index, new WeakReference<T>(value));
    }
    
    /**
     * Get an ArrayList that represents the values contained within this
     * WeakList. Any invalid references are automatically cleaned in the
     * returned List.
     * 
     * @return An ArrayList containing the values of the remaining valid
     *         references from this WeakList.
     */
    public ArrayList<T> values() {
        ArrayList<T> values = new ArrayList<T>();
        
        for (int i = 0; i < size(); i++) {
            WeakReference<T> ref = get(i);
            
            if (ref != null && ref.get() != null) {
                values.add(ref.get());
            }
        }
        
        return values;
    }
    
    /**
     * Get an array that represents the values contained within this WeakList.
     * Any invalid references will have been cleaned from the returned List.
     * 
     * @return An array containing the values of the remaining valid references
     *         from this WeakList.
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
