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
 * The NeighbourGraph represents an unweighted, directed graph using an 
 * internal neighbour-list representation.
 * The implementation is not synchronized.
 *
 * @author  Henrik Lauritzen
 */
public class NeighbourGraph implements java.io.Serializable {

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
	
	public NeighbourGraph() {
		_data = new HashMap();
		_ids = new HashMap();
		_vs = new TreeMap();
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * @return the index in the graph of the given vertex. The return value
	 *  is negative iff the vertex does not exist in the graph.
	 */
	public int indexOf(Object vertex) {
		Integer i = (Integer)_ids.get(vertex);
		return i == null ? -1 : i.intValue();
	}
	
	
	/**
	 * @return the vertex having the given index, or null if the vertex
	 *  does not exist.
	 */
	public Object getVertex(int v) {
		return _vs.get(new Integer(v));
	}
	

	/**
	 * Ensure that the given vertex is assigned an id.
	 * @param v the vertex to be added to the vertex table
	 * @return the ID given to the vertex. 
	 * @see #getVertex(int)
	 * @see #indexOf(Object)
	 */
	public int addVertex(Object v) {
		Integer id = (Integer)_ids.get(v);
		if (id == null) {
			id = new Integer(_ids.size());
			_ids.put(v, id);
			_vs.put(id, v);
		}
		return id.intValue();
	}
	
	
	/**
	 * Adds a new edge to the graph.
	 * @param from the origin of the edge
	 * @param to the destination of the edge
	 * @return true iff the edge did not already exist
	 */
	public boolean add(Object from, Object to) {
		Collection c = (Collection)_data.get(from);		

		if (c == null) {
			addVertex(from);
			addVertex(to);
			c = createNeighbourList();
			_data.put(from, c);
			c.add(to);
			return true;
		}
		else if (c.contains(to)) {
			return false;
		}

		c.add(to);
		addVertex(to);
		return true;
	}
	
	
	/**
	 * @return a boolean indicating whether the graph holds the edge 
	 * (from, to).
	 */
	public boolean contains(Object from, Object to) {
		Collection c = (Collection)_data.get(from);
		return c == null ? false : c.contains(to);
	}
	
	
	/**
	 * Retreive the vertices of the map.
	 * @param container the container to which the values will be added.
	 *  If the given contiainer is null, a new container will be created.
	 * @return the modified or newly created collection of vertices.
	 */
	public Collection getVertices(Collection container) {
		if (container == null) {
			return new ArrayList(_vs.values());
		}
		else {
			container.addAll(_vs.values());
			return container;
		}
	}
	
	
	/**
	 * @return the number of vertices in the graph
	 */
	public int size() {
		return _data.size();
	}
	
	
	/**
	 * @return the degree of the given vertex. The return value is
	 *  negative iff the vertex does not exist in the graph
	 */
	public int degreeOf(Object vertex) {
		Collection c = (Collection)_data.get(vertex);
		return c == null ? -1 : c.size();
	}
		

	/**
	 * @return the vertices reachable from vertex, or null if the vertex
	 *  does not exist in the graph.
	 */
	public List getNeighbours(Object vertex) {
		Collection c = (Collection)_data.get(vertex);
		if (c == null) {
			return null;
		}
		else {
			return new ArrayList(c);
		}
	}

	
	/**
	 * Calculate the shortest distance between all pairs of vertices
	 *  in the graph. The operation runs in O(|V|^3) time for a graph
	 *  having |V| vertices.
	 */
	public int[][] calcDistances() {
		//  Floyd-Warshall, CLR p. 564		
		int n = _data.size();
		int[][] D = new int[n][];
		
		// create the initial distance matrix
		int[] row = D[0] = new int[n];
		Arrays.fill(row, Integer.MAX_VALUE);		
		for (int i = 1; i < n; i++) {
			D[i] = (int[])row.clone();
		}
				
		
		Iterator ei = _vs.entrySet().iterator();
		
		for (int i = 0; i < n; i++) {
			Map.Entry e = (Map.Entry)ei.next();
			
			row = D[i];
			row[i] = 0;
						
			for (Iterator it = ((Collection)_data.get(e.getValue())).iterator(); 
					it.hasNext(); ) {
				row[indexOf(it.next())] = 1;
			}
		}
		
		long d;

		for (int k = 0; k < n; k++) {			// for k <- 0 to n-1
			for (int i = 0; i < n; i++) {		//   do for i <- 0 to n-1
				for (int j = 0; j < n; j++)	{	//	   do for j <- 0 to n-1
					d = (long)D[i][k] + D[k][j];
					if (D[i][j] > d) {
						D[i][j] = (int)d;		//	     do Dij = min(Dij, Dik + Dkj)
					}
				}
			}
		}
		
		return D;								// return D
	}

	// ------------------------------ protected ------------------------------	
	
	/**
	 * Creates a new, empty neighbour list instance.
	 * The default implementation uses a {@link HashSet}
	 */
	protected Collection createNeighbourList() {
		return new HashSet();
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

	// Maps a vertex to a List of neighbours
	private Map _data;
	
	// Maps a vertex to a vertex ID
	private Map _ids;

	// Maps a vertex ID to a vertex
	private SortedMap _vs;
	
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
