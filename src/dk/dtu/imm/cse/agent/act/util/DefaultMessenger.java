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

import dk.dtu.imm.cse.agent.act.afc.*;


/**
 * The DefaultMessenger uses a {@link PostOffice} to send and receive messages.
 *
 * @author  Henrik Lauritzen
 */
public class DefaultMessenger implements Messenger {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	
	/**
	 * The attribute name used to hold the receiver of a message.
	 */
	public final static String RECEIVER = "to";
	
	
	/**
	 * The attribute name used to hold the sender of a message
	 */
	public final static String SENDER = "from";
	

	/**
	 * The attribute name used to hold the type of of a message
	 */
	public final static String TYPE = "type";

	
	/**
	 * The attribute name used to hold the contents of a message
	 */
	public final static String CONTENTS = "contents";
	
	
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
	 * Creates a new messenger component.
	 * @param po the {@link PostOffice} used to distribute messages.
	 * @param acme the ACME for which messenger should be used.
	 */
	public DefaultMessenger(PostOffice po, Acme acme) {
		_inbox = (_po = po).register(_id = acme.getId());
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/** 
	 * @return the Mailbox used by this messenger
	 */
	public Mailbox getMailbox() {
		return _inbox;
	}
	
	// ------------------------------ protected ------------------------------
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * Sends the message to its recipient(s). The sender's ID will automatically
	 *  be added to the message, using {@link #SENDER} as the key, if no such
	 *  information already exists. If no value exists under the 
	 * {@link #RECEIVER} key, the message will be broadcast to all receivers
	 *  at the post office.
	 */
	public void send(Map msg) {
		if (!msg.containsKey(SENDER)) {
			msg.put(SENDER, _id);
		}
		
		Object recipient = msg.get(RECEIVER);
		if (recipient == null) {
			_po.broadcast(msg, _id);
		}
		else {
			_po.send(msg, recipient);
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
	// ------------------------------ instance -----------------------------
	
	// the post office used to deliver messages
	private PostOffice _po;
	
	// the mailbox used to receive messages
	private Mailbox _inbox;
	
	// the ID used to identify the sender
	private Object _id;
		
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
