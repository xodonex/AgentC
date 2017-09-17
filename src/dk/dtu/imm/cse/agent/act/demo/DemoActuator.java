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

import java.util.*;
import dk.dtu.imm.cse.agent.act.afc.*;
import dk.dtu.imm.cse.agent.act.testbed.*;
import dk.dtu.imm.cse.agent.act.util.*;


/**
 * The DemoActuator allows the {@link DemoAcme} to give its units
 * their orders. In addition, the DemoActuator provides a simple
 * output routine for debugging.
 *
 * @author  Henrik Lauritzen
 */
public class DemoActuator extends GenericActuator {

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
	 * Constructs a new actuator. 
	 * @param game the game to which orders should be given
	 * @param id the ACME's ID
	 * @param player the player number
	 */
	public DemoActuator(HaplomacyGame game, Object id, int player) {
		super();
		_game = game;
		_id = id;
		_player = player;
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Prints the concatenation of the given parameters to {@link System#out}.
	 */
	public void print(Object[] params) {
		synchronized (System.out) {
			for (int i = 0; i < params.length; i++) {
				System.out.print(params[i]);
			}
			System.out.println();
		}
	}
	
	
	/** 
	 * This method is used to register a given player as being friendly, meaning
	 * that the player should neither be the target of an 
	 * {@link #attack(Object) attack}, nor of a {@link #defend() defensive move}.
	 */
	public boolean registerFriend(Object player) {
		_friendly.add(player);
		return true;
	}
	
	
	/** 
	 * This method is used to cancel a previously
	 * {@link #registerFriend(Object) registered} friend.
	 */
	public boolean unregisterFriend(Object player) {
		_friendly.remove(player);
		return true;
	}
	
	
	/**
	 * Clears the set of currently {@link #registerFriend(Object) registered}
	 *  friendly players.
	 */
	public boolean resetFriends() {
		_friendly.clear();
		return true;
	}
	
	
	/**
	 * This method orders the player's units to be defensive, using the
	 * previously {@link #registerFriend(Object) registered}
	 * friendly players. The set of friendly players is reset afterwards.
	 */
	public boolean defend() {
		synchronized (_game) {
			// explicit synchronization is necessary here
			_game.giveDefensiveOrders(_player, _friendly, Collections.EMPTY_LIST);
		}
		return true;
	}

	
	/**
	 * This method orders the player's units to be offensive, using the
	 * previously {@link #registerFriend(Object) registered}
	 * friendly players. The set of friendly players is reset afterwards.
	 * @param strength a Double value defining the percentage of the player's
	 * units which should participate in the attack.
	 */
	public boolean attack(Object strength) {
		boolean result;
		synchronized (_game) {
			// explicit synchronization is necessary here
			result = _game.giveOffensiveOrders(_player, 
					((Double)strength).doubleValue(), _friendly, 
					Collections.EMPTY_LIST);
		}
		return result;
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
	
	public boolean xeq(String name, Object[] params) {
		if ("print".equals(name)) {
			print(params);
			return true;
		}
		else {
			return super.xeq(name, params);
		}
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

	// the game simulation
	private HaplomacyGame _game;
	
	// the player number
	private int _player;
	
	// the player id
	private Object _id;

	// the set of friendly players
	private Set _friendly = new HashSet(8);
	
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
