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
package dk.dtu.imm.cse.agent.act.util;

import java.util.*;

/**
 * A collection of various smaller commonly used values and methods.
 *
 * @author  Henrik Lauritzen
 */
public class Util {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------

	/**
	 * This array has zero length; it should be used whenever possible in order
	 * to reduce the number of different zero-length array instances.
	 */
	public final static Object[] NO_OBJECTS = new Object[0];

	
	/**
	 * This array has zero length; it should be used whenever possible in order
	 * to reduce the number of different zero-length array instances.
	 */
	public final static int[] NO_INTS = new int[0];

	
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

	// no instances are allowed
	private Util() {
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
	 * Encode a series of values as a string
	 * @param values the values to be encoded
	 * @param sep the separator to be used between individual values
	 * @return the encoded string
	 */
	public static String toString(Object[] values, String sep) {
		return toString(new StringBuffer(), Arrays.asList(values), sep).toString();
	}
	
	
	/**
	 * Encode a series of values as a string
	 * @param values the values to be encoded
	 * @param sep the separator to be used between individual values
	 * @return the encoded string
	 */
	public static String toString(Collection values, String sep) {
		return toString(new StringBuffer(), values, sep).toString();
	}
	

	/**
	 * Encode a series of values as a string.
	 * @param buf the buffer to which the encoded string will be appended
	 * @param values the values to be encoded
	 * @param sep the separator to be used between individual values
	 * @return the modified buffer (buf)
	 */
	public static StringBuffer toString(StringBuffer buf, 
			Object[] values, String sep) {
		return toString(buf, Arrays.asList(values), sep);
	}
	
	
	/**
	 * Encode a series of values as a string.
	 * @param buf the buffer to which the encoded string will be appended
	 * @param values the values to be encoded
	 * @param sep the separator to be used between individual values
	 * @return the modified buffer (buf)
	 */
	public static StringBuffer toString(StringBuffer buf, 
			Collection values, String sep) {				
		int s = values.size();
		if (s == 0) {
			return buf;
		}
		
		Iterator it = values.iterator();
		for (int i = 0; ;) {
			buf.append(it.next());
			if (++i >= s) break;
			buf.append(sep);
		}
		return buf;
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
