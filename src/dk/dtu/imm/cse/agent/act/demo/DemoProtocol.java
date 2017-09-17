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
package dk.dtu.imm.cse.agent.act.demo;


/**
 * Tagging interface defining various communication-related constants.
 *
 *
 * @author  Henrik Lauritzen
 */
public interface DemoProtocol {

	// =======================================================================
	// Fields
	// =======================================================================

	/**
	 * Message sender ID indicating a message from the simulation itself.
	 */
	public String SIMULATION = "simulation";
	
	
	/**
	 * Message attribute name indicating the type of a message
	 */
	public String MESSAGE_TYPE = "type";
	
	
	/**
	 * Message attribute name indicating the contents of a message
	 */
	public String MESSAGE_CONTENTS = "contents";

	
	/**
	 * Message type indicating that a player's unit was attacked by another
	 *  player.
	 */
	public String UNIT_ATTACKED = "unitAttacked";

	
	/**
	 * Message type indicating that a player's support centre was attacked by 
	 * another player.
	 */
	public String SUPPORT_CENTRE_ATTACKED = "supportCentreAttacked";
	
	
	/**
	 * Message type indicating that a player's support centre was overtaken
	 * by by another player.
	 */
	public String SUPPORT_CENTRE_CONQUERED = "supportCentreConquered";
	
	
	/**
	 * Message type indicating that a player was eliminated
	 */
	public String PLAYER_ELIMINATED = "playerEliminated";
	
	
	/**
	 * Message type indicating that a player requests something from anohther player.
	 */
	public String REQUEST = "request";
	
	
	/**
	 * Message type indicating that a player accepts something.
	 */
	public String ACCEPT = "accept";
	
	
	/**
	 * Message type indicating that a player rejects something.
	 */
	public String REJECT = "reject";
	
	// =======================================================================
	// Methods
	// =======================================================================
		
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
