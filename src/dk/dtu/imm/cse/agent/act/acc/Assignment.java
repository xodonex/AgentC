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
 * The parse tree for an assignment statement
 *
 * @author  Henrik Lauritzen
 */
public class Assignment implements Statement, AccConstants {

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

	public Assignment(Variable v, Value binding) {
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
		if (_v.isWildcard()) {
			AccUtils.throwParseException(_v.getInitialToken(), 
					"Illegal use of wildcard in assignment");
		}

		// verify that the variable is in scope and is assignable
		Variable def = (Variable)scope.get(_v.getName());
		if (def == null) {
			AccUtils.throwParseException(_v.getInitialToken(),
					"Undefined variable " + _v);
		}
		if (!def.isAssignable()) {
			AccUtils.throwParseException(_v.getInitialToken(),
					"Illegal assignment to unassignable variable " + _v);
		}
		
		// classify the variable
		_v.classifyAsUse(def);
		
		// check the binding in a read-only scope
		_binding.checkSymbols(tree, scope, SCOPE_USEONLY);
	}
	
	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		return b.append(_v.toJavaExpression()).append(" = ").
				append(_binding.toJavaExpression()).append(";\n");				
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return _v + " = " + _binding + ";";
	}

	
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