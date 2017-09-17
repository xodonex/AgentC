// Copyright 2002 Henrik Lauritzen.
/*
    This file is part of the AgentC Toolkit.

    The AgentC Toolkit is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The AgentC Toolkit is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the AgentC Toolkit.  If not, see <http://www.gnu.org/licenses/>.
*/
package dk.dtu.imm.cse.agent.act.util;

import java.util.*;

/**
 * The ArrayWrapperList provides a List view of an array of objects. While
 * the list does not support the usual mutating operations, the whole array
 * is allowed to be changed at once. Thus a single ArrayWrapperList instance
 * can be used multiple times instead of creating multiple new list instances
 * through <code>Arrays.asList()</code>.
 *
 * @author  Henrik Lauritzen
 */
public class ArrayWrapperList extends AbstractList implements RandomAccess {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Instance fields
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Constructors
	// =======================================================================
		
	// ------------------------------- public --------------------------------

	/**
	 * Creates a new ArrayWrapperList containing an empty array
	 */
	public ArrayWrapperList() {
		_data = null;
	}


	/**
	 * Creates a new ArrayWrapperList containing the specified data set
	 */
	public ArrayWrapperList(Object[] data) {
		_data = data;
	}
	

	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Replaces the current list data with the new data.
	 */
	public void setData(Object[] data) {
		_data = data;
	}
		
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public Object get(int index) {
		if (_data == null) {
			throw new IndexOutOfBoundsException("" + index);
		}		
		return _data[index];
	}
	
	
	public int size() {
		return _data == null ? 0 : _data.length;
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public Object[] toArray() {
	    return (Object[])_data.clone();
	}

	
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < _data.length; i++) {
				if (_data[i] == null) return i;
			}
		} 
		else {
			for (int i = 0; i < _data.length; i++) {
				if (o.equals(_data[i])) return i;
			}
		}
		return -1;
	}
	
	
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
	
	// the list data
	private Object[] _data;
		
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
