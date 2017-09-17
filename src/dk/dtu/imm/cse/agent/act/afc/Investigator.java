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

/**
 * The Investigator is a "plug-in" for the {@link Acme}, which allows custom 
 * Java code can be accessed from within AgentC through the QUERY primitive.
 *
 * @author  Henrik Lauritzen
 */
public interface Investigator {

	// =======================================================================
	// Fields
	// =======================================================================

	// =======================================================================
	// Methods
	// =======================================================================
		
	/**
	 * Answer a query; this corresponds to the QUERY primitive
	 * in AgentC.
	 * @param q an identification of the query
	 * @param params the parameters for the query
	 * @return an answer to the query; the type and value of this answer
	 *  depends on the specific implementation.
	 */
	public Object query(String q, Object[] params);
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
