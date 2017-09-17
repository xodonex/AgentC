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
 * The parse tree for a WHEN[...] rule
 *
 * @author  Henrik Lauritzen
 */
public class MessageRule implements Statement, AccConstants {

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
	 * Creates a new WHEN ... rule
	 * @param guard the rules' guard block. null is used to represent
	 *  NOTHING.
	 * @param block the block of statements to be executed
	 */
	public MessageRule(Map guard, List block) {
		_guard = guard;
		_block = block;
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
		// create a new scope
		Map newScope = new HashMap(scope);

		// check the guard, if any
		if (_guard != null) {
			for (Iterator i = _guard.values().iterator(); i.hasNext(); ) {
				// check each of the guard conditions, allowing new variables
				((Value)i.next()).checkSymbols(
						tree, newScope, SCOPE_DEFUSE);
			}
		}
		
		// check the block in the new (possibly modified) scope
		for (int i = 0, max = _block.size(); i < max; i++) {
			((Statement)_block.get(i)).checkSymbols(
					tree, newScope, SCOPE_DEFUSE);
		}
	}	

	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		AccUtils.indent(b, indentLevel);
		if (_guard == null) {
			b.append("if (").append(MESSAGE_MAP_NAME).append(' ').
				append("== null) {\n");
		}
		else {
			b.append("while (").append(MESSAGE_MAP_NAME).append(' ').
					append("!= null) {\n");
		}
		indentLevel++;
		
		if (_guard != null) {
			// generate code to match every guard condition
			for (Iterator i = _guard.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				String key = AccUtils.encodeStringLiteral((String)e.getKey());
				Value v = (Value)e.getValue();
								
				// 1: generate code to check that the message has a matching key
				AccUtils.indent(b, indentLevel);
				b.append("if (!").append(MESSAGE_MAP_NAME).append(".containsKey(").
						append(key).append(")) break;\n");
				
				if ((v instanceof Variable) && ((Variable)v).isWildcard()) {
					// no additional code is necessary for the wildcard
					continue;
				}
				
				// 2: retreive the key
				AccUtils.indent(b, indentLevel);
				b.append(TEMP_OBJ_NAME).append(" = ").append(MESSAGE_MAP_NAME).
						append(".get(").append(key).append(");\n");
				
				boolean simpleGuard = true;
				
				if (v instanceof Sentence) {
					simpleGuard = false;
					
					// 3: examine that the key has the correct type
					AccUtils.indent(b, indentLevel);
					b.append("if (!(").append(TEMP_OBJ_NAME).
							append(" instanceof Fact)) break;\n");

					// 3a: cast the key
					AccUtils.indent(b, indentLevel);
					b.append(TEMP_FACT_NAME).append(" = (Fact)").
							append(TEMP_OBJ_NAME).append(";\n");
							
					Sentence s = (Sentence)v;

					if (!s.isSimple()) {
						// construct code to configure the bitset and
						// save the variable positions						
						List l = s.makeBitsetCode(b, indentLevel);
						
						// construct code to match the pattern
						AccUtils.indent(b, indentLevel);
						b.append("if (!").append(TEMP_FACT_NAME).
								append(".matches(");
						s.makeParameterTriple(b);
						b.append(", ").append(TEMP_BS_NAME).append(")) break;\n");
												
						// construct code for the vardefs
						for (Iterator it = l.iterator(); it.hasNext(); ) {
							int idx = ((Integer)it.next()).intValue();
							LetStatement.generateCode(indentLevel, b,
									(Variable)s.getTerms().get(idx),
									TEMP_FACT_NAME + ".getTerm(" + idx + ")");
						}
						
						// make code corresponding to the alias
						Variable a = s.getAlias();
						if (a != null) {
							LetStatement.generateCode(indentLevel, b, a, 
									TEMP_FACT_NAME);
						}
					}
					else {
						AccUtils.indent(b, indentLevel);
						b.append("if (!").append(TEMP_FACT_NAME).
								append(".equals(");
						s.makeParameterTriple(b);
						b.append(")) break;\n");

						// make code to corresponding to the alias
						Variable a = s.getAlias();
						if (a != null) {
							LetStatement.generateCode(indentLevel, b, a, 
									TEMP_FACT_NAME);
						}
					}
				}
				else if (v instanceof Variable) {
					Variable var = (Variable)v;
					if (var.isDef()) {
						// generate code for the variable def
						LetStatement.generateCode(indentLevel, b, 
								var, TEMP_OBJ_NAME);
						simpleGuard = false;
					}
					// else code is generated later
				}
				
				if (simpleGuard) {
					// not a sentence variable def (otherwise code has already 
					// been generated). 
					// The guard will be a simple expression
					AccUtils.indent(b, indentLevel);
					b.append("if (!isEqual(").append(TEMP_OBJ_NAME).append(", ").
							append(v.toJavaExpression()).append(")) break;\n");
				}
			} // for
			
			// add an empty space before the body
			if (_guard.size() > 0 && _block.size() > 0) {
				b.append('\n');
			}
		} // _guard != null
		
		// generate code for the body
		for (int i = 0, max = _block.size(); i < max; i++) {
			((Statement)_block.get(i)).generateCode(indentLevel, b);
		}
		
		// terminate the loop (a loop is used in order to utilize the break
		// in the generated code)
		if (_guard != null && !(_block.size() > 0 && 
				_block.get(_block.size() - 1) instanceof ReturnStatement)) {
			AccUtils.indent(b, indentLevel);
			b.append("break;\n");
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
		StringBuffer buf = new StringBuffer("WHEN ");
		if (_guard == null) {
			buf.append("NOTHING");
		}
		else {
			buf.append("[");
			if (_guard.size() > 0) {
				for (Iterator i = _guard.entrySet().iterator(); ;) {
					Map.Entry e = (Map.Entry)i.next();
					buf.append(e.getKey()).append("=").append(e.getValue());
					if (i.hasNext()) buf.append(", "); else break;					
				}		
			}
			buf.append("]");
		}
		buf.append(" {");
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
			
	// the message guard (if any)
	Map _guard;
	
	// the block of statements to be executed
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
