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
 * An AgentC statement.
 *
 * @author  Henrik Lauritzen
 */
public interface Statement extends AbstractSyntax {

	// =======================================================================
	// Fields
	// =======================================================================

	// =======================================================================
	// Methods
	// =======================================================================
		
	/**
	 * Generate Java code for the statement. This will only be valid if
	 *  a semantical check has been performed on the parse tree prior
	 *  to the code generation.
	 * @param indentLevel determines how many indentations to use on
	 *  a given program line.
	 * @param b the buffer to which code will be appended
	 * @return b
	 */
	public StringBuffer generateCode(int indentLevel, StringBuffer b);
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
