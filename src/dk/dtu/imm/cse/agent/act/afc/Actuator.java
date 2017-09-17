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
 * The Actuator is a "plug-in" for the {@link Acme ACME}, such that the agent's
 * actions can be carried out in the specific environment in which it is 
 * situated.
 *
 * @author  Henrik Lauritzen
 */
public interface Actuator {

	// =======================================================================
	// Fields
	// =======================================================================

	// =======================================================================
	// Methods
	// =======================================================================

	/**
	 * Execute an action; this corresponds to both the DO and XEQ primitives
	 * in AgentC.
	 * @param name the name of the action
	 * @param params the action's parameters.
	 * @return true iff the action succeeds.
	 */
	public boolean xeq(String name, Object[] params);
	
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
