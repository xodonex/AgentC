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
 * The parse tree for the ADOPT/DROP statement
 *
 * @author  Henrik Lauritzen
 */
public class MentalUpdateStatement implements Statement, AccConstants {

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

	public MentalUpdateStatement(boolean isAdopt, Value v) {
		_isAdopt = isAdopt;
		_v = v;
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
		// allow variables in DROP but not in ADOPT
		_v.checkSymbols(tree, scope, _isAdopt || _v instanceof Variable ? 
				SCOPE_USEONLY : SCOPE_DEFUSE);
	}
	

	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		if (_v instanceof Sentence) {
			Sentence s = (Sentence)_v;
			
			// make code to initialize the bitset, if necessary
			List vdefs = s.makeBitsetCode(b, indentLevel);			
			
			// make code to add or remove as necessary
			AccUtils.indent(b, indentLevel);
			b.append(TEMP_KB_NAME).append(_isAdopt ? ".add(" : ".remove(");			
			s.makeParameterTriple(b);
			
			if (vdefs.size() > 0) {
				// INVARIANT: !c.isSimple() && !_isAdopt
				// add the bitset to the list of parameters
				b.append(", ").append(TEMP_BS_NAME);
			}
		}
		else {
			// simple case (adopt/drop a variable)
			AccUtils.indent(b, indentLevel);
			b.append(TEMP_KB_NAME).append(_isAdopt ? ".add(" : ".remove(");
			b.append("(Fact)").append(((Expression)_v).toJavaExpression());
		}

		return b.append(");\n");
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return (_isAdopt ? "ADOPT " : "DROP ") + _v;
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
	
	// whether this parse tree represents the ADOPT statement
	private boolean _isAdopt;
	
	// the sentence/variable to be adopted/dropped
	private Value _v;
	
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
