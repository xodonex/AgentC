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
package dk.dtu.imm.cse.agent.act.testbed;

import java.awt.*;


/**
 * Represents a single unit in a Haplomacy game.
 *
 * @author  Henrik Lauritzen
 */
public class Unit implements HaplomacyConstants {

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
	 * Creates a new unit and places it in the given province.
	 * @param player the number of the player owning the unit.
	 * @param IllegalArgumentException if the player number is invalid,
	 *  or if the province cannot be occupied by the unit.
	 */
	public Unit(int player, Province prov) {
		if ((_owner = player) < PLAYER_MIN || player > PLAYER_MAX) {
			throw new IllegalArgumentException("" + player);
		}
		(_location = prov).setOccupant(this);
		prov.updateOwnership();
	}
			
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
		
	/**
	 * @return an ID for the player owning the unit.
	 */
	public int getOwner() {
		return _owner;
	}
	
	
	/**
	 * @return the current location of the unit
	 */
	public Province getLocation() {
		return _location;
	}
		
		
	/**
	 * Update the location of the unit. This is done automatically from
	 * {@link Province#setOccupant(Unit)}.
	 */
	public void setLocation(Province location) {
		_location = location;
	}
		

	/**
	 * @return true iff the unit has no orders, i.e., is holding its
	 *  position.
	 */
	public boolean isHolding() {
		return _support == null && _destination == null;
	}
	
	
	/**
	 * Order the unit to hold its position, canceling any previous orders.
	 */
	public void hold() {
		_support = null;
		_destination = null;
	}
	
	
	/**
	 * @return true iff the unit has been ordered to support
	 */
	public boolean isSupporting() {
		return _support != null;
	}
		

	/**
	 * @return the unit currently being supported, if any.
	 */
	public Unit getSupportedUnit() {
		return _support;
	}
	
	
	/**
	 * Order the unit to give support to another unit, replacing any previous
	 * orders. No check for validity of the order is performed at this point.
	 */
	public void support(Unit u) {
		if (u == null) {
			throw new NullPointerException();
		}
		if (u == this || u.getSupportedUnit() == this) {
			throw new IllegalArgumentException();
		}
		_destination = null;
		_support = u;
	}
	
	
	/**
	 * @return true iff the unit has been ordered to move
	 */
	public boolean isMoving() {
		return _destination != null;
	}

	
	/**
	 * @return the destination of the unit's movement orders, if any.
	 */
	public Province getMoveDestination() {
		return _destination;
	}
	
	
	/**
	 * Order the unit to move to another province, replacing any previous
	 * orders. No check for validity of the order is performed at this point.
	 */
	public void moveTo(Province p) {
		if (p == null) {
			throw new NullPointerException();
		}
		if (p == _location) {
			throw new IllegalArgumentException();
		}
			
		_support = null;
		_destination = p;
	}

	
	/**
	 * @return the province which the unit's current orders affects
	 */
	public Province getInfluenceArea() {
		if (_destination != null) {
			return _destination;
		}
		else if (_support != null) {
			return _support.getMoveDestination();
		}
		else {
			return null;
		}
	}
	
	
	/**
	 * Paints the unit on the given graphics.
	 * @param g the graphics object on which the unit should be painted.
	 * @param board the board to which the unit (and its location) belongs
	 */
	public void paint(Graphics g, HaplomacyBoard board) {
		if (_location == null) {
			return;
		}
		Point c = _location.getCoordinates(null);
		double scale = board.getZoomLevel();
		
		double radius = UNIT_SIZE * scale / 2;
		
		// calculate the center coordinates
		double cx = scale * c.x;
		double cy = scale * c.y;
		
		// paint the unit
		g.setColor(board.getPlayerColor(_owner));
		g.fillOval((int)(cx - radius), (int)(cy - radius), 
				(int)(2 * radius) + 1, (int)(2 * radius) + 1);
		g.setColor(board.getBorderColor());
		g.drawOval((int)(cx - radius), (int)(cy - radius), 
				(int)(2 * radius), (int)(2 * radius));
		
	
		// determine the point towards which the order is directed
		Point d;
		if (_destination != null) {
			d = _destination.getCoordinates(null);
		}
		else if (_support != null) {
			d = _support.getLocation().getCoordinates(null);
		}
		else return; // no orders

		// determine the angle towards which the orders are directed
		double v = Math.atan2(d.y - c.y, d.x - c.x);

		// calculate the size of the arrow
		double dev = 2.0 * Math.PI / 16.0;
		double baseDist = 1.4 * radius;
		double topDist = Math.sqrt(0.5) * baseDist *
				(Math.sin(dev) - Math.sin(-dev)) + baseDist;
		
		// make the arrow narrow when supporting
		if (_support != null) {
			dev /= 2.25;
		}
		
		// calcualate the points of the arrow
		int[] xpoints = {
			(int)(cx + baseDist * Math.cos(v - dev)),
			(int)(cx + topDist * Math.cos(v)),
			(int)(cx + baseDist * Math.cos(v + dev))
		};
		int[] ypoints = {
			(int)(cy + baseDist * Math.sin(v - dev)),
			(int)(cy + topDist * Math.sin(v)),
			(int)(cy + baseDist * Math.sin(v + dev))
		};
		
		// draw the arrow		
		if (_destination != null) {
			g.setColor(board.getPlayerColor(_owner));
			g.fillPolygon(xpoints, ypoints, 3);
		}
		else {
			g.setColor(board.getPlayerColor(_support.getOwner()));
			g.fillPolygon(xpoints, ypoints, 3);
		}
		g.setColor(board.getBorderColor());
		g.drawPolygon(xpoints, ypoints, 3);
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
		if (_location == null) {
			return "Unit@" + 
					Integer.toHexString(System.identityHashCode(this));
		}
		return _location.toString() +
				(_destination != null ? "-" + _destination :
					_support != null ? " S " + _support : "-holds");
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
	
	// the ID of the player owning this unit
	private int _owner;
	
	// the province in which the unit is currently located
	private Province _location;
		
	// the province to which the unit is ordered to move.
	// INVARIANT: _destination == null || _support == null
	private Province _destination;
	
	// the unit which this unit has been ordered to support
	// INVARIANT: _support == null || _destination == null
	private Unit _support;
		
	
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
