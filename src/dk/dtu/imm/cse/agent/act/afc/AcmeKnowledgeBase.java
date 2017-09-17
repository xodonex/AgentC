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

import java.util.*;

import dk.dtu.imm.cse.agent.act.util.*;


/**
 * A KnowledgeBase implementation which is optimized to be used as the ACME's
 * knowledge base. Internally, data are structured in such a way that 
 * {@link #add(Fact)}, {@link #remove(Fact)} and {@link #contains(Fact)}
 * can be executed in O(a) time for a Fact of arity a. At the same time,
 * {@link #match(Fact, BitSet)} can be executed in O(n * a) time, given
 * a Fact of arity a and a knowledge base containing n entries having
 * the same category and predicate symbol as the given fact, regardless
 * of the total size of the base. This makes the AcmeKnowledgeBase well
 * suited as the ACME's main knowledge base, since the performance of 
 * <code>match()</code> is not degraded when the base contains many
 * different kinds of facts, as the main knowledge base most likely will.
 * However, the price is a relatively low iteration performance.
 *
 * @author  Henrik Lauritzen
 */
public class AcmeKnowledgeBase implements KnowledgeBase {

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
	 * Constructs an initially empty knowledge base which allows
	 *  only one attitude (0).
	 */
	public AcmeKnowledgeBase() {
		this(1);
	}
	
	
	/**
	 * Constructs an initially empty knowledge base which allows
	 *  attitudes 0 through size.
	 */
	public AcmeKnowledgeBase(int size) {
		_base = new Map[size];
		for (int i = 0; i < _base.length; i++) {
			_base[i] = new HashMap();
		}
		_size = 0;
	}
		
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public synchronized boolean isEmpty() {
		return _size == 0;
	}

	
	public synchronized int size() {
		return _size;
	}
	
	
	public synchronized void clear() {
		for (int i = 0; i < _base.length; i++) {
			_base[i].clear();
		}
		_size = 0;
	}
	

	public Object getLock() {
		return this;
	}
	
	
	/**
	 *  Note: if the knowledge base is used by multiple
	 *  threads, the iterator must be created and accessed under
	 *  synchronization on the knowledge base itself.
	 */
	public Iterator iterator() {
		return new Iterator() {
			private int _id = 0;
			private Iterator _mapIterator = _base[_id].entrySet().iterator();
			private String _name = null;
			private Set _set = null;
			private Iterator _setIterator = null;
			private boolean _removable = false;
			
			private int _current = 0, _max = _size - 1;
			
			
			public boolean hasNext() {
				return _current <= _max;
			}
			
			
			public Object next() {
				if (_current > _max) {
					throw new NoSuchElementException();
				}
				
				while (_setIterator == null || !_setIterator.hasNext()) {
					while (!_mapIterator.hasNext()) {
						_mapIterator = _base[++_id].entrySet().iterator();
					}
					
					Map.Entry e = (Map.Entry)_mapIterator.next();
					_name = (String)e.getKey();
					_set = (Set)e.getValue();
					_setIterator = _set.iterator();
				}
				
				_current++;
				_removable = true;
				return new Fact(_id, _name, 
						((List)_setIterator.next()).toArray());
			}
			
			
			public void remove() {
				if (!_removable) {
					throw new IllegalStateException();
				}
				
				_setIterator.remove();
				if (_set.size() == 0) {
					_mapIterator.remove();
				}
				
				_size--;
				_removable = false;
					
			}
			
		};
	}
	

	public boolean add(Fact f) {		
		return add(f.getCategory(), f.getName(), f.getTermList());
	}

	
	public synchronized boolean add(int category, String name, Object[] terms) {
		Map m = _base[category];
		List ts = makeStorageList(terms);
		Set b = (Set)m.get(name);

		if (b == null) {
			m.put(name, b = new LinkedHashSet());
		}		
		if (b.add(ts)) {
			_size++;
			return true;
		}
		else {
			return false;
		}
	}
	

	public boolean contains(Fact f) {
		return contains(f.getCategory(), f.getName(), f.getTermList());
	}
	
	
	public boolean contains(int category, String name, Object[] terms) {
		if (category < 0 || category >= _base.length) {
			return false;
		}
		synchronized (this) {
			Set b = (Set)_base[category].get(name);
			return b == null ? false : b.contains(makeLookupList(terms));
		}
	}
	
	
	public boolean remove(Fact f) {
		return remove(f.getCategory(), f.getName(), f.getTermList());
	}

	
	public boolean remove(int category, String name, Object[] terms) {
		if (category < 0 || category >= _base.length) {
			return false;
		}
	
		synchronized (this) {
			Map m = _base[category];
			Set b = (Set)m.get(name);
			if (b == null) {
				return false;
			}		
			if (b.remove(makeLookupList(terms))) {
				if (b.size() == 0) {
					m.remove(name);
				}
				_size--;
				return true;
			}
			else {
				return false;
			}
		}
	}

	
	public int remove(Fact f, BitSet vars) {
		return remove(f.getCategory(), f.getName(), f.getTermList(), vars);
	}
	
	
	public int remove(int category, String name, Object[] terms, BitSet vars) {
		if (category < 0 || category >= _base.length) {
			return 0;
		}
		int removed = 0;

		synchronized (this) {
			Map m = _base[category];
			Set b = (Set)m.get(name);
			if (b == null) {
				return 0;
			}		

			List matchList = makeLookupList(terms);
			for (Iterator i = b.iterator(); i.hasNext(); ) {
				if (Fact.matchTerms((List)i.next(), matchList, vars)) {
					i.remove();
					removed++;
				}
			}

			_size -= removed;
		}
		return removed;
	}

	
	public List match(Fact f, BitSet vars) {
		return match(f.getCategory(), f.getName(), f.getTermList(), vars);
	}
	

	public List match(int category, String name, Object[] terms, 
			BitSet vars) {
		if (category < 0 || category >= _base.length) {
			return Collections.EMPTY_LIST;
		}
		
		List result = new ArrayList();
		synchronized (this) {
			Set lookupSet = (Set)(_base[category].get(name));
			if (lookupSet == null) {
				return Collections.EMPTY_LIST;
			}

			List matchList = makeLookupList(terms);
			for (Iterator i = lookupSet.iterator(); i.hasNext(); ) {
				List ts = (List)i.next();
				if (Fact.matchTerms(matchList, ts, vars)) {
					result.add(new Fact(category, name, ts.toArray()));
				}
			}
		}
	
		return result;
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
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
	
	// The contents of the knowledge base.
	// Each index, which corresponds to an attitude id,
	// contains a map of predicate symbol (String) keys and
	// Set values, each of which contain term lists (List).
	private Map[] _base;
	
	// the size of the knowledge base; this needs to be maintained separately
	// for efficiency reasons.
	private int _size;
	
	// a shared list instance used for lookup operations
	private ArrayWrapperList _lookupList = new ArrayWrapperList();
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	
	// wrap an array of terms as a list which can be stored in the base.
	private static List makeStorageList(Object[] terms) {
		return (terms == null || terms.length == 0) ? 
				Collections.EMPTY_LIST : Arrays.asList(terms);
	}
	
	// ------------------------------ instance -----------------------------

	// wrap an array of terms as a list which can be used to probe the 
	// knowledge base; in this case, the shared ListWrapper instance
	// is used in order to avoid creating a new list
	private List makeLookupList(Object[] terms) {
		_lookupList.setData(terms);
		return _lookupList;
	}
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}