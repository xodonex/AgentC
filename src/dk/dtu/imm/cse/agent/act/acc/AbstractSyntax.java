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

import java.util.Map;


/**
 * Defines the common operations for all syntax tree components.
 *
 * @author  Henrik Lauritzen
 */
public interface AbstractSyntax {

	// =======================================================================
	// Fields
	// =======================================================================

	/**
	 * Indicates a scope where variable uses as well as defs are allowed.
	 */
	public int SCOPE_DEFUSE = 0;
	
	
	/**
	 * Indicates a scope where only variable uses are allowed.
	 */
	public int SCOPE_USEONLY = 1;
	
	
	/**
	 * Indicates a scope where no variable occurrences are allowed.
	 */
	public int SCOPE_NOVARS = 2;
	
	
	// =======================================================================
	// Methods
	// =======================================================================

	/**
	 * This method checks the semantics of the symbols used in the represented
	 *  abstract syntax tree. The checks to be performed are the following
	 *  <ol>
	 *  <li value="1">Check that all occurrences of constant names correspond
	 *   to an existing definition
	 *  <li value="2">Classify variables (def/use)
	 *  <li value="3">Check that variables are legally used according to their
	 *   classification
	 * </ol>
	 * @param tree the parse tree
	 * @param scope the variable scope of <em>the enclosing syntax tree</em>
	 * @param scopeType an indication of the type of the enclosing syntax tree
	 *  (the value should be one of the SCOPE_xxx constants)
	 * @exception ParseException if a semantical error is detected.
	 */
	public void checkSymbols(ParseTree tree, Map scope, int scopeType) 
			throws ParseException;
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
