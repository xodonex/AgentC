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
 * Represents a single province in a Haplomacy game.
 *
 * @author  Henrik Lauritzen
 */
public abstract class Province implements HaplomacyConstants {
	
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
	// ------------------------------ protected ------------------------------
	
	protected Province() {		
	}
	
	// ------------------------------- private -------------------------------
	
	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * Return the location, in "natural" corrdinate space, of the centre
	 *  of the province.
	 * @param container the Point instance which will be modified to
	 *  hold the coordinates. If the given value is null, a new instance
	 *  will be created.
	 * @return the (possibly newly created) container
	 */
	public abstract Point getCoordinates(Point container);
	
	
	/**
	 * Return a bounding box for the extent of the province, in
	 *  "natural" coordinate space.
	 * @param container the Rectangle instance which will be modified to
	 *  hold the bounds. If the given value is null, a new instance
	 *  will be created.
	 * @return the (possibly newly created) container
	 */
	public abstract Rectangle getBounds(Rectangle container);
	
	
	/**
	 * @return true iff the province is a support centre.
	 */
	public abstract boolean isSupportCentre();
	
	
	/**
	 * Retreive the unit currently occupying the province.
	 * @return null iff the province is not occupied by any unit.
	 */
	public Unit getOccupant() {
		return _occupant;
	}
	
	
	/**
	 * Make the given unit occupy this province
	 * @param occupant the unit to occupy the province next. If the
	 *  value is null, the province will be unoccupied.
	 * @exception IllegalArgumentException if the given unit is unable
	 *  to occupy the province
	 */
	public void setOccupant(Unit occupant) {
		if (_occupant == occupant) {
			return;
		}
		else if (occupant == null) {
			if (_occupant != null) {
				_occupant.setLocation(null);
			}
		}
		else {
			if (_occupant != null || !allowsUnit(occupant)) {
				throw new IllegalArgumentException("" + occupant);
			}
			
			Province p = occupant.getLocation();
			if (p != this && p != null) {
				p.setOccupant(null);
			}
			occupant.setLocation(this);
		}
		_occupant = occupant;		
	}
	
	
	/**
	 * Determines whether the given unit is allowed to occupy this province.
	 * The default implementation always returns true.
	 * @return true iff it is possible for the unit to occupy this province.
	 */
	public boolean allowsUnit(Unit u) {
		return true;
	}
	
	
	/**
	 * Return an ID for the player which is currently owning the province.
	 * If the province is not a {@link #isSupportCentre() support centre},
	 * the province should indicate neutrality.
	 * @return {@link HaplomacyConstants#PLAYER_NEUTRAL} if the province
	 *  is not a support centre, or if no owner has been
	 * {@link #updateOwnership() assigned}.
	 */
	public int getOwner() {
		return _owner;
	}
	
	
	/**
	 * Updates ownership information for this province, according to the
	 *  following rule:<ul>
	 * <li>If the province is not a {@link #isSupportCentre() support centre},
	 * nothing happens.
	 * <li>If the province is unoccupied, nothing happens.
	 * <li>Otherwise, ownership is transferred to the player whose unit
	 *  is occupying the province.
	 * </ul>.
	 */
	public void updateOwnership() {
		if (_occupant != null && isSupportCentre()) {
			_owner = _occupant.getOwner();
		}
	}
	
	
	/**
	 * Paints the province on the given graphics.
	 * @param g the graphics object on which the province should be painted.
	 * @param board the board containing this province 
	 */
	public abstract void paint(Graphics g, HaplomacyBoard board);
		
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
	
	// the current owner of this province
	private int _owner = PLAYER_NEUTRAL;
	
	// the unit currently occupying the province
	private Unit _occupant;
	
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