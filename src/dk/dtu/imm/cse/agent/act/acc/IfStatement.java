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
 * The parse tree for an IF statement
 *
 * @author  Henrik Lauritzen
 */
public class IfStatement implements Statement, AccConstants {

	// =======================================================================
	// Class fields,
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

	public IfStatement(List conditions, List blocks) {
		_conditions = conditions;
		_blocks = blocks;
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
		// create a new scope for each of the branches
		Map newScope = new HashMap();
		
		for (int i = 0, max = _conditions.size(); i < max; i++) {
			List condition = (List)_conditions.get(i);
			List block = (List)_blocks.get(i);
			
			// start the check by resetting the scope to the enclosing scope
			newScope.clear();
			newScope.putAll(scope);
			
			// check the condition (may be null in the ELSE branch)
			if (condition != null) {
				for (Iterator it = condition.iterator(); it.hasNext(); ) {
					// check each condition; new defs should be allowed
					// in mental conditions, but not otherwise
					Condition c = (Condition)it.next();
					c.checkSymbols(tree, newScope, 
							(c instanceof Sentence) ? 
							SCOPE_DEFUSE : SCOPE_USEONLY);
				}
			}
			
			// check the block in the new (possibly modified) scope
			for (Iterator it = block.iterator(); it.hasNext(); ) {
				((Statement)it.next()).checkSymbols(
						tree, newScope, SCOPE_DEFUSE);
			}
		}
	}

	
	public StringBuffer generateCode(int indentLevel, StringBuffer b) {
		// the name of the variable used in the code to indicate whether
		// one of the branches has matched
		String matchVar = MATCH_NAME + indentLevel;
		
		// generate the initial code - a for-statement
		AccUtils.indent(b, indentLevel++);
		b.append("for (boolean ").append(matchVar).append(" = false; ; ) {\n"); 

		// generate code for each of the branches
		for (int i = 0, max = _blocks.size(); i < max; i++) {
			// determine whether the condition is simple, i.e., whether
			// a temporary knowledge base needs to be constructed and iterated
			// over
			List l = (List)_conditions.get(i);
			boolean simple;
			
			if (l == null) {
				// the ELSE - block has a simple condition
				simple = true;
			}
			else {
				// examine each of the conditions
				simple = true;
				for (Iterator it = l.iterator(); it.hasNext(); ) {
					Object obj = it.next();
					if ((obj instanceof Sentence) && !((Sentence)obj).isSimple()) {
						simple = false;
						break;
					}
				}
			}

			// generate code corresponding to the condition (no code is 
			// necessary in the ELSE branch (l == null)
			if (l != null) {
				AccUtils.indent(b, indentLevel++);
				b.append("do {\n");
			
				// generate code to evaluate the condition(s)
				// and the body
				generateConditionCode(i, l.iterator(), b, 
						indentLevel, matchVar);
			}
			else {
				// simply generate code for the body in the ELSE branch
				addBodyCode(i, b, indentLevel, matchVar);
			}			
			
			if (l != null) {
				// make code to terminate the while-loop 
				List block = (List)_blocks.get(i);
				int blocksize = block.size();
				
				AccUtils.indent(b, --indentLevel);
				b.append("} while (false);\n");
			}				
			
			// generate code to terminate the statement if the preceding 
			// branch matched
			if (i < max - 1) {
				AccUtils.indent(b, indentLevel);
				b.append("if (").append(matchVar).append(") break;\n");
			}
		}
		
		// make code to terminate the for - statement
		boolean addBreak = true;
		if (_blocks.size() > 0) {
			Object c = _conditions.get(_blocks.size() - 1);
			if (c == null) {
				List blk = (List)_blocks.get(_blocks.size() - 1);			
				if (blk.size() > 0 && 
						blk.get(blk.size() - 1) instanceof ReturnStatement) {
					// no break may be generated in the ELSE branch if a return
					// statement terminated the block
					addBreak = false;
				}
			}
		}
		if (addBreak) {
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
		StringBuffer result = new StringBuffer();
		int s = _conditions.size();
		
		for (int i = 0; i < s; i++) {
			List c = (List)_conditions.get(i);
			if (c == null) {
				result.append("ELSE {");
			}
			else {
				result.append(i == 0 ? "IF (" : "ELSIF (");
				Util.toString(result, c, ", ");
				result.append(") {");
			}
			for (Iterator it = ((List)_blocks.get(i)).iterator(); 
					it.hasNext(); ) {
				result.append("\n\t").append(it.next());
			}
			result.append("\n}").toString();
			if (i < s - 1) {
				result.append("\n");
			}
		}
		
		return result.toString();
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
		
	// a list of condition lists. The first is the condition for the IF-clause, 
	// while the remaining are conditions for the ELSIF-clauses, unless the 
	// condition is null (in which case the last clause is an ELSE clause)
	private List _conditions;
	
	// a list of statement blocks (statement lists)
	private List _blocks;
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
	
	// generate code for the body of a branch
	private void addBodyCode(int block, StringBuffer b, int indentLevel, 
			String matchVar) {
		// start the branch by indicating a match
		AccUtils.indent(b, indentLevel);
		b.append(matchVar).append(" = true;\n");
			
		// generate code for every statement in the body
		for (Iterator i = ((List)_blocks.get(block)).iterator(); i.hasNext(); ) {			
			((Statement)i.next()).generateCode(indentLevel, b);
		}
	}

	
	// generate condition code for a simple condition
	private void translateSimpleCond(StringBuffer b, int indentLevel,
			Condition c, String cond) {
		// the condition is a simple sentence or an expression
		if (c instanceof Sentence) {
			Sentence s = (Sentence)c;
			Variable alias = s.getAlias();
			
			if (alias != null) {
				// generate code to instantiate the new fact
				AccUtils.indent(b, indentLevel);
				b.append(TEMP_FACT_NAME).append(" = new Fact(");
				s.makeParameterTriple(b);
				b.append(");\n");

				// make code to check the condition
				AccUtils.indent(b, indentLevel);
				b.append("if (!").append(TEMP_KB_NAME).append(".contains(").
						append(TEMP_FACT_NAME).append(")) continue;\n");
				
				// make code to bind the alias variable
				LetStatement.generateCode(indentLevel, b, alias, TEMP_FACT_NAME);
				return;
			}
		}
		
		// make the test for the provided condition
		AccUtils.indent(b, indentLevel);
		b.append("if (").append(cond).append(") continue;\n");
	}
	
	
	// recursively generate the code for a complex set of conditions
	// block: the index into _blocks at which the body is found
	// it: the iterator producing the conditions
	// b: the buffer to be used
	// indentlevel: the current code indentation level
	// matchVar: the name of the branch control variable
	private void generateConditionCode(int block,
			Iterator it, StringBuffer b, int indentLevel, String matchVar) {
		if (!it.hasNext()) {
			// generate the body code and finish
			addBodyCode(block, b, indentLevel, matchVar);
			return;
		}
				
		// the name of the list of matches currently used in the generated code
		String currentMatch;

		Condition c = (Condition)it.next();
		String cond = c.toJavaCondition(true);
		
		if (cond != null) {
			// simple condition
			translateSimpleCond(b, indentLevel, c, cond);
			
			// generate the remaining code
			generateConditionCode(block, it, b, indentLevel, matchVar);
			return;
		}
		else {
			// c is a sentence containing variable declarations;
			Sentence s = (Sentence)c;
			
			// get the indices of new variables and make code to generate
			// a bitset of the indices
			List idxs = s.makeBitsetCode(b, indentLevel);

			// make code to perform the match
			AccUtils.indent(b, indentLevel);
			b.append("List ").
					append(currentMatch = MATCH_LIST_NAME + indentLevel).
					append(" = ").append(TEMP_KB_NAME).append(".match(");
			s.makeParameterTriple(b);
			b.append(", ").append(TEMP_BS_NAME).append(");\n");

			// make the beginning of the iteration loop
			String itName = LOOP_INDEX_NAME + indentLevel;
			String boundName = LOOP_BOUND_NAME + indentLevel;
			AccUtils.indent(b, indentLevel++);
			b.append("for (int ").append(itName).
					append(" = 0, ").append(boundName).append(" = ").
					append(currentMatch).append(".size(); ").append(itName).
					append(" < ").append(boundName).append("; ").
					append(itName).append("++) {\n");

			// make code to bind the new variables
			AccUtils.indent(b, indentLevel);
			b.append(TEMP_FACT_NAME).append(" = (Fact)").append(currentMatch).
					append(".get(").append(itName).append(");\n");							
			for (Iterator j = idxs.iterator(); j.hasNext(); ) {
				int idx = ((Integer)j.next()).intValue();
				Variable v = (Variable)s.getTerm(idx);
				if (!v.isWildcard()) {
					LetStatement.generateCode(indentLevel, b,
							(Variable)s.getTerm(idx), 
							TEMP_FACT_NAME + ".getTerm(" + idx + ")");
				}
			}
			Variable alias = s.getAlias();
			if (alias != null) {
				LetStatement.generateCode(indentLevel, b, alias, TEMP_FACT_NAME);
			}

			// generate the remaining code inside the loop using the
			// new knowledge base
			generateConditionCode(block, it, b, indentLevel, matchVar);
			
			// generate the end of the iteration loop
			AccUtils.indent(b, --indentLevel);
			b.append("}\n");
		}
	}

	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
