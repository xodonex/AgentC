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
 * The HaplomacyDemo uses 4 {@link DemoAgent} instances to control the 
 * 4 players of a default Haplomacy game. 
 *
 * <strong>Note:</strong> The HaplomacyDemo cannot be used by more than one thread.
 *
 * @author  Henrik Lauritzen
 */
public class HaplomacyDemo implements DemoProtocol {

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
	 * Creates a new Haplomacy simulation
	 * @param players the ACMEs used to control the the four players
	 */
	public HaplomacyDemo(DemoAcme[] players) {
		this(players, null);
	}
			
	/**
	 * Creates a new Haplomacy simulation using the default game board.
	 * @param players the ACMEs used to control the the four players
	 * @param random the random number generator to be used for all
	 *   the ACMEs investigators; if the parameter is null, then 
	 *   a new random number generator will be created
	 * @param delay the default timeout to be used for conversations
	 * @see #getPostOffice()
	 */
	public HaplomacyDemo(DemoAcme[] players, Random random) {
		if ((_playerAcmes = players).length != 4) {
			throw new IllegalArgumentException();
		}
		
		_game = new HaplomacyGame();
		_po = new PostOffice();
		
		_players = new DemoAgent[_playerAcmes.length];
		for (int i = 0; i < _players.length; i++) {
			_players[i] = new DemoAgent(_po, _game, _playerAcmes[i], random);
		}
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Start the player agents. Only the first invocation will have any effect.
	 */
	public void start() {
		if (_phase < PHASE_START) {
			_phase = PHASE_START;
			for (int i = 0; i < _players.length; i++) {
				// start the thread
				_players[i].start();
				
				// don't let it time out
				_players[i].pause();
				
				// this case is used by the ACMEs to find their next strategy
				_players[i].getMailbox().add(DemoAgent.FINISHED_RESULTS);
			}
		}
	}
	
	
	/**
	 * Terminate the game simulation, and the involved agents.
	 * Only the first invocation will have any effect.
	 */
	public void terminate() {
		if (_phase < PHASE_START) {
			// terminated before the agents were started
			_phase = PHASE_FINAL;
			return;
		}
		else if (_phase == PHASE_FINAL) {
			// already terminated
			return;
		}

		// kill all the agents
		_phase = PHASE_FINAL;
		for (int i = 0; i < _players.length; i++) {
			_players[i].kill();
		}
	}

	
	/**
	 * Wait until all agents are waiting for new events.
	 * @exception IllegalStateException if the simulation is in the
	 *  negotiation phase when the method is invoked.
	 * @exception InterruptedException if an interruption occurs while waiting.
	 */
	public void waitUntilAgentsDone() throws IllegalStateException,
			InterruptedException {
		switch (_phase) {
		case PHASE_INITIAL:
		case PHASE_FINAL:
			// nothing happens
			return;
		case PHASE_NEGOTIATING:
			throw new IllegalStateException();
		}
		
		// wait until the message queues have been emptied
		for (int i = 0; i < _players.length; i++) {
			_players[i].waitForMessage();
		}		
	}
	

	/**
	 * Determine whether the simulation has been started yet.
	 */
	public boolean isStarted() {
		return _phase > PHASE_INITIAL;
	}
	
	
	/**
	 * Determine whether the simulation has finished.
	 */
	public boolean isFinished() {
		return _phase == PHASE_FINAL;
	}

	
	/**
	 * Determine whether the simulation is active, i.e., whether it has been
	 * started but hasn't finished yet.
	 */
	public boolean isAlive() {
		return _phase > PHASE_INITIAL && _phase < PHASE_FINAL;
	}
	
	
	/**
	 * Determine whether the simulation currently is in the negotiation phase
	 */
	public boolean isNegotiating() {
		return _phase == PHASE_NEGOTIATING;		
	}
	
	
	/**
	 * Determine whether the simulation is currently in an idle state
	 */
	public boolean isIdle() {
		return isAlive() && (_phase & 1) == 0;
	}
	
	
	/**
	 * Determine whether the simulation is currently in an active state
	 */
	public boolean isActive() {
		return isAlive() && (_phase & 1) == 1;
	}
	
		
	/**
	 * Retreive the display component used to visualize the game board
	 */
	public javax.swing.JComponent getDisplay() {
		return _game.getBoard().getDisplayComponent();
	}
	
	
	/**
	 * Retreive the post office used for agent intercommunictaion. 
	 */
	public PostOffice getPostOffice() {
		return _po;
	}

	
	/**
	 * Retrieve the Haplomacy game board being used. No methods should be invoked
	 * on the board unless the demo is {@link #isIdle() idle}.
	 */
	public HaplomacyBoard getGameBoard() {
		return _game.getBoard();
	}
	
	
	/**
	 * Progress to the next simulation phase. The different phases are:
	 * <ol><li value="1">Start (idle phase)
	 * <li value="2">Negotiate (active phase, agents run continuously)
	 * <li value="3">End of negotiations (idle phase)
	 * <li value="4">Order writing (active phase: agents give their orders)
	 * <li value="5">End of order writing (idle phase)
	 * <li value="5">Adjudication (active phase: the game state is updated,
	 *    and agents react to the results)
	 * </ol>
	 * For each invocation of the method, the current phase is updated in
	 * a cyclic way. Upon a transition from an active phase to an idle phase,
	 *  the method will block until all agent activity has ceased.
	 * Transitions from an idle phase to an active phase will happen
	 * without blocking the execution, as soon as all agents have received
	 * suitable orders.
	 * @param negotiateRate the rate at whcich negotiations are performed,
	 *  specified as the number of milliseconds between each new negotiation 
	 *  cycle. The parameter is only used when progressing to the negotiation
	 *  phase.
	 * @return false iff the game has been won by a single player
	 * @exception IllegalStateException if the simulation is not 
	 * {@link #isAlive() alive}.
	 * @exception InterruptedException if an interruption occurs during 
	 *  the phase change.
	 */
	public boolean nextPhase(long negotiateRate) throws IllegalStateException,
			InterruptedException {
		if (!isAlive()) {
			throw new IllegalStateException();
		}
		
		if (isActive()) {
			if (_phase == PHASE_NEGOTIATING) {
				// pause the agents, if they are negotiating
				for (int i = 0; i < _players.length; i++) {
					_players[i].getMailbox().add(DemoAgent.STOP_NEGOTIATE);
					_players[i].pause();
				}
			}
			
			// update the current phase
			_phase = (_phase + 1) % PHASE_COUNT;

			// wait until all existing messages have been processed
			waitUntilAgentsDone();
			
			// update the game display after orders have been written
			if (_phase == PHASE_END_ORDERS) {
				getDisplay().repaint();
			}
			
			// done
			return true;
		}
		
		// update the current phase
		_phase = (_phase + 1) % PHASE_COUNT;
		
		if (_phase == PHASE_NEGOTIATING) {
			// start all the agents with their respective delays
			for (int i = 0; i < _players.length; i++) {
				_players[i].getMailbox().add(DemoAgent.START_NEGOTIATE);
				_players[i].unpause(negotiateRate);
			}
		}
		else if (_phase == PHASE_ORDERS) {
			for (int i = 0; i < _players.length; i++) {
				_players[i].getMailbox().add(DemoAgent.GIVE_ORDERS);
			}
		}
		else if (_phase == PHASE_ADJUDICATION) {
			Set[][] result = _game.update();
			if (result == null) {
				// game over
				return false;
			}
			for (int i = 0; i < _players.length; i++) {
				Set[] results = result[i];
				if (results.length == 0) {
					Map message = new HashMap(6);
					message.put(DefaultMessenger.SENDER, SIMULATION);
					message.put(MESSAGE_TYPE, PLAYER_ELIMINATED);
					message.put(MESSAGE_CONTENTS, new Integer(i));
					_po.broadcast(message, SIMULATION);
					continue;
				}
				Integer player = new Integer(i);
				sendNotifications(player, result[i][0], UNIT_ATTACKED);
				sendNotifications(player, result[i][1], SUPPORT_CENTRE_ATTACKED);
				sendNotifications(player, result[i][2], SUPPORT_CENTRE_CONQUERED);
			}
			for (int i = 0; i < _players.length; i++) {
				_players[i].getMailbox().add(DemoAgent.FINISHED_RESULTS);
			}
		}
		
		return true;
	}

		
	/**
	 * Run a whole series of simulation cycles. 
	 * @return false iff the game has been won by a single player
	 * @param turns the number of turns to simulate
	 * @param negotiateDelay the duration of the negotiation phase, specified
	 *  in milliseconds.
	 * @param negotiateRate the rate at which new conversations are initiated
	 * @param orderDelay the number of milliseconds to wait for the orders to 
	 *   be displayed.
	 * @exception IllegalStateException if the simulation is not 
	 *  {@link #isAlive() alive} and in the fist phase of the simulation cycle
	 * when the method is invoked.
	 * @exception InterruptedException if an interruption occurs during 
	 *  the phase change.
	 */
	public synchronized boolean playTurns(int turns, long negotiateDelay, 
			long negotiateRate, long orderDelay)
			throws IllegalStateException, InterruptedException {
		if (_phase != PHASE_START) {
			throw new IllegalStateException();
		}
		
		boolean result = true;
		for (; turns > 0; turns--) {
			nextPhase(negotiateRate); // PHASE_START -> PHASE_NEGOTIATING
			if (negotiateDelay > 0L) {
				wait(negotiateDelay);
			}
			nextPhase(negotiateRate); // PHASE_NEGOTIATING -> PHASE_END_NEGOTIATING
			nextPhase(negotiateRate); // PHASE_END_NEGOTIATING -> PHASE_ORDERS
			nextPhase(negotiateRate); // PHASE_ORDERS -> PHASE_END_ORDERS
			if (orderDelay > 0L) {
				wait(orderDelay);
			}
			result = nextPhase(negotiateRate); // PHASE_END_ORDERS -> PHASE_ADJUDICATION
			nextPhase(negotiateRate); // PHASE_ADJUDICATION -> PHASE_START
			if (!result) {
				break;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Play a whole game, and record some statistics about the results.
 	 * @param maxTurns the maximal number of terms in a game
	 * @param negotiateDelay the number of milliseconds to wait for negotiations
	 * @param negotiateRate  the delay between new conversations in negotiations
	 * @param orderDelay the number of milliseconds to wait for the orders to 
	 *   be displayed.
	 * @return an int[] instance for each player which holds the following 
	 * information:
	 * <ul><li>Number of turns the player stayed in the game
	 * <li>Number of support centres owned at the end of the game
	 * <li>Number of units owned at the end of the game
	 * </ul>
	 * @exception IllegalStateException if the simulation is not 
	 *  {@link #isAlive() alive} and in the fist phase of the simulation cycle
	 * when the method is invoked.
	 * @exception InterruptedException if an interruption occurs during 
	 *  the phase change.
	 */
	public synchronized int[][] playGame(int maxTurns, long negotiateDelay, 
			long negotiateRate,  long orderDelay) 
			throws IllegalStateException, InterruptedException {
		if (_phase != PHASE_START) {
			throw new IllegalStateException();
		}
		
		int[][] result = new int[3][4];

		for (int i = 0; i < maxTurns; i++) {
			if (!playTurns(1, negotiateDelay, negotiateRate, orderDelay)) {		
				break;
			}

			// update the results
			for (int j = 0; j < 4; j++) {
				if ((result[1][j] = _game.countUnits(j)) > 0) {
					result[0][j]++;
				}
			}
		}
		
		// update the resulting army sizes and the ownerhip informaion
		// one last time
		for (int i = 0; i < 4; i++) {
			result[1][i] = _game.countUnits(i);
			result[2][i] = _game.countSupportCentres(i);
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
	
	public String toString() {
		String prefix = "HaplomacyDemo - ";
		switch (_phase) {
			case PHASE_INITIAL:
				return prefix + "not started yet";
			case PHASE_START:
				return prefix + "idle at start of turn";
			case PHASE_NEGOTIATING:
				return prefix + "negotiating";
			case PHASE_END_NEGOTIATING:
				return prefix + "finished negotiating";
			case PHASE_ORDERS:
				return prefix + "writing orders";
			case PHASE_END_ORDERS:
				return prefix + "finished writing orders";
			case PHASE_ADJUDICATION:
				return prefix + "adjudicating orders";
			case PHASE_FINAL:
				return prefix + "terminated.";
			default:
				throw new InternalError();
		}		
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Class methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Plays a series of demo games using a fixed set of ACMEs.
	 * @param acmes the ACMEs which should play the game
	 * @param games the number of games to be played
	 * @param maxTurns the maximal number of terms in a game
	 * @param negotiateDelay the number of milliseconds to wait for negotiations
	 * @param negotiateRate the number of milliseconds to wait before 
	 *  a conversation can be started
	 * @exception InterruptedException if an interruption occurs during 
	 *  the game.
	 */
	public static List playGames(DemoAcme[] acmes, int games,
			int maxTurns, long negotiateDelay, long negotiateRate) 
			throws InterruptedException {
		List results = new ArrayList(games);
		
		for (int i = 0; i < games; i++) {
			HaplomacyDemo demo = new HaplomacyDemo(acmes, null);
			demo.start();			
			int[][] result = demo.playGame(maxTurns, negotiateDelay, 
					negotiateRate, 0L);
			demo.terminate();
			results.add(result);
		}
		
		return results;
	}
	
	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	
	// phase descriptions
	private final static int 
		PHASE_INITIAL = -1,			// no threads are started yet
		PHASE_START = 0,			// nothing happens; paused
		PHASE_NEGOTIATING = 1,		// agents negotiate, unpaused
		PHASE_END_NEGOTIATING = 2,	// nothing happens; paused
		PHASE_ORDERS = 3,			// agents write orders; paused
		PHASE_END_ORDERS = 4,		// nothing happens; paused
		PHASE_ADJUDICATION = 5,		// orders are resolved, results handled; paused
		PHASE_FINAL = Integer.MAX_VALUE, // simulation finished
		PHASE_COUNT = 6;			// number of phases in the normal cycle
	
	// ------------------------------ instance -----------------------------
	
	// the player ACMEs 
	private DemoAcme[] _playerAcmes;
	
	// the player agents
	private DemoAgent[] _players;
	
	// the post office used for intercommunication
	private PostOffice _po;
	
	// the Haplomacy game instance
	private HaplomacyGame _game;

	
	// the current simulation phase
	private int _phase = PHASE_INITIAL; 
	
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	private void sendNotifications(Integer receiver, Collection ids, Object msg) {
		Map message;
		
		for (Iterator it = ids.iterator(); it.hasNext(); ) {
			message = new HashMap(8);
			message.put(DefaultMessenger.RECEIVER, receiver);
			message.put(DefaultMessenger.SENDER, SIMULATION);
			message.put(MESSAGE_TYPE, msg);
			message.put(MESSAGE_CONTENTS, it.next());
			_players[receiver.intValue()].getMailbox().add(message);
		}		
	}

	
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
