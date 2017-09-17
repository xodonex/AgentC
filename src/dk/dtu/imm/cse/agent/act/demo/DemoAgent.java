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
 * The DemoAgent extends the generic {@link MessageController}, and is
 * effectively both the reactive and communicative constituent of the
 * agent. The DemoAgent's behaviour is controlled by the {@link Acme ACME}
 * from which it is composed, and by the {@link HaplomacyDemo} in which
 *  it is situated.
 *
 * @author  Henrik Lauritzen
 */
public class DemoAgent extends MessageController {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	
	/**
	 * A special message used to notify the agent that the negotion phase
	 *  has started.
	 */
	public final static Map START_NEGOTIATE = new HashMap(1);	

	
	/**
	 * A special message used to notify the agent that the negotion phase
	 *  has ended.
	 */
	public final static Map STOP_NEGOTIATE = new HashMap(1);

	
	/**
	 * A special message used to notify the agent that it should give its
	 * orders.
	 */
	public final static Map GIVE_ORDERS = new HashMap(1);
	
	
	/**
	 * A special message used to notify the agent that all status messages
	 * have been sent.
	 */
	public final static Map FINISHED_RESULTS = new HashMap(1);
	
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
	 * Constructs a new DemoAgent.
	 * @param po the PostOffice at which the messenger should be registered.
	 * @param game the HaplomacyGame instance used to represent the game simulation.
	 * @param acme the DemoAcme speicyfing the agent's behaviour.
	 * @param random the random number generator to be used by the investigator. 
	 * If the given value is null, then a new random number generator will be 
	 *   created.
	 */
	public DemoAgent(PostOffice po, HaplomacyGame game, 
			DemoAcme acme, Random random) {		
		DefaultMessenger mgr = new DefaultMessenger(po, acme);
		Integer id = (Integer)acme.getId();
		(_acme = acme).init(
				new DemoActuator(game, id, id.intValue()), mgr, 
				new DemoInvestigator(game, id.intValue(), random));
		_mbox = mgr.getMailbox();
		
		// execute the custom initialization code
		_acme.init((Map)null);
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
	// ------------------------------ protected ------------------------------
	
	protected void handleMessage(Map msg, boolean paused) {
		if (msg == START_NEGOTIATE) {
			_negotiating = true;
		}
		else if (msg == STOP_NEGOTIATE) {
			_negotiating = false;
		}
		else if (msg == GIVE_ORDERS) {
			_acme.giveOrders((Map)null);
		}
		else if (msg == FINISHED_RESULTS) {
			_acme.updateStatus((Map)null);
		}
		else {
			if (_acme.isReceivedMessageLogged()) {
				_acme.getLog().add(msg);
			}
			if (_negotiating) {
				_acme.negotiate(msg);
			}
			else {
				_acme.updateStatus(msg);
			}
		}
	}
	

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public void start() {
		start(_mbox);
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

	// the mental engine used 
	private DemoAcme _acme;
	
	// the mailbox used by the acme
	private Mailbox _mbox;

	// the current status
	private boolean _negotiating;
	
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