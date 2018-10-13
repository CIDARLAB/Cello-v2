/**
 * Copyright (C) 2017 Massachusetts Inpstitute of Technology (MIT)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cellocad.cello2.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Collection for CObject Object's.
 * @param <T> the type of elements in this collection
 * 
 * @author Vincent Mirian
 * 
 * @date Oct 27, 2017
 *
 */

public class CObjectCollection<T extends CObject> extends CObject implements List<T>, RandomAccess, Cloneable, Serializable{
	
	private void init(){
		collection = new ArrayList<T>();
	}
	
	/**
	 *  Constructs an empty list.
	 */
	public CObjectCollection() {
		super();
		init();
	}

	/**
	 *  Returns the first occurrence of the element with its name equivalent to parameter <i>name</i>.
	 *  
	 *  @param name name of the element to return
	 *  @return the first occurrence of the element with its name equivalent to parameter <i>name</i>.
	 */
	public T findCObjectByName(final String name){
		T rtn = null, cobjTemp = null;
		Iterator<T> cobjIt = collection.iterator();
		while (
				(rtn == null)
				&& (cobjIt.hasNext())
				){
			cobjTemp = cobjIt.next();
			if (cobjTemp.getName().equals(name)){
				rtn = cobjTemp;
			}
		}
		return rtn;
	}
	
	/**
	 *  Returns the first occurrence of the element with its idx equivalent to parameter <i>index</i>.
	 *  
	 *  @param index idx of the element to return
	 *  @return the first occurrence of the element with its idx equivalent to parameter <i>index</i>.
	 */
	public T findCObjectByIdx(final int index){
		T rtn = null, cobjTemp = null;
		Iterator<T> cobjIt = collection.iterator();
		while (
				(rtn == null)
				&& (cobjIt.hasNext())
				){
			cobjTemp = cobjIt.next();
			if (cobjTemp.getIdx() == index){
				rtn = cobjTemp;
			}
		}
		return rtn;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<T> collection;

	/**
	 * Appends the specified element to the end of this list.
	 * 
	 * @param e element to be appended to this list
	 * @return true (as specified by Collection.add(E))
	 */
	@Override
	public boolean add(T e) {
		return collection.add(e);
	}

	/**
	 * Inserts the specified element at the specified position in this list. Shifts the element currently at that position (if any) and any subsequent elements to the right (adds one to their indices).
	 * 
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
	 */
	@Override
	public void add(int index, T element) {
		collection.add(index, element);
	}
	
	/**
	 * Appends all of the elements in the specified collection to the end of this list, in the order that they are returned by the specified collection's Iterator. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress. (This implies that the behavior of this call is undefined if the specified collection is this list, and this list is nonempty.)
	 * 
	 * @param c collection containing elements to be added to this list
	 * @return true if this list changed as a result of the call
	 * 
	 * @throws NullPointerException if the specified collection is null
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return collection.addAll(c);
	}
	
	/**
	 * Inserts all of the elements in the specified collection into this list, starting at the specified position. Shifts the element currently at that position (if any) and any subsequent elements to the right (increases their indices). The new elements will appear in the list in the order that they are returned by the specified collection's iterator.
	 * 
	 * @param index index at which to insert the first element from the specified collection
	 * @param c collection containing elements to be added to this list
	 * @return true if this list changed as a result of the call
	 * 
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
	 * @throws NullPointerException if the specified collection is null
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return collection.addAll(index, c);
	}
	
	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns.
	 */
	@Override
	public void clear() {
		collection.clear();
	}
	
	/**
	 * Returns true if this list contains the specified element. More formally, returns true if and only if this list contains at least one element e such that (o==null ? e==null : o.equals(e)).
	 * 
	 * @param o element whose presence in this list is to be tested
	 * @return true if this list contains the specified element 
	 */
	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}
	
	/**
	 * Returns true if this collection contains all of the elements in the specified collection.
This implementation iterates over the specified collection, checking each element returned by the iterator in turn to see if it's contained in this collection. If all elements are so contained true is returned, otherwise false.
	 * 
	 * @param c collection to be checked for containment in this collection
	 * @return true if this collection contains all of the elements in the specified collection
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}
	
	/**
	 * Returns the element at the specified position in this list.
	 * 
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
	 */
	@Override
	public T get(int index) {
		return collection.get(index);
	}
	
	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element. More formally, returns the lowest index i such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 * 
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element
	 */
	@Override
	public int indexOf(Object o) {
		return collection.indexOf(o);
	}
	
	/**
	 * Returns true if this list contains no elements.
	 * 
	 * @return true if this list contains no elements
	 */
	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}
	
	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 * 
	 * @return an iterator over the elements in this list in proper sequence
	 */
	@Override
	public Iterator<T> iterator() {
		return collection.iterator();
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not contain the element. More formally, returns the highest index i such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there is no such index.
	 * 
	 * @param o element to search for
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does not contain the element
	 */
	@Override
	public int lastIndexOf(Object o) {
		return collection.lastIndexOf(o);
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper sequence). The returned list iterator is fail-fast.
	 * 
	 * @return a list iterator over the elements in this list (in proper sequence)
	 */
	@Override
	public ListIterator<T> listIterator() {
		return collection.listIterator();
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper sequence), starting at the specified position in the list. The specified index indicates the first element that would be returned by an initial call to next. An initial call to previous would return the element with the specified index minus one. The returned list iterator is fail-fast.
	 * 
	 * @return a list iterator over the elements in this list (in proper sequence), starting at the specified position in the list
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
	 */
	@Override
	public ListIterator<T> listIterator(int index) {
		return collection.listIterator(index);
	}
	
	/**
	 * Removes the first occurrence of the specified element from this list, if it is present. If the list does not contain the element, it is unchanged. More formally, removes the element with the lowest index i such that (o==null ? get(i)==null : o.equals(get(i))) (if such an element exists). Returns true if this list contained the specified element (or equivalently, if this list changed as a result of the call).
	 * 
	 * @param o element to be removed from this list, if present
	 * @return true if this list contained the specified element
	 */
	@Override
	public boolean remove(Object o) {
		return collection.remove(o);
	}
	
	/**
	 * Removes the element at the specified position in this list. Shifts any subsequent elements to the left (subtracts one from their indices).
	 * 
	 * @param index the index of the element to be removed
	 * @return the element that was removed from the list
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
	 */
	@Override
	public T remove(int index) {
		return collection.remove(index);
	}

	/**
	 * Removes from this list all of its elements that are contained in the specified collection.
	 * 
	 * @param c collection containing elements to be removed from this list
	 * @return true if this list changed as a result of the call
	 * @throws ClassCastException if the class of an element of this list is incompatible with the specified collection (optional)
	 * @throws NullPointerException if this list contains a null element and the specified collection does not permit null elements (optional), or if the specified collection is null
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return collection.removeAll(c);
	}

	/**
	 * Retains only the elements in this list that are contained in the specified collection. In other words, removes from this list all of its elements that are not contained in the specified collection.
	 * 
	 * @param c collection containing elements to be retained in this list
	 * @return true if this list changed as a result of the call
	 * @throws ClassCastException if the class of an element of this list is incompatible with the specified collection (optional)
	 * @throws NullPointerException if this list contains a null element and the specified collection does not permit null elements (optional), or if the specified collection is null
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return collection.retainAll(c);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * 
	 * @param index index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException - if the index is out of range (index < 0 || index >= size())
	 */
	@Override
	public T set(int index, T element) {
		return collection.set(index, element);
	}

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list
	 */
	@Override
	public int size() {
		return collection.size();
	}

	/**
	 * Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive. (If fromIndex and toIndex are equal, the returned list is empty.) The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list, and vice-versa. The returned list supports all of the optional list operations.
	 * 
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException if an endpoint index value is out of range (fromIndex < 0 || toIndex > size)
	 * @throws IllegalArgumentException if the endpoint indices are out of order (fromIndex > toIndex)
	 */
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return collection.subList(fromIndex, toIndex);
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element).<br>
	 * 
	 * The returned array will be "safe" in that no references to it are maintained by this list. (In other words, this method must allocate a new array). The caller is thus free to modify the returned array.<br>
	 * 
	 * This method acts as bridge between array-based and collection-based APIs.
	 * 
	 * @return an array containing all of the elements in this list in proper sequence
	 */
	@Override
	public Object[] toArray() {
		return collection.toArray();
	}

	/**
	 * Returns an array containing all of the elements in this list in proper sequence (from first to last element); the runtime type of the returned array is that of the specified array. If the list fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the runtime type of the specified array and the size of this list.<br>
	 * 
	 * If the list fits in the specified array with room to spare (i.e., the array has more elements than the list), the element in the array immediately following the end of the collection is set to null. (This is useful in determining the length of the list only if the caller knows that the list does not contain any null elements.)
	 * 
	 * @param a the array into which the elements of the list are to be stored, if it is big enough; otherwise, a new array of the same runtime type is allocated for this purpose.
	 * @return an array containing the elements of the list
	 * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of every element in this list
	 * @throws NullPointerException if the specified array is null
	 */
	@Override
	public <Type> Type[] toArray(Type[] a) {
		return collection.toArray(a);
	}

	/*
	 * HashCode
	 */
	/**
	 * Returns the hash code value for this list.<br>
	 * 
	 * This implementation uses exactly the code that is used to define the list hash function in the documentation for the List.hashCode() method.<br>
	 * 
	 * @return the hash code value for this list
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((collection == null) ? 0 : collection.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	/**
	 * Compares the specified object with this list for equality. Returns true if and only if the specified object is also a list, both lists have the same size, and all corresponding pairs of elements in the two lists are equal. (Two elements e1 and e2 are equal if (e1==null ? e2==null : e1.equals(e2)).) In other words, two lists are defined to be equal if they contain the same elements in the same order.<br>
	 * 
	 * This implementation first checks if the specified object is this list. If so, it returns true; if not, it checks if the specified object is a list. If not, it returns false; if so, it iterates over both lists, comparing corresponding pairs of elements. If any comparison returns false, this method returns false. If either iterator runs out of elements before the other it returns false (as the lists are of unequal length); otherwise it returns true when the iterations complete.
	 * 
	 * @param obj the object to be compared for equality with this list
	 * @return true if the specified object is equal to this list
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!(obj instanceof CObjectCollection<?>)) {
			return false;
		}
		CObjectCollection<?> other = (CObjectCollection<?>) obj;
		if (collection == null) {
			if (other.collection != null)
				return false;
		} else if (!collection.equals(other.collection))
			return false;
		return true;
	}

	/*
	 * toString
	 */
	/**
	 * Returns a string representation of the elements in this collection. Elements are represented by their respective <i>toString()</i> method.
	 * 
	 * @return a string representation of this collection
	 */
	protected String getCollectionElementToString() {
		String rtn = "";
		for (int i = 0; i < this.size(); i++) {
			rtn = rtn + Utils.getTabCharacter();	
			T element = this.get(i);
			rtn = rtn + element.toString();
		}
		return rtn;
	}

	/**
	 * Returns a string representation of this collection. Collection is enclosed by curly brackets ("{}").
	 * 
	 * @return a string representation of this collection
	 */
	@Override
	public String toString() {
		String rtn = "";
		rtn = rtn + "collection = ";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + "{";
		rtn = rtn + Utils.getNewLine();
		rtn = rtn + this.getCollectionElementToString();
		rtn = rtn + "}";
		rtn = rtn + Utils.getNewLine();	
		return rtn;
	}
	
}
