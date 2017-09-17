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


/**
 * This class serves as the base implemtation for Haplomacy player ACMEs.
 * It contains various definitions, provides facilities for logging the
 * ACMEs actions, and specified the AgentC procedures which need to be
 * defined.
 *
 * @author  Henrik Lauritzen
 */
public abstract class DemoAcme extends Acme implements DemoProtocol {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	
	/**
	 * ID of the red player
	 */
	public final static Integer RED = new Integer(0);

	
	/**
	 * ID of the blue player
	 */
	public final static Integer BLUE = new Integer(1);


	/**
	 * ID of the green player
	 */
	public final static Integer GREEN = new Integer(2);
	
	
	/**
	 * ID of the yellow player
	 */
	public final static Integer YELLOW = new Integer(3);
	
	
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
	
	protected DemoAcme(Object id) {
		super(id);
		_log = Collections.synchronizedList(new ArrayList());
	}
	
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * This AgentC procedure will be called once, when the agent is started.
	 */
	public Object init(Map msg) {
		return null;
	}
	

	/**
	 * This AgentC procedure is called repeatedly during the negotiation phase.
	 * <strong>Note</strong>: a new conversation may <em>only</em> be inititiated
	 * if the given message is null (WHEN NOTHING ...).
	 */
	public abstract Object negotiate(Map msg);
	
	
	/**
	 * This AgentC procedure is called once, after the the negotiation phase
	 * has ended.
	 */
	public abstract Object giveOrders(Map mgs);
	
	
	/**
	 * This AgentC procedure is called once for every status message after the
	 *  adjudication phase.
	 * <strong>Note:</strong>No new messages may be produced from this procedure.
	 */
	public abstract Object updateStatus(Map msg);
	
	
	
	/**
	 * Determine whether incoming messages should be logged.
	 */
	public boolean isReceivedMessageLogged() {
		return _logReceived;
	}
	
	
	/**
	 * Toggle whether received messages should be logged. Note that the
	 * ACME itself cannot log these messages.
	 */
	public void setReceivedMessageLogged(boolean logged) {
		_logReceived = logged;
	}
	

	/**
	 * Determine whether outgoing messages should be logged.
	 */
	public boolean isSentMessageLogged() {
		return _logSent;
	}
	
	
	/**
	 * Toggle whether outgoing messages should be logged.
	 */
	public void setSentMessageLogged(boolean logged) {
		_logSent = logged;
	}
	
	/**
	 * Determine whether the actions of the ACME sould be logged.
	 */
	public boolean isActionLogged() {
		return _logActions;
	}
	
	
	/**
	 * Toggle whether actions should be logged.
	 */
	public void setActionLogged(boolean logged) {
		_logActions = logged;
	}
	
	/**
	 * Determine whether the queries of the ACME are logged.
	 */
	public boolean isQueryLogged() {
		return _logQueries;
	}
	
	/** 
	 * Toggle whether the ACMEs queries should be logged.
	 */ 
	public void setQueryLogged(boolean logged) {
		_logQueries = logged;
	}
	
	
	/**
	 * Retreive the ACME's log.
	 * @return a list containing an Object[] entry for each action or query.
	 *  The log should preferrably also include the messages received, as 
	 *  Map instances (or null), but the ACME cannot log these messages itself.
	 * The returned list is thread safe. 
	 */
	public List getLog() {
		return _log;
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
	
	public boolean xeqAction(String name, Object[] terms) {
		boolean result = super.xeqAction(name, terms);
		if (_logActions) {
			_log.add(new Object[] {"XEQ", name, terms, 
					result ? Boolean.TRUE : Boolean.FALSE});			
		}
		return result;
	}
	
	
	public boolean doAction(String name, Object[] terms) {
		boolean result = super.doAction(name, terms);
		if (_logActions) {
			_log.add(new Object[] {"DO", name, terms, 
					result ? Boolean.TRUE : Boolean.FALSE});
		}
		return result;
	}
	
	
	public Object query(String name, Object[] terms) {
		Object result = super.query(name, terms);
		if (_logQueries) {
			_log.add(new Object[] {"Q", name, terms, result });
		}
		return result;
	}
	

	public void send(Map msg) {
		if (_logSent) {
			_log.add(new Object[] {"SAY", msg});
		}
		super.send(msg);
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
	
	// a log of the ACME's actions 
	private List _log;

	// whether incoming or outgoing messages, actions and queries 
	// should be logged.
	private volatile boolean _logReceived, _logSent, _logActions, _logQueries;
		
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
