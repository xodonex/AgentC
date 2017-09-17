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
 * The parse tree for a LOCKED { ...} statement
 *
 * @author  Henrik Lauritzen
 */
public class LockedStatement implements Statement, AccConstants {

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
	 * Creates a new LOCKED { ... } statement.
	 * @param block the block of enclosed statements
	 */
	public LockedStatement(List block) {
		_block = block;
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Retreive the last return statement in the block, if the block
	 * ends with a return statent.
	 * @return null if the last statement in the block is not a return
	 *  statement (or if the block is empty)
	 */
	public ReturnStatement getLastReturn() {
		int size = _block.size();
		if (size == 0) {
			return null;
		}
		
		Object obj = _block.get(size - 1);
		if (obj instanceof ReturnStatement) {
			return (ReturnStatement)obj;
		}
		else if (obj instanceof LockedStatement) {
			return ((LockedStatement)obj).getLastReturn();
		}
		return null;
	}
	
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public void checkSymbols(ParseTree tree, Map scope, int scopeType)
			throws ParseException {
		// create a new scope
		Map newScope = new HashMap(scope);

		// check the block in the new scope
		for (int i = 0, max = _block.size(); i < max; i++) {
			((Statement)_block.get(i)).checkSymbols(
					tree, newScope, SCOPE_DEFUSE);
		}
	}	

	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		b.append("synchronized (").append(TEMP_KB_NAME).
				append(".getLock()) {\n");
		indentLevel++;
		
		for (int i = 0, max = _block.size(); i < max; i++) {
			((Statement)_block.get(i)).generateCode(indentLevel, b);
		}
		
		AccUtils.indent(b, --indentLevel);
		return b.append("}\n");
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		StringBuffer buf = new StringBuffer("LOCKED {");
		for (Iterator i = _block.iterator(); i.hasNext(); ) {
			buf.append("\n\t").append(i.next());
		}
		return buf.append("\n}").toString();
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
			
	// the block of enclosed statements 
	List _block;
	
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