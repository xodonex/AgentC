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

import java.util.*;


/**
 * The parse tree for a LET statement
 *
 * @author  Henrik Lauritzen
 */
public class LetStatement implements Statement, AccConstants {

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

	public LetStatement(Variable v, Value binding) {
		_v = v;
		_binding = binding;
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
	
	public void checkSymbols(ParseTree tree, Map scope, int scopeType)
			throws ParseException {
		// check the binding first, in the original scope
		_binding.checkSymbols(tree, scope, SCOPE_USEONLY);
			
		// classify the variable as a def and modify the scope accordingly
		if (!_v.isWildcard()) {
			_v.classifyAsDef(scope, true);
		}
	}
	
	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		return generateCode(indentLevel, b, _v, _binding.toJavaExpression());
	}	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return "LET " + _v + " = " + _binding + ";";
	}

	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Generate code for a new variable binding.
	 * @param indentLevel the indentation level
	 * @param b the string buffer to which the code is appended
	 * @param var the variable
	 * @param binding the value to be bound to the variable
	 * @return b
	 * @see #generateCode(int, StringBuffer)
	 */
	public static StringBuffer generateCode(int indentLevel, StringBuffer b,
			Variable var, String binding) {
		AccUtils.indent(b, indentLevel);
		if (var.isWildcard()) {
			b.append(TEMP_OBJ_NAME);
		}
		else {
			if (var.isDef()) {
				b.append("Object ");
			}
			b.append(var.toJavaExpression());
		}
		return b.append(" = ").append(binding).append(";\n");				
	}
	
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
	
	// the variable to be bound
	private Variable _v;
	
	// the bound value
	private Value _binding;
		
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
