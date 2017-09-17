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
 * The parse tree for an AgentC variable
 *
 * @author  Henrik Lauritzen
 */
public class Variable implements Expression, ErrorLocation {

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

	public Variable(Token token) {
		this(token.image.equals("_") ? "_" : token.image.substring(1));
		_token = token;
	}
		
	// ------------------------------ protected ------------------------------

	Variable(String name) {
		if ("_".equals(name)) {
			_def = this;
			_id = WILDCARD_ID;
		}
		else {
			_name = name;			
		}		
	}
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the name of the variable
	 */
	public String getName() {
		return _name;
	}
	
	
	/**
	 * @return true iff the variable is a wildcard
	 */
	public boolean isWildcard() {
		return _id == WILDCARD_ID;
	}	
	
	
	/**
	 * Classify the variable as a definition in the syntax.
	 * @param scope the scope table to which the variable will be added.
	 * @exception ParseException if the variable has already been classified.
	 */
	public void classifyAsDef(Map scope) throws ParseException {
		classifyAsDef(scope, false);
	}
	

	/**
	 * Classify the variable as a definition in the syntax.
	 * @param scope the scope table to which the variable will be added.
	 * @param isAssignable whether the variable is assignable
	 * @exception ParseException if the variable has already been classified.
	 */
	public void classifyAsDef(Map scope, boolean assignable) throws ParseException {
		if (_def != null) {			
			AccUtils.throwParseException(getInitialToken(),
					"Attempt to reclassify variable \"" + getName() + "\"");
		}
		
		// configure as a def
		_def = this;
		_assignable = assignable;
		
		// assign a new, unique variable number
		_id = ((Integer)_numGenerator.get()).intValue();
		_numGenerator.set(new Integer(_id + 1));

		// update the scope
		scope.put(getName(), this);		
	}

	
	/**
	 * Classify the variable as a usage in the syntax.
	 * @param def the variable instance representing the definition of
	 *  this variable.
	 * @exception ParseException if the variable has already been classified.
	 */
	public void classifyAsUse(Variable def) throws ParseException {
		if (_def != null) {			
			AccUtils.throwParseException(getInitialToken(),
					"Attempt to reclassify variable \"" + getName() + "\"");
		}
		_def = def;
	}
	
	
	/**
	 * @return true iff the variable has been classified as a def.
	 */
	public boolean isDef() {
		return _def == this;
	}
	
	
	/**
	 * @return true iff the variable has been classified as a use.
	 */
	public boolean isUse() {
		return _def != null && _def != this;
	}
	
	
	/**
	 * @return true iff the variable is assignable
	 */
	public boolean isAssignable() {
		return _def == null ? false : _def._assignable;
	}
	
	
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	public Token getInitialToken() {
		return _token;
	}
	
	
	public void checkSymbols(ParseTree tree, Map scope, int scopeType)
			throws ParseException {
		String name = getName();
		
		if (scopeType == SCOPE_NOVARS) {
			AccUtils.throwParseException(getInitialToken(),
					"Variable \"" + name + 
					"\" illegally used inside initial fact");
		}
		if (isWildcard()) {
			if (scopeType == SCOPE_USEONLY) {
				AccUtils.throwParseException(getInitialToken(),
					"Illegal use of wildcard variable");
			}
			else {
				return;
			}
		}			
		
		Variable def = (Variable)scope.get(name);
		if (def == null) {
			// the variable has not yet been defined; check that a def
			// is legal and handle accordingly
			if (scopeType == SCOPE_DEFUSE) {
				// legal def.
				classifyAsDef(scope);
			}
			else {
				// unknown variable
				AccUtils.throwParseException(getInitialToken(),
						"Unknown variable \"" + name + "\"");
			}
		}
		else {
			classifyAsUse(def);
		}			
	}

	
	public String toJavaExpression() {
		if (_def == null) {
			throw new IllegalStateException(
					"Attempt to generate code for an unclassified variable");
		}
		return java.text.MessageFormat.format(AccConstants.VAR_PATTERN, 
				new String[] { _def._name, "" + _def._id });
	}	
		
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return isWildcard() ? "_" : "?" + _name;
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
	
	// generator for variaable numbers
	private static ThreadLocal _numGenerator = new ThreadLocal() {
         protected synchronized Object initialValue() {
			 return new Integer(0);
         }
     };

	 // the ID used for a wildcard variable
	 private final static int WILDCARD_ID = -1;
	
	// ------------------------------ instance -----------------------------
	
	// the token resulting in the variable name
	private Token _token;
	
	// the variable name
	private String _name;

	// the variable instance corresponding to the initial definition
	// of this variable. _def == null means that this variable has not
	// yet been classified. _def == this means that it is a def,
	// while _def != null && _def != this indicates that it is a use.
	private Variable _def = null;

	// whether the variable is assignable
	private boolean _assignable = false;
	
	// a unique ID used as the variable name
	private int _id = 0;
	
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
