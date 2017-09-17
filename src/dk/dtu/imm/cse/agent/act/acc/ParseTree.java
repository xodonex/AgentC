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

import java.lang.reflect.*;
import java.util.*;

import dk.dtu.imm.cse.agent.act.afc.*;


/**
 * The ParseTree holds the parsed components of an AgentC specification.
 *
 * @author  Henrik Lauritzen
 */
public class ParseTree implements AccConstants {

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
	 * Constructs a new parse tree
	 */
	public ParseTree() {
		_attitudes = new LinkedHashMap();
		_defs = new LinkedHashMap();
		_facts = new ArrayList();
		_procedures = new LinkedHashMap();
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * Includes the declarations given in an interface to the parse tree.
	 * @see #addInterfaceDecl(Class)
	 */
	public void addInterfaceDecl(String intf) throws ParseException {
		try {
			addInterfaceDecl(Class.forName(intf));
		}
		catch (ClassNotFoundException e) {
			throw new ParseException("The specified interface \"" + intf +
					"\" could not be found");
		}
	}
	

	/**
	 * Includes the declarations given in an interface to the parse tree:
	 * public constants will be directly usable, and public methods having 
	 * a matching signature will be added as requirement declarations.
	 */
	public void addInterfaceDecl(Class cls) throws ParseException {
		try {
			// verify that the class represents a public interface
			int mask = Modifier.INTERFACE | Modifier.PUBLIC;
			if ((cls.getModifiers() & mask) != mask) {
				throw new ParseException(cls.getName() + 
						" is not a public interface");
			}
			
			// add the appropriate fields
			addDefsFromFields(cls, Modifier.PUBLIC, 0);

			// add the appropriate methods
			addProceduresFromMethods(cls);
		}	
		catch (IllegalAccessException e) {
			throw new ParseException(e.getMessage());
		}
	}
	
	
	/**
	 * Determine whether the given identifier has been used in a declaration
	 * obtained from an interface or superclass.
	 */
	public boolean isExternalDef(String name) {
		Literal l = (Literal)_defs.get(name);
		return l == null ? false : l.getType() == TYPE_EXTERNAL;
	}
	

	/**
	 * Includes the declarations given in the specified superclass for the 
	 * resulting ACME.
	 * @see #setSuperclass(Class)
	 */
	public void setSuperclass(String sclass) throws ParseException {
		try {
			setSuperclass(Class.forName(sclass));
		}
		catch (ClassNotFoundException e) {
			throw new ParseException("The specified superclass \"" + sclass +
					"\" could not be found");
		}
	}
	

	/**
	 * Includes the declarations given in the specified superclass for the 
	 * resulting ACME. Final, static constants (protected or public) will
	 * be included as constants, and methods (protected or public) will
	 * be included as external procedures, or as requirement declarations in
	 * case of an abstract method.
	 */
	public void setSuperclass(Class cls) throws ParseException {
		try {			
			// check that the class has the correct type
			if ((cls.getModifiers() & 
					(Modifier.FINAL | Modifier.INTERFACE | Modifier.PUBLIC)) !=
					(Modifier.PUBLIC)) {				
				throw new ParseException("The specified superclass \"" +
						cls.getName() + "\" is not a public, nonfinal class");
			}
			
			// check that the class inherits from Acme.
			Class sclass = cls; 
			while (true) {
				if (sclass == Acme.class) {
					// OK
					break;
				}
				sclass = sclass.getSuperclass();
				if (sclass == null) {
					throw new ParseException("The specified superclass \"" +
							cls.getName() + "\" does not inherit from \"" +
							Acme.class.getName());
				}
			}
			
			// add the appropriate fields as constants
			addDefsFromFields(cls, 0, Modifier.PROTECTED | Modifier.PUBLIC);
			
			// add the appropriate method declarations
			addProceduresFromMethods(cls);
		}	
		catch (IllegalAccessException e) {
			throw new ParseException(e.getMessage());
		}
	}

	
	/**
	 * Determine whether the given identifier is the name of a procedure
	 * whose body has been inherited.
	 */
	public boolean isExternalProcedure(ProcedureSignature sig) {
		Object obj = _procedures.get(sig);
		return obj == EXTERNAL_PROCEDURE || obj == EXTERNAL_FINAL_PROCEDURE;
	}
	
	
	
	/**
	 * Add an attitude declaration to the program
	 */
	public void addAttitude(Token attitude, int value) throws ParseException {
		if (value < 0) {
	  		AccUtils.throwParseException(attitude, "Negative attitude ID");
		}
		
		Integer v = (Integer)_attitudes.get(attitude.image);
		if (v != null) {
			if (v.intValue() != value) {
				AccUtils.throwParseException(attitude,
					"Attitude " + attitude.image + " redefined from " +
						v + " to " + value);
			}
		}
		else {
			_attitudes.put(attitude.image, new Integer(value));
		}
	}

	
	/**
	 * Add a symbol definition to the program.
	 */
	public void addDef(Token name, Literal value) throws ParseException {
		Literal old = (Literal)_defs.get(name.image);
		if (old != null) {
			if (!old.equals(value)) {
				AccUtils.throwParseException(name,
					"Constant " + name.image + " redefined from " +
					old.getValue() + " (" + old.getJavaType() + ") to " +
					value.getValue() + " (" + value.getJavaType() + ")");
			}
		}
		else {
			_defs.put(name.image, value);
		}
	}
	
	
	/**
	 * Add an initial fact to the program
	 */
	public void addFact(Sentence s) {
		_facts.add(s);
	}
	
	
	/**
	 * Add a procedure to the program
	 */
	public void addProcedure(Token name, List params, List block) 
			throws ParseException {		
		ProcedureSignature sig = new ProcedureSignature(name.image, params);
		
		if (_procedures.containsKey(sig)) {
			if (block == null) {
				// requirement declaration - do nothing
			}
			else {
				Object p = _procedures.get(sig);
				if (p != null && p != EXTERNAL_PROCEDURE) {
					// dissalow redeclaration of procedures, unless
					// they are inherited and not final.
					AccUtils.throwParseException(name,
						"Procedure " + name.image + " illegally redefined");
				}
				else if (block != null) {
					_procedures.put(sig, block);
				}
				// else null is already stored 
			}
		}
		else {
			// add the declaration
			_procedures.put(sig, block);
		}
	}
	
	
	/**
	 * Determine whether a given procedure signature is present in the parse tree.
	 */
	public boolean isProcedure(String name, int params) {
		return _procedures.containsKey(
				new ProcedureSignature(name, makeParams(params)));
	}
	
	
	/**
	 * @return the attitude declaration map
	 */
	public Map getAttitudes() {
		return _attitudes;
	}

	
	/**
	 * @return the symbol declaration map
	 */
	public Map getDefs() {
		return _defs;
	}
	
	
	/**
	 * @return the initial facts
	 */
	public List getFacts() {
		return _facts;
	}
	
	
	/**
	 * @return the procedure map
	 */
	public Map getProcedures() {
		return _procedures;
	}
	
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
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	
	// a unique List instance used to represent a procedure whose
	// implementation has been inherited
	private final static List EXTERNAL_PROCEDURE = new ArrayList(0);
	
	// a unique List instance used to represent a procedure whose
	// implementation is final and has been inherited.
	private final static List EXTERNAL_FINAL_PROCEDURE = new ArrayList(0);

	// ------------------------------ instance -----------------------------

	// contains the attitude declarations
	private Map _attitudes;
	
	// contains the symbol declarations
	private Map _defs;
	
	// contains the knowledge base's initial facts
	private List _facts;
	
	// contains the procedures of the code
	private Map _procedures;

		
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	// add the constant fields from the given class
	private void addDefsFromFields(Class c, int reqMask, int optMask) 
			throws IllegalAccessException {
		Set testSet = new LinkedHashSet();
		testSet.addAll(Arrays.asList(c.getFields()));
		testSet.addAll(Arrays.asList(c.getDeclaredFields()));
		for (Iterator i = testSet.iterator(); i.hasNext(); ) {
			addDefFromField((Field)i.next(), reqMask, optMask);
		}
	}
	
	// add a definition directly from a field
	private void addDefFromField(Field f, int requiredMask, int optionalMask) 
			throws IllegalAccessException {
		if ((f.getModifiers() & requiredMask) != requiredMask ||
				(optionalMask == 0 ? false : 
					(f.getModifiers() & optionalMask) == 0) ||
				_defs.containsKey(f.getName())) {
			// the field does not match the requirements.
			return;
		}
		
		// the literal to represent the field value
		Literal value;
		
		Class t = f.getType();
		if (t.isPrimitive()) {
			if (t == Double.TYPE) {
				value = new Literal(TYPE_DOUBLE, "" + f.get(null));
			}
			else if (t == Integer.TYPE) {
				value = new Literal(TYPE_INT, "" + f.get(null));
			}
			else {
				// can't use the value
				return;
			}
		}
		else {
			// use the value directly
			value = new Literal(f.get(null));
		}
			
		// add the field value as a declaration
		_defs.put(f.getName(), value);		
	}


	// add the procedures specified or implemented in the given class
	private void addProceduresFromMethods(Class c) throws ParseException {
		Set testSet = new LinkedHashSet();
		testSet.addAll(Arrays.asList(c.getMethods()));
		testSet.addAll(Arrays.asList(c.getDeclaredMethods()));
		for (Iterator i = testSet.iterator(); i.hasNext(); ) {
			addProcedureFromMethod((Method)i.next());
		}
	}
	
	// make a parameter list of a given length
	private List makeParams(int size) {
		List result = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			result.add(new Variable("" + (char)('a' + size)));
		}
		return result;
	}
	
	// add a procedure from a specified or inherited method
	private void addProcedureFromMethod(Method m) throws ParseException {
		// a public or protected method returnin an Object is required
		if ((m.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0 ||
				m.getReturnType() != Object.class) {
			return;
		}

		// verify the parameter types
		Class[] pTypes = m.getParameterTypes();
		if (pTypes.length < 1 || pTypes[0] != Map.class) {
			return;
		}
		for (int i = 1; i < pTypes.length; i++) {
			if (pTypes[i] != Object.class) {
				return;
			}
		}
		
		// verify that no checked exceptions are thrown
		Class[] exs = m.getExceptionTypes();
		for (int i = 0; i < exs.length; i++) {
			if (!(Error.class.isAssignableFrom(exs[i]) ||
					RuntimeException.class.isAssignableFrom(exs[i]))) {
				// can't use it if it throws a checked exception
				return;
			}
		}
		
		ProcedureSignature sig = new ProcedureSignature(m.getName(), 
			makeParams(pTypes.length - 1));
		Object previousDef = _procedures.get(sig);
		if ((m.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
			// the method is final. Ensure that the procedure has not been 
			// declared before
			if (previousDef != null && 
					previousDef != EXTERNAL_FINAL_PROCEDURE) {
				throw new ParseException("Procedure \"" + m.getName() +
						"\" cannot overridden");
			}
			
			_procedures.put(sig, EXTERNAL_FINAL_PROCEDURE);
		}
		else {
			if (previousDef != null) {
				// don't interfere with a previous definition
				return;
			}
			if ((m.getModifiers() & Modifier.ABSTRACT) != 0) {
				// the method is abstract - make a requirement declaration
				_procedures.put(sig, null);
			}
			else {
				// the method has been implemented - add an external definition
				_procedures.put(sig, EXTERNAL_PROCEDURE);
			}
		}				
	}

	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================

	/**
	 * The ProcedureSignature holds the signature of a procedure, that is
	 */  
	public static class ProcedureSignature {		
		ProcedureSignature(String name, List params) {
			_name = name;
			_params = params;
		}
		
		
		public int hashCode() {
			return _name.hashCode() * 31 + _params.size();
		}
		
		
		public boolean equals(Object o) {
			if (!(o instanceof ProcedureSignature)) {
				return false;
			}
			
			ProcedureSignature sig = (ProcedureSignature)o;
			return _name.equals(sig._name) && _params.size() == sig._params.size();
		}
		
		
		public String toString() {
			StringBuffer b = new StringBuffer(_name).append('(');
			for (int i = 0, max = _params.size(); i < max; i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append(_params.get(i));
			}
			return b.append(')').toString();
		}
		
		/**
		 * Check the signature for semantical errors, and create a new 
		 * scope of variables.
		 * @return the new variable scope
		 * @exception ParseException if one of the procedure variables
		 *  is a wildcard.
		 */
		public Map checkSymbols() throws ParseException {
			Map scope = new HashMap();
			
			for (int i = 0, max = _params.size(); i < max; i++) {
				Variable v = (Variable)_params.get(i);
				if (v.isWildcard()) {
					AccUtils.throwParseException(v.getInitialToken(),
							"Illegal use of wildcard variable");
				}
				if (scope.containsKey(v.getName())) {
					AccUtils.throwParseException(v.getInitialToken(),
							"Duplicate parameter name");
				}
				v.classifyAsDef(scope);
			}
						
			return scope;
		}

		
		/**
		 * Makes a parameter list from the signature and adds it to the given
		 * string buffer.
		 * @param prefix the string to be prefixed iff the number of parameters
		 *  is larger than zero
		 * @param b the string buffer to which the signature will be added.
		 * @return b
		 */
		public StringBuffer addSignature(String prefix, StringBuffer b) {
			int max = _params.size();
			if (max == 0) {
				return b;
			}
			
			b.append(prefix);
			for (int i = 0; i < max; i++) {
				if (i > 0) {
					b.append(", ");
				}
				b.append("Object ").append(
						((Variable)_params.get(i)).toJavaExpression());
			}
			return b;
		}
		
		
		/**
		 * @return the name of the procedure
		 */
		public String getName() {
			return _name;
		}
		
		
		private String _name;
		private List _params;
	}
}
