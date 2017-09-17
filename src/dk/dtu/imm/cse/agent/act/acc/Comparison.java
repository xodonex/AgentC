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
 * The parse tree for a comparison between expressions
 *
 * @author  Henrik Lauritzen
 */
public class Comparison implements Condition, AccConstants {

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

	public Comparison(Expression e1, int id, Expression e2) {
		_id = id;
		_e1 = e1;
		_e2 = e2;
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
		// the branches of the comparison may only contain simple conditions,
		// hence SCOPE_USEONLY.
		_e1.checkSymbols(tree, scope, SCOPE_USEONLY);
		_e2.checkSymbols(tree, scope, SCOPE_USEONLY);
	}
	
	
	public String toJavaCondition(boolean negated) {
		// the condition requires that the values used are Comparables,
		// or the generated code will be invalid; however, there is no
		// way to check this at compile time, since the left-hand
		// expression may be obtained from a QUERY.
		StringBuffer buf = new StringBuffer();
		if (_id == COMP_EQ || _id == COMP_NE) {
			if (_id == COMP_NE ^ negated) {
				buf.append('!');
			}
			buf.append("isEqual(").append(_e1.toJavaExpression()).
					append(", ").append(_e2.toJavaExpression()).append(')');
		}
		else {
			buf.append("(((Comparable)").
				append(_e1.toJavaExpression()).append(").compareTo(").
				append(_e2.toJavaExpression()).append(") ").
				append(AccUtils.idToJavaOperator(_id, negated)).
				append(" 0)");
		}
		return buf.toString();
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return new StringBuffer(_e1.toString()).append(' ').
				append(AccUtils.idToJavaOperator(_id, false)).append(' ').
				append(_e2).toString();
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
	
	// the comparison ID
	private int _id;
	
	// the left-hand expression
	private Expression _e1;
	
	// the right-hand expression
	private Expression _e2;
		
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