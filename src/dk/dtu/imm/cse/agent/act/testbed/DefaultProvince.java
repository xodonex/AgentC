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
 * The default province paints itself as a square whose location
 *  is determined by grid coordinates.
 *
 * @author  Henrik Lauritzen
 */
public class DefaultProvince extends Province {

	// =======================================================================
	// Class fields
	// =======================================================================

	// ------------------------------- public --------------------------------
	
	/**
	 * Used to indicate that no label should be used
	 */
	public final static int LABEL_NONE = 0;

	
	/**
	 * Used to indicate that the label should be placed to the top
	 */	
	public final static int LABEL_TOP = 1;

	
	/**
	 * Used to indicate that the label should be placed to the bottom
	 */	
	public final static int LABEL_BOTTOM = 2;

	
	/**
	 * Used to indicate that the label should be placed to the left
	 */	
	public final static int LABEL_LEFT = 4;
	
	
	/**
	 * Used to indicate that the label should be placed to the right
	 */
	public final static int LABEL_RIGHT = 8;
	
	
	
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
	 * Creates a new province having the given characteristics. If the 
	 * province is a support centre, a unit will be created accordingly.
	 * @param row the row of the province in
	 * @param owner the player initially owning the province
	 * @param isSupport whether this province is a support centre.
	 * @param labelLocation a flag determining which of the 4 sides of 
	 *   the province should be given a label containing the coordinates.
	 * @see #LABEL_NONE
	 * @see #LABEL_TOP
	 * @see #LABEL_BOTTOM
	 * @see #LABEL_LEFT
	 * @see #LABEL_RIGHT	 
	 */
	public DefaultProvince(int row, int col, int owner, 
			boolean isSupport, int labelLocation) {
		super();
		
		if ((_row = row) < 0 || (_col = col) < 0) {
			throw new IllegalArgumentException();
		}
		if ((_isSupport = isSupport) && owner != PLAYER_NEUTRAL) {
			new Unit(owner, this);
		}
		_label = labelLocation;
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
	
	
	public Rectangle getBounds(Rectangle container) {
		container = calcBounds(container);		
		if ((_label & LABEL_TOP) == LABEL_TOP) {
			container.y -= (int)(1.33 * LABEL_SIZE);
			container.height += (int)(1.33 * LABEL_SIZE);
		}
		if ((_label & LABEL_BOTTOM) == LABEL_BOTTOM) {
			container.height += (int)(0.5 * LABEL_SIZE);
		}
		if ((_label & LABEL_LEFT) == LABEL_LEFT) {
			container.x -= (int)(1.75 * LABEL_SIZE);
			container.width += (int)(1.75 * LABEL_SIZE);
		}
		if ((_label & LABEL_RIGHT) == LABEL_RIGHT) {
			container.width += (int)(0.22 * LABEL_SIZE);
		}
		return container;
	}
	

	public Point getCoordinates(Point container) {
		if (container == null) {
			container = new Point();
		}
		container.x = _col * GRID_SIZE + GRID_SIZE / 2;
		container.y = _row * GRID_SIZE + GRID_SIZE / 2;
		return container;
	}
	

	public void paint(Graphics g, HaplomacyBoard board) {
		Rectangle c = calcBounds(null);
		double scale = board.getZoomLevel();
		
		int cx = (int)(c.x * scale),
			cy = (int)(c.y * scale),
			w = (int)(c.width * scale),
			h = (int)(c.height * scale);

		int borderWidth = (int)scale;
		if (borderWidth < 1) {
			borderWidth = 1;
		}
		
		g.setColor(board.getBackgroundColor());
		g.fillRect(cx, cy, w, h);
		
		g.setColor(board.getBorderColor());
		for (int i = 0; i < borderWidth; i++) {
			g.drawRect(cx + i, cy + i, w - 2 * i, h - 2 * i);
		}
		
		if (_isSupport) {
			int owner = getOwner();
			if (owner == PLAYER_NEUTRAL) {
				g.setColor(board.getNeutralColor());
			}
			else {
				g.setColor(board.getPlayerColor(owner));
			}
			g.fillRect(cx + borderWidth, cy + borderWidth, 
					w - 2 * borderWidth + 1, h - 2 * borderWidth + 1);
			g.setColor(board.getBackgroundColor());
			int t = (int)(scale * UNIT_SIZE / 4);
			g.fillRect(cx + t, cy + t, w - 2 * t, h - 2 * t);
		}

		Font font = new Font("monospace", Font.BOLD, (int)(scale * 12));
		g.setFont(font);
		g.setColor(board.getBorderColor());
		
		if ((_label & LABEL_TOP) == LABEL_TOP) {
			g.drawString("" + (char)('A' + _col),
					cx + w / 2, 
					(int)(cy - scale * 0.3 * LABEL_SIZE));
		}
		if ((_label & LABEL_BOTTOM) == LABEL_BOTTOM) {
			g.drawString("" + (char)('A' + _col),
					cx + w /2, 
					(int)(cy + h + scale * 0.9 * LABEL_SIZE));
		}
		if ((_label & LABEL_LEFT) == LABEL_LEFT) {
			g.drawString(Integer.toString(_row),
					(int)(cx - scale * 0.9 * LABEL_SIZE),
					cy + h / 2);
		}
		if ((_label & LABEL_RIGHT) == LABEL_RIGHT) {
			g.drawString(Integer.toString(_row),
					(int)(cx + w + scale * 0.35 * LABEL_SIZE), 
					cy + h / 2);
		}
	}

	
	public boolean isSupportCentre() {
		return _isSupport;
	}
	
	// ------------------------------ protected ------------------------------

	// =======================================================================
	// Overridden methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	public String toString() {
		return "" + (char)('A' + _col) + _row;
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
	
	// the size of the grid
	private final static int GRID_SIZE = 2 * UNIT_SIZE;
	
	// the size of the label
	private final static int LABEL_SIZE = UNIT_SIZE;
	
	// ------------------------------ instance -----------------------------

	// the location of the label, if any
	private int _label;
	
	// the coordinates of the province
	private int _row, _col;
		
	// whether the province is a support centre
	private boolean _isSupport;
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	private Rectangle calcBounds(Rectangle container) {			
		if (container == null) {
			container = new Rectangle();
		}		
		container.x = _col * GRID_SIZE;
		container.y = _row * GRID_SIZE;
		container.height = container.width = GRID_SIZE;
		return container;
	}


	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
