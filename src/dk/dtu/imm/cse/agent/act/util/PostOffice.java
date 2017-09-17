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
 * The PostOffice maintains a collection of Mailboxes, and allows messages 
 * to be sent or broadcast to these.
 *
 * @author  Henrik Lauritzen
 */
public class PostOffice {
	
	
	
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
	 * Creates a new, initially empty post office.
	 */
	public PostOffice() {
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------
	
	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	
	/**
	 * Registers a new address at the post office, that is, ensure that
	 *  a mailbox exists for the given ID.
	 * @param id the address of the new mailbox
	 * @return the Mailbox instance which will be used to buffer messages for
	 *  the recipient.
	 */
	public synchronized Mailbox register(Object id) {
		Mailbox result = (Mailbox)_boxes.get(id);
		if (result == null) {
			_boxes.put(id, result = new Mailbox());
		}
		return result;
	}
	
	
	/**
	 * Removes a user from the post office.
	 * @param id the address of the user to be removed.
	 * @return null iff the user did not exist; otherwise, the user's
	 *  mailbox is returned
	 */
	public synchronized Mailbox unregister(Object id) {
		return (Mailbox)_boxes.remove(id);
	}
	
	
	/**
	 * Retreives the mailbox for a specified address.
	 * @param id the address
	 * @return null iff the user has not been registered
	 */
	public synchronized Mailbox getMailbox(Object id) {
		return (Mailbox)_boxes.get(id);
	}
	
	
	/**
	 * @return a collection of all registered addresses.
	 */
	public synchronized Collection getAllUsers() {
		return new ArrayList(_boxes.keySet());
	}
	
	
	/**
	 * Sends a message to a single address.
	 * @param msg the message
	 * @param receiver the receiver's address
	 * @return true iff the user did exist
	 * @exception NullPointerException iff the message is null
	 */
	public synchronized boolean send(Map msg, Object receiver) {
		Mailbox box = (Mailbox)_boxes.get(receiver);
		if (box != null) {
			box.add(msg);
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Broadcasts a message to all registered addresses.
	 * @param msg the message
	 * @param sender the sender's address; the message will not
	 *  be broadcast to this address.
	 * @return the number of receivers.
	 * @exception NullPointerException iff the message is null
	 */
	public synchronized int broadcast(Map msg, Object sender) {
		if (msg == null) {
			throw new NullPointerException();
		}
		
		int result = 0;
		for (Iterator i = _boxes.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			if (sender == null ? e.getKey() == null : sender.equals(e.getKey())) {
				continue;
			}
			((Mailbox)e.getValue()).add(msg);
			result++;
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
	
	// Maps a user ID to the Mailbox instance registered for that user.
	private Map _boxes = new HashMap();
	
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