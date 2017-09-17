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
import dk.dtu.imm.cse.agent.act.util.*;


/**
 * The parse tree for the DO and XEQ condition/statements
 *
 * @author  Henrik Lauritzen
 */
public class Action implements Condition, Statement {

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

	public Action(boolean isDo, String name, List terms) {
		_isDo = isDo;
		_name = name;
		_terms = terms;
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
		for (int i = 0, max = _terms.size(); i < max; i++) {
			((AbstractSyntax)_terms.get(i)).checkSymbols(
					tree, scope, SCOPE_USEONLY);
		}
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		StringBuffer result = new StringBuffer(_isDo ? "DO " : "XEQ ");
		result.append(_name).append("(");
		Util.toString(result, _terms, ", ");
		return result.append(')').toString();
	}
	
	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		b.append(_isDo ? "doAction" : "xeqAction").append('(').
				append(AccUtils.encodeStringLiteral(_name)).append(", ");
		AccUtils.translateTerms(b, _terms, null);
		return b.append(");\n");
	}

	
	public String toJavaCondition(boolean negated) {
		StringBuffer b = new StringBuffer();
		if (negated) {
			b.append('!');
		}
		b.append(_isDo ? "doAction" : "xeqAction").append('(').
				append(AccUtils.encodeStringLiteral(_name)).append(", ");
		AccUtils.translateTerms(b, _terms, null);
		return b.append(')').toString();
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
		
	// indicates whether the statement is DO (true) or XEQ (false)
	private boolean _isDo;
	
	// the name of the action
	private String _name;
	
	// the value terms for the action
	private List _terms;
	
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
