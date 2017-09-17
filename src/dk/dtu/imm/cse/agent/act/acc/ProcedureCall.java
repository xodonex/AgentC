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
 * The parse tree for a CALL expression
 *
 * @author  Henrik Lauritzen
 */
public class ProcedureCall implements Statement, Expression, ErrorLocation {

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

	public ProcedureCall(Token name, List params) {		
		_name = name;
		_params = params;
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
		if (!tree.isProcedure(_name.image, _params.size())) {			
			AccUtils.throwParseException(_name, "Unknown procedure \"" + 
					_name.image + "\"(" + _params.size() + ")");
		}
		for (int i = 0, max = _params.size(); i < max; i++) {
			((Expression)_params.get(i)).checkSymbols(tree, scope, SCOPE_USEONLY);
		}
	}


	public Token getInitialToken() {
		return _name;
	}
	

	public String toJavaExpression() {
		return makeCode(new StringBuffer()).toString();
	}
	

	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		makeCode(b);
		return b.append(";\n");
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		StringBuffer result = new StringBuffer("CALL ");
		result.append(_name).append("(");
		for (int i = 0, max = _params.size(); i < max; i++) {
			if (i > 0) {
				result.append(", ");
			}
			result.append(_params.get(i));
		}
		return result.append(")").toString();
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
	
	// the name of the procedure to be called
	private Token _name;

	// the parameters of the called procedure
	private List _params;
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	private StringBuffer makeCode(StringBuffer b) {
		b.append(_name.image).append('(').
				append(AccConstants.MESSAGE_MAP_NAME);
		for (int i = 0, max = _params.size(); i < max; i++) {
			b.append(", ");
			b.append(((Expression)_params.get(i)).toJavaExpression());				
		}
		return b.append(')');
	}
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}