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
package dk.dtu.imm.cse.agent.act.afc;

import java.io.*;
import java.util.*;

import dk.dtu.imm.cse.agent.act.util.*;


/**
 * The Fact data type represents a DATALOG fact. <em>All contained
 * terms must, as the Fact itself, be immutable and serializable</em>.
 *
 * @author  Henrik Lauritzen
 */
public class Fact implements Serializable {

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
	 * Creates a new Fact instance.
	 * @param category the categoy of the fact (the ACME uses on of the
	 *   ATTITUDE_xxx codes here).
	 * @param name the name (predicate symbol) of the fact
	 * @param terms the list of terms. The value null may be used to indicate
	 *  an empty list of terms.
	 */
	public Fact(int category, String name, Object[] terms) {
		_category = category;		
		
		if ((_name = name) == null || _name.length() == 0) {
			throw new IllegalArgumentException(name);
		}
		// store the interned string to save memory
		_name = name.intern();
		
		if ((_terms = terms) == null || _terms.length == 0) {
			// use a shared, empty array in order to save memory
			_terms = Util.NO_OBJECTS;
		}		
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the category code for the fact.
	 */
	public int getCategory() {
		return _category;
	}
	
	
	/**
	 * @return the name (predicate symbol) of the fact.
	 */
	public String getName() {
		return _name;
	}

	
	/**
	 * @return arity (number of terms) of this fact.
	 */
	public int getArity() {
		return _terms.length;
	}
	
	
	/**
	 * Retreive a single term from the list of terms.
	 * @param idx the index of the term to be retreived
	 * @exception IndexOutOfBoundsException if the index is subzero or
	 *  is at or above the {@link #getArity arity} of the fact.
	 */
	public Object getTerm(int idx) throws IndexOutOfBoundsException {
		return _terms[idx];		
	}
	
	
	/**
	 * Retreives all of the fact's terms at once.
	 * @param container the container to which the terms will be added.
	 *  If the given container is null, a new container will be created.
	 * @return the container used to store the terms.
	 */
	public Collection getTerms(Collection container) {
		if (container == null) {
			container = new ArrayList(getArity());
		}
		
		for (int i = 0; i < _terms.length; i++) {
			container.add(_terms[i]);				
		}
		
		return container;
	}

	
	/**
	 * @see #matches(int, String, Object[], BitSet)
	 */
	public boolean matches(Fact f, BitSet vars) {
		return matches(f._category, f._name, f._terms, vars);
	}
	 
	
	/**
	 * Determine whether this Fact matches another, given a set of variable
	 *  indices (ie., a set of the locations to omit from the match).
	 * @param category the category code to be matched
	 * @param name the predicate symbol to be matched
	 * @param terms the list of terms to be matched
	 * @param vars contains the locations of variables (terms to be omitted 
	 *  from the match) as the indices of the bits that are set. If the value
	 *  is null, all terms will be considered in the match.
	 * @return true iff this fact matches the specified values.
	 * @see #matchTerms(Object[], Object[], BitSet)
	 */	
	public boolean matches(int category, String name, Object[] terms, 
			BitSet vars) {
		if (vars == null) {
			return equals(category, name, terms);
		}
		
		if (_category != category || (_name != name && !_name.equals(name))) {
			return false;
		}
		
		return matchTerms(_terms, terms, vars);		
	}
	 
	
	/**
	 * Determine whether this fact contains the same category, name and
	 *  terms as the given values.
	 * @param category the category to be checked
	 * @param name the name to be checked
	 * @param terms the terms to be checked
	 * @return true iff 
	 * <pre>{@link #equals(Object) equals}(new Fact(category, name, terms))</pre>
	 */
	public boolean equals(int category, String name, Object[] terms) {
		if (_category != category || 
				(_name != name && !_name.equals(name))) {
			return false;
		}
		else if (terms == null) {
			return _terms.length == 0;
		}
		
		for (int i = 0; i < _terms.length; i++) {
			if (_terms[i] == null) {
				if (terms[i] != null) return false;
			}
			else {
				if (!_terms[i].equals(terms[i])) return false;
			}
		}		
		
		return true;
	}
	
	// ------------------------------ protected ------------------------------
	
	/**
	 * For efficiency reasons, the KnowledgeBase implementations in this
	 *  package are allowed to access the term list directly; of course,
	 *  this list should *not* be modified.
	 */
	Object[] getTermList() {
		return _terms;
	}
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append('[').append(_category).append(']');
		buf.append(_name);
		buf.append('(');
		if (_terms.length > 0) {
			Util.toString(buf, _terms, ", ");
		}
		buf.append(')');
		return buf.toString();
	}
	
	
	public synchronized int hashCode() {
		if (_isHashed) {
			return _hashCode;
		}
		
		_hashCode = _category * 31 + _name.hashCode();

		for (int i = 0; i < _terms.length; i++) {
			_hashCode *= 31;
			if (_terms[i] != null) {
				_hashCode += _terms[i].hashCode();
			}
		} 

		_isHashed = true;
		return _hashCode;
	}
	
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Fact)) {
			return false;
		}
		
		Fact f = (Fact)obj;
		if (hashCode() != f.hashCode()) {
			return false;
		}
		
		return equals(f._category, f._name, f._terms);
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Match two lists of terms against each other, possibly omitting some
	 *  of the terms.
	 * @param t1 the first list of terms to be matched
	 * @param t2 the second list of terms to be matched
	 * @param vars contains the locations of variables (terms to be omitted 
	 *  from the match) as the indices of the bits that are set. 
	 * @return true iff the two term lists match, that is, if they have 
	 *  equal lengths and if all pairs of terms at an index for which
	 *  the bit set contains a clear bit, are {@link Object#equals(Object) equal}.
	 * @see #matchTerms(List, List, BitSet)
	 */
	public static boolean matchTerms(Object[] t1, Object[] t2, BitSet vars) {
		if (t1 == null) {
			return t2 == null || t2.length == 0;
		}

		int max = t1.length;
		if (t2 == null || t2.length != max) {
			return false;
		}
		
		int idx = 0;		
		while ((idx = vars.nextClearBit(idx)) >= 0 && idx < max) {
			if (t1[idx] == null) {
				if (t2[idx] != null) return false;
			}
			else {
				if (!t1[idx].equals(t2[idx])) {
					return false;
				}
			}		
			idx++;
		}
		
		return true;
	}
	

	/**
	 * An alternative version of {@link #matchTerms(Object[], Object[], BitSet)}.
	 */
	public static boolean matchTerms(List l1, List l2, BitSet vars) {
		if (l1 == null) {
			return l2 == null || l2.size() == 0;
		}

		int max = l1.size();
		if (l2 == null || l2.size() != max) {
			return false;
		}
		
		int idx = 0;		
		while ((idx = vars.nextClearBit(idx)) >= 0 && idx < max) {
			Object obj = l1.get(idx);
			if (obj == null) {
				if (l2.get(idx) != null) return false;
			}
			else {
				if (!obj.equals(l2.get(idx))) {
					return false;
				}
			}		
			idx++;
		}
		
		return true;
	}
	
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	// the category code.
	// In the ACME, this serves to identify the modality (DID, BELIEVE)
	// when the Fact is transferred in messages.
	private int _category;
	
	// the predicate symbol.
	// The value is interned in order to save memory space and to 
	// speed up .equals() 
	private String _name;
	
	// the list of terms; the value null is used to represent an empty
	// term list, in order to avoid keeping a potentially large number
	// of different array instances in memory.
	private Object[] _terms;
	
	// the cached hash code; this is calculated when first needed.
	// The value is transient in order to avoid serializing redundant
	// data.
	private transient int _hashCode;
	
	// whether the _hashCode has been calculated yet
	private transient boolean _isHashed = false;
	
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
	
	private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		// intern the predicate symbol when deserializing
		_name = _name.intern();
	}
	
 	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
