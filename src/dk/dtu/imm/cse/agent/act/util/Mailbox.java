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
 * The Mailbox allows buffering of messages and allows different threads
 * to read and write.
 *
 * @author  Henrik Lauritzen
 */
public class Mailbox {

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
	 * Creates a new Mailbox.
	 */
	public Mailbox() {
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the current number of buffered messages
	 */
	public int size() {
		synchronized (_buffer) {
			return _buffer.size();
		}
	}
	
	
	/**
	 * Places a new message into the message buffer.
	 * @param msg the message
	 * @exception NullPointerException if the message is null
	 */
	public void add(Map msg) throws NullPointerException {
		if (msg == null) {
			throw new NullPointerException();
		}
		synchronized (_buffer) {
			_buffer.addLast(msg);
			if (_buffer.size() == 1) {
				_buffer.notify();
			}
		}
	}
	
	
	/**
	 * Places a collection of new messages into the message buffer at once.
	 *  If the operation fails for either reason, the buffer will not be
	 *  modified.
	 * @param msgs the messages
	 * @exception NullPointerException if either message is null. 
	 * @exception ClassCastException if either message is not a Map instance.
	 */
	public void add(Collection msgs) 
			throws ClassCastException, NullPointerException {
		if (msgs.size() == 0) {
			return;
		}
		
		for (Iterator i = msgs.iterator(); i.hasNext(); ) {
			Object obj = i.next();
			if (obj == null) {
				throw new NullPointerException();
			}
			else if (!(obj instanceof Map)) {
				throw new ClassCastException(obj.getClass().getName());
			}
		}
		
		synchronized (_buffer) {
			boolean notify = _buffer.size() == 0;
			_buffer.addAll(msgs);
			if (notify) {
				_buffer.notify();
			}
		}
	}

	
	/**
	 * Retreives the next message from the buffer, if a message is available.
	 * @param extract whether the message should be removed from the mailbox.
	 * @return null iff the buffer was empty. Otherwise, a Map instance
	 *  will be returned.
	 */
	public Map peek(boolean extract) {
		synchronized (_buffer) {
			if (_buffer.size() == 0) {
				return null;
			}
			return (Map)(extract ? _buffer.removeFirst() :_buffer.get(0));
		}
	}
	
	
	/**
	 * Corresponds to <code>get(0)</code>
	 */
	public Map get() throws InterruptedException {
		return get(0L);		
	}

	
	/**
	 * Extracts the next message from the buffer. The operation will 
	 * be suspended until a message is available, or until the given
	 * timeout has expired. 
	 * @param timeout the maximal number of milliseconds to wait for a
	 *  new message. A value of 0 is used to represent an infinite timeout.
	 * @return null iff the operation timed out before a message was available.
	 *  Otherwise, the first message in the queue is removed and returned.
	 * @exception InterruptedException if an interruption occurs while
	 * operation is suspended.
	 */
	public Map get(long timeout) throws InterruptedException {
		synchronized (_buffer) {			
			while (_buffer.size() == 0) {
				_buffer.wait(timeout);
				if (timeout > 0) {
					return _buffer.size() == 0 ? 
							null : (Map)_buffer.removeFirst();
				}
			}
			return (Map)_buffer.removeFirst();
		}
	}
	
	
	/**
	 * Retreives a number of messages from the buffer. 
	 * @param size the requested number of messages to be received. If 
	 *  negative, the entire contents of the mailbox will be returned.
	 * @param remove whether the messages returned should be removed from
	 *  the mailbox.
	 * @return a Collection containing as many of the first messages
	 *  as possible, limited by <code>size</code> and the current size of the 
	 *  buffer.
	 */
	public Collection extract(int size, boolean remove) {
		synchronized (_buffer) {
			int avail = _buffer.size();
			if (avail == 0 || size == 0) {
				return Collections.EMPTY_LIST;
			}
			
			if (size > avail || size < 0) {
				size = avail;
			}
			Collection result = new ArrayList(size);
			Iterator it = _buffer.iterator();
			for (int i = 0; i < size; i++) {
				result.add(it.next());
				if (remove) {
					it.remove();
				}
			}
			
			return result;
		}
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
	
	// the buffered messages are added to tail and extracted from the 
	// head
	private LinkedList _buffer = new LinkedList();
	
		
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