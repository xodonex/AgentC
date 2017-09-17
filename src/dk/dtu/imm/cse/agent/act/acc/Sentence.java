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

import dk.dtu.imm.cse.agent.act.afc.*;


/**
 * The parse tree for an AgentC sentence (a DATALOG fact plus an attitude).
 *
 * @author  Henrik Lauritzen
 */
public class Sentence implements Value, Condition, AccConstants {

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
	 * Constructs a new sentence
	 * @param attitude the token decription of the attitude
	 * @param name the predicate symbol
	 * @param terms a list of parse trees for the terms
	 */
	public Sentence(Token attitude, String name, List terms) {
		if ((_attitude = attitude) == null) {
			throw new NullPointerException();
		}
		if ((_name = name) == null || _name.length() == 0) {
			throw new IllegalArgumentException();
		}
		_terms = terms;		
	}

	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************

	// =======================================================================
	// New instance methods
	// =======================================================================

	// ------------------------------- public --------------------------------

	/**
	 * @return the numerical attitude id. This will be established during
	 *  the semantical checks.
	 */
	public int getAttitude() {
		return _attitudeId;
	}

	
	/**
	 * @return the name (predicate symbol) of the fact.
	 */
	public String getName() {
		return _name;
	}
	
	
	/**
	 * @return the list of terms. <strong>N.B.</strong>: this list should
	 *  not be modified.
	 */
	public List getTerms() {
		return _terms;
	}

	
	/**
	 * @return the term at the specified index
	 * @param i the index of the term to be retreived
	 * @exception IndexOutOfBoundsException if the specified index is invalid
	 */
	public Expression getTerm(int i) {
		return (Expression)_terms.get(i);
	}

	
	/**
	 * Determine whether this sentence is simple, i.e., whether its list of
	 * terms is free from variable defs.
	 * @return true iff the terms do not include variable defs.
	 */
	public boolean isSimple() {
		for (int i = 0, max = _terms.size(); i < max; i++) {
			Object v = _terms.get(i);
			if (v instanceof Variable) {
				if (((Variable)v).isDef()) return false;				
			}
		}
		return true;
	}
		
	
	/**
	 * Retreive the indices of all contained terms which are new variable
	 *  definitions.
	 * @return a list of Integer instances whose values indicate the position
	 *  of variable defs in the term list. The list will be empty iff
	 *  the sentence is {@link #isSimple() simple}.
	 */
	public List getVardefIndices() {
		int s = _terms.size();
		ArrayList l = new ArrayList(s);
		
		for (int i = 0; i < s; i++) {
			Value v = (Value)_terms.get(i);
			if (v instanceof Variable) {
				Variable var = (Variable)v;
				if (var.isDef()) {
					l.add(new Integer(i));
				}
			}			
		}
		
		return l;
	}
	
	
	/**
	 * Make code to configure the bitset to match the terms of this sentence.
	 * @param b the buffer to which the generated code should be appended
	 * @param indentLevel the code indent level
	 * @return the value produced by {@link #getVardefIndices()}
	 */
	public List makeBitsetCode(StringBuffer b, int indentLevel) {		
		List l = getVardefIndices();
		if (l.size() == 0) {
			return l;
		}

		// construct a BitSet indicating the variables
		// to be matched
		AccUtils.indent(b, indentLevel);
		b.append(TEMP_BS_NAME).append(".clear();\n");							
		for (Iterator it = l.iterator(); it.hasNext(); ) {
			AccUtils.indent(b, indentLevel);
			b.append(TEMP_BS_NAME).append(".set(").
					append(it.next()).append(");\n");
		}		
		
		return l;
	}
	
	
	/**
	 * Encode the category, name and terms as 3 Java expressions
	 *  separated by commas. Note that if the sentence is not 
	 * {@link #isSimple() simple}, the value null will be generated
	 *  for the variable defs.
	 * @param b the string buffer to which results will be appended
	 * @return b
	 */
	public StringBuffer makeParameterTriple(StringBuffer b) {
		b.append(_attitudeId).append(", ").
				append(AccUtils.encodeStringLiteral(_name)).
				append(", ");
		AccUtils.translateTerms(b, _terms, "null");
		return b;
	}
	

	/**
	 * Set the alias variable to be used for this sentence 
	 * (only valid when the sentence is part of an IF or WHEN statement)
	 */
	public void setAlias(Variable v) {
		_alias = v == null || v.isWildcard() ? null : v;
	}
	
	
	/**
	 * @return the alias variable used for this sentence 
	 * (only valid when the sentence is part of an IF or WHEN statement)
	 */
	public Variable getAlias() {
		return _alias;
	}
	
	
	/**
	 * Encode a Java expression which produces the represented Fact.
	 * Note that if the sentence is not 
	 * {@link #isSimple simple}, the value null will be generated in
	 *  the place of variable defs.
	 */
	public String toJavaExpression() {
		StringBuffer b = new StringBuffer("new Fact(").append(_attitudeId).
				append(", ").append(AccUtils.encodeStringLiteral(_name)).
				append(", ");
		AccUtils.translateTerms(b, _terms, "null");
		return b.append(')').toString();
	}
	
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================

	// ------------------------------- public --------------------------------

	public void checkSymbols(ParseTree tree, Map scope, int scopeType)
			throws ParseException {

		Integer id = (Integer)tree.getAttitudes().get(_attitude.image);
		if (id == null) {
			AccUtils.throwParseException(_attitude, 
					"Unknown attitude " + _attitude.image);
		}
		_attitudeId = id.intValue();

		for (int i = 0, max = _terms.size(); i < max; i++) {
			((AbstractSyntax)_terms.get(i)).checkSymbols(
					tree, scope, scopeType);
		}
		
		if (_alias != null) {
			_alias.classifyAsDef(scope);
		}
	}
	

	public String toJavaCondition(boolean negated) {
		if (!isSimple()) {
			// can't encode as a Java expression
			return null;
		}		
		
		StringBuffer b = new StringBuffer();
		if (negated) {
			b.append('!');
		}
		b.append(TEMP_KB_NAME).append(".contains(");
		makeParameterTriple(b);
		return b.append(')').toString();
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================

	// ------------------------------- public --------------------------------

	public String toString() {
		return _attitude.image + " " + _name + _terms +
				(_alias != null ? " AS " + _alias : "");
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

	// the symbolical ID for the fact
	private Token _attitude;

	// the numerical ID for the fact (configured during semantical check)
	private int _attitudeId;
	
	// the predicate symbol of the attitude
	private String _name;

	// the terms of the attitude
	private List _terms;
	
	// the alias for the clase (only used in IF/WHEN)
	private Variable _alias;

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
