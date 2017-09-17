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
 * The parse tree for a constant reference
 *
 * @author  Henrik Lauritzen
 */
public class Constant implements Expression, ErrorLocation {

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

	public Constant(Token token, boolean external) {
		_token = token;
		_external = external;
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the name of the constant referred.
	 */
	public String getName() {
		return _token.image;
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
		// simply verify that this constant was defined previously
		if (!tree.getDefs().containsKey(getName())) {
			AccUtils.throwParseException(getInitialToken(), 
				"Undefined symbol \"" + getName() + "\"");
		}
	}
	

	public String toJavaExpression() {
		return _external ? getName() :
				java.text.MessageFormat.format(AccConstants.CONST_PATTERN,
					new String[] { getName() });
	}

	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return getName();
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

	// the token which contains the name of the constant
	private Token _token;
	
	// whether the token was externally defined
	private boolean _external;
	
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
