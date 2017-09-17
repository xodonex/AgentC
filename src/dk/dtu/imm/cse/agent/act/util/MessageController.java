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
 * The MessageController thread is the basis of a communicative and/or reactive
 * constituent based on GMI messages. It delegates to the subclass to handle
 * messages which are received, but provides the functionality to suspend
 * execution and to generate events at a fixed rate in the absence of messages.
 *
 * @author  Henrik Lauritzen
 */
public abstract class MessageController extends Thread {

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
	 * Constructs a new message controller
	 * @see #start(Mailbox)
	 */
	public MessageController() {
		super();
	}
	
	
	/** 
	 * Constructs a new message controller in the given thread group.
	 * @see #start(Mailbox)
	 */
	public MessageController(ThreadGroup tg) {
		super(tg, "Thread-" + tg.activeCount());
	}
	

	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * Resume execution of the thread
	 * @param timeout the maximal period, in milliseconds, which will elapse
	 *  before an execution cycle is performed. If the value is not
	 *  positive, then an execution cycle will only take place when a message
	 *  has been received.
	 * @return true iff the thread was still active at the time of the method
	 * invocation
	 */
	public synchronized boolean unpause(long delay) {
		if (_finished) {
			return false;
		}
		_delay = delay;
		_mbox.add(START);
		return true;
	}
	
	
	/**
	 * Pause execution of the thread.
	 * @return true iff the thread was still active at the time
	 * of the execution
	 */
	public synchronized boolean pause() {
		if (_finished) {
			return false;
		}
		_delay = 0L;
		_mbox.add(STOP);
		return true;
	}
	
	
	/**
	 * Terminate the thread, i.e., ensure that no new messages are processed
	 * by the thread. Any messages buffered before this method returns
	 * will continue to be processed before the thread dies. 
	 * Use {@link Thread#join()} to ensure that all messages have been processed.
	 * The method can be invoked multiple times, but only the first invocation
	 * will have any effect.
	 */
	public synchronized void kill() {
		if (_finished) {
			// already killed
			return;
		}
		_mbox.add(TERMINATE);
		_finished = true;		
	}
	
	
	/**
	 * @return the mailbox used for receiving messages.
	 */
	public Mailbox getMailbox() {
		return _mbox;
	}

	
	/**
	 * Starts the thread.
	 * @param mailbox the mailbox to be used to receive messages.
	 * @exception IllegalThreadStateException if the thread was already started
	 * @see Thread#start()
	 */
	public synchronized void start(Mailbox mbox) 
			throws IllegalThreadStateException {
		if (_mbox != null) {
			// the thread was already started
			throw new IllegalThreadStateException();
		}
		_mbox = mbox == null ? new Mailbox() : mbox;
		super.start();
	}

	
	/**
	 * Blocks the calling thread until all messages found in the mailbox
	 * prior to the invocation have been handled. 
	 * <strong>Note:</strong> If the MessageController
	 * has been terminated prior to the call, then the method will
	 * return without blocking the calling thread.
	 * @exception InterruptedException if the calling thread is interrupted
	 *  while waiting.
	 */
	public void waitForMessage() throws InterruptedException {
		Map marker = new MarkerMessage();
		synchronized (marker) {
			synchronized (this) {
				if (_finished) {
					return;
				}
				_mbox.add(marker);
			}
			marker.wait();
		}
	}
	
	// ------------------------------ protected ------------------------------
	
	/**
	 * Handle a message received from the mailbox.
	 * @param msg the message to be handled. The value null indicates that
	 * the maximal delay elapsed before a message was available.
	 * @param paused whether the controller was paused when the message was
	 *  received 
	 */
	protected abstract void handleMessage(Map msg, boolean paused);
	

	/**
	 * This method is executed every time the message controller is paused.
	 */
	protected void paused() {
	}
	
	
	/**
	 * This method is executed every time the message controller is unpaused.
	 */
	protected void unpaused() {
	}
	
	
	/**
	 * This method is executed exactly once, as the last operation executed
	 * by the thread.
	 */
	protected void killed() {
	}
	
	// =======================================================================
	// Implementations of abstract methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------

	/**
	 * The main execution loop, which handles messages.
	 */
	public final void run() {
		// whether the thread is paused
		boolean paused = false;
		
		try {			
			while (true) {
				Map msg;
				try {
					msg = _mbox.get(_delay);
				}
				catch (InterruptedException e) {
					// handle an interrupt as a timeout
					msg = null;
				}

				if (msg == TERMINATE) {
					// stop executing
					break;
				}
				else if (msg == STOP) {
					paused = true;
					paused();
				}
				else if (msg == START) {
					paused = false;
					unpaused();
				}
				else if (msg instanceof MarkerMessage) {
					// wake up the waiting thread
					synchronized (msg) {
						msg.notify();
					}
				}
				else {
					// let the subclass determine how to handle the message
					handleMessage(msg, paused);
				}
			}			
		}
		finally {
			// ensure that the kill() method is invoked, even if 
			// the thread dies as the result of an uncaught exception
			// in a handler method
			kill();
			
			// notify that the thread has been killed
			killed();
		}
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------	
	
	/**
	 * This is equivalent to {@link #start(Mailbox) start(new Mailbox())}.
	 */
	public void start() {
		start(null);
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
	
	// the message used to indicate an elapsed interval
	private final static Map TIME_OUT = new HashMap(1);
	
	// the message used to indicate that execution should be started
	private final static Map START = new HashMap(1);

	// the message used to indicate that execution should be stopped
	private final static Map STOP = new HashMap(1);
	
	// the message used to indicate that the thread should be terminated
	private final static Map TERMINATE = new HashMap(1);
	
		
	// ------------------------------ instance -----------------------------

	// the mailbox used for messages
	private Mailbox _mbox;

	// whether execution has finished
	private boolean _finished = false;
	
	// the timeout delay in the main loop.
	// NOTE: the value is declared volatile because it is 
	// concurrently updated and read by different threads.
	private volatile long _delay = 0L;

	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------
	
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================

	// special message used to mark a certain point in the incoming message
	// sequence.
	private static class MarkerMessage extends HashMap {
		MarkerMessage() {
			super(1);
		}		
	}
}