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


/**
 * A KnowledgeBase is a data structure which holds a set of ground facts.
 * While the {@link Fact} data structure is used for knowledge base I/O,
 * the knowledge base may choose to internally represent the data in 
 * any suitable form.
 *
 * @author  Henrik Lauritzen
 */
public interface KnowledgeBase extends java.io.Serializable {

	// =======================================================================
	// Fields
	// =======================================================================

	
	// =======================================================================
	// Methods
	// =======================================================================
	
	
	/**
	 * Determine whether the knowledge base is empty
	 * @return true iff the knowledge base is empty
	 * @see #size()
	 */
	public boolean isEmpty();
	
	
	/**
	 * @return the number of different facts held by the knowledge base.
	 * @see #isEmpty()
	 */
	public int size();
	

	/**
	 * @return an Iterator which returns every fact contained in the knowledge
	 *  base. Every value returned by the iterator should be {@link Fact}
	 *  instances. The returned iterator should preferrably support the 
	 *  {@link Iterator#remove() remove()} operation.
	 * There is no requirement that the iterator should detect 
	 * concurrent modifications; in general, the only safe use of this method
	 * is from within a <code>synchronized</code> block using the lock
	 * returned by {@link #getLock()}
	 */
	public Iterator iterator();
	
	
	/**
	 * Empties the entire contents of the knowledge base
	 */
	public void clear();

	
	/**
	 * Returns the synchronization lock used by this knowledge base. It
	 * is required that <em>all operations except {@link #iterator()}</em>
	 * must be synchronized on this lock.
	 */
	public Object getLock();
	
	
	/**
	 * Determines whether the knowledge base contains the given fact.
	 * @param f the fact whose existence should be determined.
	 * @return true iff the knowledge base contains the given fact.
	 */
	public boolean contains(Fact f);
	
	
	/**
	 * An alternative form of the {@link #contains(Fact)} method. This method is 
	 * specified for performance reasons, since it may save the 
	 * instantiation of a Fact in some KnowledgeBase implementations.
	 * @see #contains(Fact)
	 */
	public boolean contains(int category, String name, Object[] terms);

	
	/**
	 * Ensures that a fact is present in the knowledge base.
	 * @param f the fact. 
	 * @return true iff the knowledge base was changed (ie., if the fact was
	 *  added) as a result of the invocation.
	 * @exception NullPointerException if the fact is null.
	 */
	public boolean add(Fact f) throws NullPointerException;

	
	/**
	 * An alternative form of the {@link #add(Fact)} method. This method is 
	 * specified for performance reasons, since it may save the 
	 * instantiation of a Fact in some KnowledgeBase implementations.
	 * @see #add(Fact)
	 */
	public boolean add(int category, String name, Object[] terms)
			throws IndexOutOfBoundsException, NullPointerException;

	
	/**
	 * Ensures that a fact is not present in the knowledge base.
	 * @param f the fact. 
	 * @return true iff the knowledge base was changed (ie., if the fact was
	 *  removed) as a result of the invocation.
	 * @exception NullPointerException if the fact is null.
	 */
	public boolean remove(Fact f) throws NullPointerException;

	
	/**
	 * An alternative form of the {@link #remove(Fact)} method. This method is 
	 * specified for performance reasons, since it may save the 
	 * instantiation of a Fact in some KnowledgeBase implementations.
	 * @see #remove(Fact)
	 */
	public boolean remove(int category, String name, Object[] terms)
			throws NullPointerException;


	/**
	 * Removes the facts {@link #match(Fact, BitSet) matching} the given
	 *  fact and variable positions.
	 * @return the number of facts removed from the knowledge base
	 */
	public int remove(Fact f, BitSet vars) throws NullPointerException;

	
	/**
	 * An alternative form of the {@link #remove(Fact, BitSet)} method. 
	 * This method is specified for performance reasons, since it may save the 
	 * instantiation of a Fact in some KnowledgeBase implementations.
	 * @see #remove(Fact, BitSet)
	 */
	public int remove(int category, String name, Object[] terms,
			BitSet vars) throws NullPointerException;

	
	/**
	 * Derive a subset of this knowledge base, such that the derived
	 * knowledge base contains only the facts matched by the parameters.
	 * @param f the base pattern to be matched. A successful match will
	 *  require a matching
	 *  {@link Fact#getCategory() category code} and
	 *  {@link Fact#getName() predicate symbol}, and a successful
	 *  {@link Object#equals(Object) .equals()}-comparison of the involved
	 *  terms.
	 * @param vars determines which positions in the fact should be treated
	 *  as variables in the matching (ie., which terms should not be
	 *  considered in the matching). For every term t at index i, t<sub>i</sub>,
	 *  <code>vars.get(i)</code> determines whether t<sub>i</sub> should
	 *  have significance in the matching. If vars has the value null this
	 *  will have the effect that every term will be part of the match.
	 * @return a list containing Fact instances, all of which match the given
	 *  fact.
	 * @exception NullPointerException if the fact is null.
	 */
	public List match(Fact f, BitSet vars) throws NullPointerException;
	
	
	/**
	 * An alternative form of the {@link #match(Fact, BitSet)} method. 
	 * This method is specified for performance reasons, since it may save the 
	 * instantiation of a Fact in some KnowledgeBase implementations.
	 * @see #match(Fact, BitSet)
	 */
	public List match(int category, String name, Object[] terms,
			BitSet vars) throws NullPointerException;

	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}