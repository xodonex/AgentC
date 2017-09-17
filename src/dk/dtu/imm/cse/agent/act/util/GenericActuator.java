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

import java.lang.reflect.*;
import dk.dtu.imm.cse.agent.act.afc.*;


/**
 * The GenericActuator uses Java reflection to identify the operations to
 *  use in {@link #xeq(String, Object[])}. The operations recognized
 *  are the declared methods of the subclass which have parameters of 
 *  type Object.
 *
 * @author  Henrik Lauritzen
 */
public abstract class GenericActuator implements Actuator {

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
	
	protected GenericActuator() {
	}
	
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
	
	public boolean xeq(String name, Object[] params) {	
		// retreive or generate the type specification array
		Class[] cls;
		switch (params.length) {
		case 0:
			cls = NULLARY;
			break;
		case 1:
			cls = UNARY;
			break;
		case 2:
			cls = BINARY;
			break;
		case 3:
			cls = TERNARY;
			break;
		case 4:
			cls = QUARTERNARY;
			break;
		default:
			cls = new Class[params.length];
			for (int i = 0; i < cls.length; i++) {
				cls[i] = OBJECT_CLASS;
			}
		}
		
		try {
			Method op = getClass().getDeclaredMethod(name, cls);
			if (op.getReturnType() != Boolean.TYPE && 
					op.getReturnType() != Boolean.class) {
				return false;
			}
			Object obj = op.invoke(this, params);
			return (obj instanceof Boolean) ? 
					((Boolean)obj).booleanValue() : false;
		}
		catch (Exception e) {
			return false;
		}
	}
	
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
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	
	// these values are stored to increase performance
	private final static Class OBJECT_CLASS = Object.class;
	
	private final static Class[] 
		NULLARY = {},
		UNARY = { OBJECT_CLASS },
		BINARY = { OBJECT_CLASS, OBJECT_CLASS },
		TERNARY = { OBJECT_CLASS, OBJECT_CLASS, OBJECT_CLASS },
		QUARTERNARY = { OBJECT_CLASS, OBJECT_CLASS, OBJECT_CLASS, OBJECT_CLASS };
			
	
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
