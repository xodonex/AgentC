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

import dk.dtu.imm.cse.agent.act.testbed.*;
import dk.dtu.imm.cse.agent.act.util.*;


/**
 * The DemoInvestigator provides basic arithmetic operations, a random
 * number generation facility and answers queries regarding the strength
 * of the players.
 *
 * @author  Henrik Lauritzen
 */
public class DemoInvestigator extends GenericInvestigator {

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

	public DemoInvestigator(HaplomacyGame game, int player) {
		this(game, player, null);
	}
	
	
	public DemoInvestigator(HaplomacyGame game, int player, Random rnd) {
		super();
		if ((_game = game) == null) {
			throw new NullPointerException();
		}
		_player = player;
		_r = rnd != null ? rnd : new Random();
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Performs numeric addition on the two operands, which must be 
	 *  {@link Number} instances. The return value will be an 
	 *  {@link Integer} iff both of the operands are Integer instances,
	 *  and a {@link Double} otherwise.
	 */
	public Object add(Object x, Object y) throws ClassCastException {
		if (!(x instanceof Number)) {
			return x == null ? y.toString() : x.toString() + y.toString();
		}
		
		Number n1 = (Number)x;
		Number n2 = (Number)y;
		
		if (x instanceof Integer && y instanceof Integer) {
			return new Integer(n1.intValue() + n2.intValue());
		}
		else{
			return new Double(n1.doubleValue() + n2.doubleValue());
		}
	}
	
	
	/**
	 * Performs numeric subtraction on the two operands, which must be 
	 *  {@link Number} instances. The return value will be an 
	 *  {@link Integer} iff both of the operands are Integer instances,
	 *  and a {@link Double} otherwise.
	 */
	public Number sub(Object x, Object y) throws ClassCastException {
		Number n1 = (Number)x;
		Number n2 = (Number)y;
		
		if (x instanceof Integer && y instanceof Integer) {
			return new Integer(n1.intValue() - n2.intValue());
		}
		else {
			return new Double(n1.doubleValue() - n2.doubleValue());
		}
	}
	
	
	/**
	 * Performs numeric multiplication on the two operands, which must be 
	 *  {@link Number} instances. The return value will be an 
	 *  {@link Integer} iff both of the operands are Integer instances,
	 *  and a {@link Double} otherwise.
	 */
	public Number mul(Object x, Object y) throws ClassCastException {
		Number n1 = (Number)x;
		Number n2 = (Number)y;
		
		if (x instanceof Integer && y instanceof Integer) {
			return new Integer(n1.intValue() * n2.intValue());
		}
		else {
			return new Double(n1.doubleValue() * n2.doubleValue());
		}
	}
	
	
	/**
	 * Performs numeric division on the two operands, which must be 
	 *  {@link Number} instances. The return value will be an 
	 *  {@link Integer} iff both of the operands are Integer instances,
	 *  and a {@link Double} otherwise.
	 */
	public Number div(Object x, Object y) throws ClassCastException {
		Number n1 = (Number)x;
		Number n2 = (Number)y;
		
		if (x instanceof Integer && y instanceof Integer) {
			return new Integer(n1.intValue() / n2.intValue());
		}
		else {
			return new Double(n1.doubleValue() / n2.doubleValue());
		}
	}
	
	
	/** 
	 * Generates a random number. 
	 * @param n determines how to generate the number. If the integer value
	 *  of the parameter is positive, a new integer in the area
	 *  0 <= x < n will be produced. Otherwise, the generated number will
	 *  be a double in the area 0.0 <= x < 1.0.
	 * @return the generated random number
	 */
	public Number random(Object param) {
		if (param instanceof Integer) {
			int spec = ((Number)param).intValue();
			return new Integer(spec <= 0 ? _r.nextInt() : _r.nextInt(spec));
		}
		else {
			double spec = ((Number)param).doubleValue();
			return new Double(spec <= 0.0 ? _r.nextDouble() : spec * _r.nextDouble());			
		}
	}
	

	/**
	 * Generates a new random number in the range [0.0, 1.0)
	 */
	public Double random() {
		return new Double(_r.nextDouble());
	}
	
	
	/**
	 * Determine which player is the strongest in the game.
	 */
	public Object strongestPlayer() {
		return new Integer(_game.findStrongestPlayer(-1));
	}
	
	
	/**
	 * Determine which player is the strongest opponent of the player.
	 */
	public Object strongestOpponent() {
		return new Integer(_game.findStrongestPlayer(_player));
	}
	
	
	/**
	 * Determine which player is the weakest in the game.
	 */
	public Object weakestPlayer() {
		return new Integer(_game.findWeakestPlayer(-1));
	}
	
	
	/**
	 * Determine which player is the weakest opponent of the player.
	 */
	public Object weakestOpponent() {
		return new Integer(_game.findWeakestPlayer(_player));
	}
	

	/**
	 * Determine the strength of a given player.
	 */
	public Object strengthOf(Object player) {
		return new Double(_game.strengthOf(((Integer)player).intValue()));
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
	
	// random number generator
	private Random _r;
	
	// the player number
	private int _player;
	
	// the game simulation
	private HaplomacyGame _game;
		
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