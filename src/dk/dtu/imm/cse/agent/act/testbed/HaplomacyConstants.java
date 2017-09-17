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
package dk.dtu.imm.cse.agent.act.testbed;

/**
 * Tagging interface providing constants for the Haplomacy game.
 *
 * @author  Henrik Lauritzen
 */
public interface HaplomacyConstants {

	// =======================================================================
	// Fields
	// =======================================================================
	
	/**
	 * Defines the least number of players allowed
	 */
	public int MIN_PLAYERS = 2;
	
	
	/**
	 * Defines the largest number of players allowed
	 */
	public int MAX_PLAYERS = 7;
		

	/**
	 * Defines the lowest player id.
	 */
	public int PLAYER_MIN = 0;
	
	
	/**
	 * Defines the highest player id
	 */
	public int PLAYER_MAX = MAX_PLAYERS - 1;

	
	/**
	 * Defines the player ID used to indicate a neutral support centre.
	 */
	public int PLAYER_NEUTRAL = PLAYER_MAX + 1;
	
	
	/**
	 * The base size, in pixels, used to draw a unit
	 */
	public int UNIT_SIZE = 24;
	
	
	// =======================================================================
	// Methods
	// =======================================================================
		
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
