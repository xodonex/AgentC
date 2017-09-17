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
package dk.dtu.imm.cse.agent.act.acc;

/**
 * Specifies operations for parse trees representing language constructs 
 * which may be used as conditions.
 *
 * @author  Henrik Lauritzen
 */
public interface Condition extends AbstractSyntax {

	// =======================================================================
	// Fields
	// =======================================================================

	// =======================================================================
	// Methods
	// =======================================================================
		
	/**
	 * Converts this condition to a Java boolean expression (ie., generate
	 *  code). This may not be possible in all cases; if not, the method
	 *  should return null.
	 * @param negated whether the condition should be negated
	 * @return null iff the condition cannot be translated to a single
	 *   Java boolean expression.
	 */
	public String toJavaCondition(boolean negated);
	
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
