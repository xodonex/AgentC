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
 * Commonly used methods for the AgentC compiler.
 *
 *
 * @author  Henrik Lauritzen
 */
public class AccUtils implements AccConstants {

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
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	private AccUtils() {
	}

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
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * Creates an appropriate parse error in a common format.
	 * @param tok the offending token
	 * @param message a description of the error
	 * @exception ParseException is the result of the invocation; thus the
	 *   method always completes abruptly.
	 */
	public static void throwParseException(Token tok, String message) 
			throws ParseException {
		throw new ParseException(message + 
				" at line " + tok.beginLine + 
				", column " + tok.beginColumn + ".");
	}

	
	/**
	 * Check that a statement is not unreachable
	 * @param prev the statement preceding the statement
	 * @param stm the statement whose reachability should be determined
	 */
	public static void checkReachability(Statement prev, Statement stm)
			throws ParseException {
		ReturnStatement error = null;
		if (prev instanceof ReturnStatement) {
			error = (ReturnStatement)prev;
		}
		else if (prev instanceof LockedStatement) {
			error = ((LockedStatement)prev).getLastReturn();
		}
		
		if (error != null) {
			throwParseException(error.getInitialToken(),
					"Unreachable statement following RETURN"); 
		}
	}

	
	/**
	 * Converts a comparsion code to the corresponding Java comparison operator.
	 * @param id one of the comparison codes used in the {@link Comparison}
	 * @return null iff the ID is not valid; it should be one of the 
	 * COMP_xxx constants in {@link AccConstants}
	 * @param invert whether the generated operator should be the inverse 
	 * of the specified operator.
	 */
	public static String idToJavaOperator(int id, boolean inverse) {
		if (inverse) {
			switch (id) {
			case COMP_EQ:
				return "!=";
			case COMP_NE:
				return "==";
			case COMP_LT:
				return ">=";
			case COMP_LE:
				return ">";
			case COMP_GE:
				return "<";
			case COMP_GT:
				return "<=";
			default:
				return null;
			}
		}
		else {
			switch (id) {
			case COMP_EQ:
				return "==";
			case COMP_NE:
				return "!=";
			case COMP_LT:
				return "<";
			case COMP_LE:
				return "<=";
			case COMP_GE:
				return ">=";
			case COMP_GT:
				return ">";
			default:
				return null;
			}			
		}
	}

	
	/**
	 * Generate code to produce a Java expression which represents a
	 * list of AgentC terms.
	 * @param buf the string buffer to which the code will be appended. If
	 *  the value is null, a new buffer will be created.
	 * @param terms the list of terms
	 * @param vardefReplacement the code to be used for terms which are
	 *  variable defs. If the value is null, the variable name will be
	 *  used even in this case.
	 * @return the modified string buffer containing the translated list of terms
	 * @exception IllegalArgumentException if the term list contains a
	 *  {@link Variable} which is not a {@link Variable#isUse() use}.
	 */
	public static StringBuffer translateTerms(StringBuffer buf, List terms,
			String vardefReplacement) {
		if (buf == null) {
			buf = new StringBuffer();
		}
		
		int l = terms.size();
		if (l == 0) {
			// avoid instantiating empty arrays in the code
			return buf.append("Util.NO_OBJECTS");
		}
		else {
			buf.append("new Object[] {");
			for (int i = 0; i < l; i++) {
				Expression e = (Expression)terms.get(i);
				if (vardefReplacement != null && 
						(e instanceof Variable) && 
						((Variable)e).isDef()) {
					buf.append(vardefReplacement);
				}
				else {
					buf.append(e.toJavaExpression());
				}
				
				if (i < l - 1) {
					buf.append(", ");
				}
			}
			buf.append("}");
		}
		
		return buf;
	}
	
	
	/**
	 * Append a number of indentations to a string buffer.
	 * @param b the string buffer to which indentations will be appended.
	 * @param level the number of indentations to be added.
	 */
	public static void indent(StringBuffer b, int level) {
		while (level > 0) {
			b.append('\t');
			level--;
		}
	}
	
	
	/**
	 * Encode a string as a Java string literal.
	 * @param id the string whose value should be encoded.
	 * @return the converted string.
	 */
	public static String encodeStringLiteral(String id) {
		char[] chars = id.toCharArray();
		StringBuffer buf = new StringBuffer(110 * chars.length / 100);
		buf.append('\"');
		
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c < 32) {
				switch (c) {
				case '\t':
					buf.append("\\t");
					break;
				case '\n':
					buf.append("\\n");
					break;
				case '\r':
					buf.append("\\r");
					break;
				default:
					buf.append('\\').append(Integer.toOctalString(chars[i]));
				}
			}
			else if (c > 128) {
				buf.append("\\u");
				if (c < 0x1000) {
					buf.append('0');
					if (c < 0x0100) {
						buf.append('0');
						if (c < 0x0010) {
							buf.append('0');
						}
					}
				}
				buf.append(Integer.toHexString(c));
			}
			else {
				switch (chars[i]) {
				case '\'':
				case '\\':
				case '\"':						
					buf.append('\\');
				}
				buf.append(c);
			}
		}
		
		return 	buf.append('\"').toString();
	}
	
	
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
		
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
