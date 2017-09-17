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


/**
 * Tagging interface containing various constants used in the ACC.
 *
 * @author  Henrik Lauritzen
 */
public interface AccConstants {

	// =======================================================================
	// Fields
	// =======================================================================

	/**
	 * The ID used for the "equal to" comparison
	 */
	public int COMP_EQ = 0;
	
	
	/**
	 * The ID used for the "not equal to" comparison
	 */
	public int COMP_NE = 1;
	
	
	/**
	 * The ID used for the "less than" comparison
	 */
	public int COMP_LT = 2;
	
	
	/**
	 * The ID used for the "less than or equal to" comparison
	 */
	public int COMP_LE = 3;

	
	/**
	 * The ID used for the "greater than or equal to" comparison
	 */
	public int COMP_GE = 4;
	
	
	/**
	 * The ID used for the "greater than" comparison
	 */
	public int COMP_GT = 5;
	

	/**
	 * The type identifier for a literal of type int
	 */
	public int TYPE_INT = 0;

	
	/**
	 * The type identifier for a literal of type double
	 */
	public int TYPE_DOUBLE = 1;
	
	
	/**
	 * The type identifier for a literal of type String
	 */
	public int TYPE_STRING = 2;
	

	/**
	 * The type identifier used in a "literal" encoding of a constant
	 *  derived from an interface
	 */
	public int TYPE_EXTERNAL = 3;
	
	
	/**
	 * A pattern used to generate constant names in the translated code.
	 * Parameter 0 is the AgentC name of the constant.
	 */
	public String CONST_PATTERN = "C_{0}";

	
	/**
	 * A pattern used to generate variable names in the translated code.
	 * Parameter 0 is the AgentC name of the variable, parameter 1 is the
	 * unique ID assigned to distinguish the variable.
	 */
	public String VAR_PATTERN = "v{1}_{0}";
	
	
	/**
	 * The name used for the temporary Object variable in the generated 
	 *  procedures.
	 */
	public String TEMP_OBJ_NAME = "tempObj";

	
	/**
	 * The name used for the temporary Map variable in the generated procedures.
	 */
	public String TEMP_MAP_NAME = "tempMap";
	
	
	/**
	 * The name used for the temporary BitSet variable in the generated ACME
	 */
	public String TEMP_BS_NAME = "tempBitSet";

	
	/**
	 * The name used for the temporary Fact variable in the 
	 * generated procedures
	 */
	public String TEMP_FACT_NAME = "tempFact";


	/**
	 * The name used for the temporary KnowledgeBase variable in the 
	 * generated procedures
	 */
	public String TEMP_KB_NAME = "kBase";
	
	
	/**
	 * The name used for the message parameter of the generated procedures.
	 */
	public String MESSAGE_MAP_NAME = "msg";

	
	/**
	 * The name used for the boolean variables in code generated for
	 * an IF ... statement.
	 */
	public String MATCH_NAME = "matched";
	
	
	/**
	 * The name used for the List variables used to hold matched facts 
	 * in code generated for  an IF ... statement.
	 */
	public String MATCH_LIST_NAME = "match";

	
	/**
	 * The name used for loop index variables in code generated for
	 * an IF ... statement.
	 */
	public String LOOP_INDEX_NAME = "i";


	/**
	 * The name used for loop bound variables in code generated for
	 * an IF ... statement.
	 */
	public String LOOP_BOUND_NAME = "max";
	
	// =======================================================================
	// Methods
	// =======================================================================
		
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
