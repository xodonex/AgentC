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
 * The parse tree for a SAY ... statement.
 *
 * @author  Henrik Lauritzen
 */
public class SayStatement implements Statement, AccConstants {

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

	public SayStatement(LinkedHashMap pattern) {
		_pattern = pattern;
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
		if (_pattern == null) {
			return;
		}
		
		for (Iterator i = _pattern.values().iterator(); i.hasNext(); ) {
			((AbstractSyntax)i.next()).checkSymbols(
					tree, scope, SCOPE_USEONLY);
		}
	}


	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		b.append(TEMP_MAP_NAME).append(" = new HashMap(").
				append(2 * _pattern.size()).append(");\n");
		
		for (Iterator i = _pattern.entrySet().iterator(); i.hasNext(); ) {
			AccUtils.indent(b, indentLevel);
			Map.Entry e = (Map.Entry)i.next();
			
			b.append(TEMP_MAP_NAME).append(".put(").
					append(AccUtils.encodeStringLiteral("" + e.getKey())).
					append(", ").
					append(((Value)e.getValue()).toJavaExpression()).
					append(");\n");
		}
		
		AccUtils.indent(b, indentLevel);		
		return b.append("send(").append(TEMP_MAP_NAME).
				append(");\n");
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		StringBuffer buf = new StringBuffer("SAY ");
		if (_pattern == null) {
			buf.append("NOTHING");
		}
		else {
			buf.append("[");
			if (_pattern.size() > 0) {
				for (Iterator i = _pattern.entrySet().iterator(); ; ) {
					Map.Entry e = (Map.Entry)i.next();
					buf.append(e.getKey()).append("=").append(e.getValue());
					if (i.hasNext()) buf.append(", "); else break;					
				}
			}
			buf.append("]");
		}
		return buf.toString();		
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
		
	// binds a String instance (the identifier) to a parse tree for the 
	// expression representing the value
	private LinkedHashMap _pattern;
	
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
