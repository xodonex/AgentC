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
 * The parse tree for an AgentC literal
 *
 * @author  Henrik Lauritzen
 */
public class Literal implements Expression, AccConstants {

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

	/**
	 * Creates a new literal of the specified type and value
	 * @param type a type identifier specifying the
	 *  type of the literal value
	 * @param source the Java source code to produce the value.
	 */
	public Literal(int type, String source) {
		switch (_type = type) {
		case TYPE_INT:
			_value = Integer.valueOf(source);
			break;
		case TYPE_DOUBLE:
			_value = Double.valueOf(source);
			break;
		case TYPE_STRING:
			_value = source; // true value is decoded literal.
			break;
		default:
			throw new IllegalArgumentException("" + type);
		}
		_source = source;
	}

	
	/**
	 * Creates a new "literal" to represent an arbitrary Java value.
	 * @param value the value to be represented.
	 */
	public Literal(Object value) {
		_value = value;
		_type = TYPE_EXTERNAL;
		_source = value == null ? "null" : value.toString();
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the type of the literal value
	 */
	public int getType() {
		return _type;
	}
	
	
	/**
	 * @return the source code of the literal.
	 */
	public String getSource() {
		return _source;
	}

	
	/**
	 * @return the Java name for the literal's type.
	 */
	public String getJavaType() {
		if (_value == null) {
			return "Object";
		}
		
		String result = _value.getClass().getName();
		return result.startsWith("java.lang.") ? result.substring(10) : result;
	}

	
	/**
	 * @return a canonical representation for the value of the literal.
	 */
	public Object getValue() {
		return _value;
	}
	
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public void checkSymbols(ParseTree tree, Map scope, int scopeType)
			throws ParseException {
		// no check necessary!
	}


	public String toJavaExpression() {
		switch (_type) {
		case TYPE_INT:
			return "new Integer(" + _value + ")";
		case TYPE_DOUBLE:
			return "new Double(" + _value + ")";
		case TYPE_STRING:
			return _source;
		default:
			throw new InternalError();
		}
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return _source;
	}
	
	
	public int hashCode() {
		return _value.hashCode();
	}
	
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof Literal)) {
			return false;
		}
		
		return _value.equals(((Literal)obj)._value);
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

	// the type ID for the value
	private int _type;
	
	// the source code for the value
	private String _source;
	
	// the (possibly) decoded value of the source code, used in
	// equals() comparisons.
	private Object _value;
	
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
