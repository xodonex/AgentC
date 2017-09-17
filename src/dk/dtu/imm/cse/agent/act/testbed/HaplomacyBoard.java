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
import java.util.*;
import javax.swing.*;

import dk.dtu.imm.cse.agent.act.util.*;


/**
 * A data structure to represent the Haplomacy playing board.
 *
 * @author  Henrik Lauritzen
 */
public class HaplomacyBoard implements HaplomacyConstants {

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
	 * Creates the default Haplomacy board.
	 */
	public HaplomacyBoard() {
		this(null);
	}
	
	
	/**
	 * Creates a new Haplomacy board from the given graph, whose vertices
	 *  must be {@link Province} instances.
	 */
	public HaplomacyBoard(NeighbourGraph graph) {
		if ((_graph = graph) == null) {
			_graph = createDefaultBoard();
		}
		
		// precalculate the distances
		_distances = _graph.calcDistances();
		
		// set the default colors
		_colors = new Color[] {
			new Color(192,192,192),
			Color.WHITE,
			Color.BLACK,
			new Color(240,240,240),
			new Color(255, 32, 32),
			new Color(16, 16, 192),
			new Color(48, 192, 48),
			Color.YELLOW,
			Color.cyan,
			Color.black,
			Color.white
		};		
		
		// create the display
		_display = new Display();		
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the provinces of the game
	 */
	public Collection getProvinces(Collection container) {
		return _graph.getVertices(container);
	}
	

	/**
	 * @return the province having the given id, or null if that province
	 *  does not exist.
	 */
	public Province getProvince(int id) {
		return (Province)_graph.getVertex(id);
	}
	
	
	/**
	 * @return the ID assigned to the given province. The ID is negative
	 *   iff the province does not exist in the graph.
	 */
	public int getProvinceId(Province p) {
		return _graph.indexOf(p);
	}
	
	
	/**
	 * @return the distance between two provinces, indexed by their 
	 *  respective {@link #getProvinceId(Province) ids}.
	 * @exception IndexOutOfBoundsException if either of the given province
	 *  ids does not exist.
	 */
	public int getDistance(int prov1, int prov2) 
			throws IndexOutOfBoundsException {
		return _distances[prov1][prov2];
	}
	

	/**
	 * @return the distance between two provinces. The method is merely
	 *  a convenience for {@link #getProvinceId(Province) 
	 * getDistance(getProvinceId(p1), getProvinceId(p2))}
	 */
	public int getDistance(Province p1, Province p2) {
		return getDistance(getProvinceId(p1), getProvinceId(p2));
	}
	
	
	/**
	 * @return true iff the given provinces are incident on each other
	 */
	public boolean hasBorder(Province p1, Province p2) {
		return _graph.contains(p1, p2);
	}
	
	
	/**
	 * @return a list of the provinces that are neighbours to the given
	 *  province
	 */
	public java.util.List getNeighbours(Province p) {
		return _graph.getNeighbours(p);
	}
	
	
	
	/**
	 * @return the current zoom level for visualization
	 */
	public double getZoomLevel() {
		return _zoomLevel;
	}
	
	
	/**
	 * Set the zoom level for visualization
	 * @param zoom the new zoom level. 
	 * @exception IllegalArgumentException if the zoom level is not 
	 *  a positive, finite number.
	 */
	public void setZoomLevel(double zoom) {
		if (zoom <= 0.0 || zoom != zoom || Double.isInfinite(zoom)) {
			throw new IllegalArgumentException("" + zoom);
		}
		if (_zoomLevel != zoom) {
			_zoomLevel = zoom;
			_display.updatePreferredSize();
			_display.validate();
			_display.repaint();
		}
	}
	
	
	/**
	 * @return the background color used to paint the board
	 */
	public Color getBackgroundColor() {
		return _colors[COLOR_BACKGROUND];
	}
	

	/**
	 * Set the background color used to paint the board
	 */
	public void setBackgroundColor(Color c) {
		_colors[COLOR_BACKGROUND] = c;
	}

	
	/**
	 * @return the background color used to paint the frame of the board
	 */
	public Color getFrameColor() {
		return _colors[COLOR_FRAME];
	}
	

	/**
	 * Set the background color used to paint the frame of the board
	 */
	public void setFrameColor(Color c) {
		_colors[COLOR_FRAME] = c;
	}


	/**
	 * @return the color used to paint borders
	 */
	public Color getBorderColor() {
		return _colors[COLOR_BORDER];
	}
	

	/**
	 * Set the background color used to paint the board
	 */
	public void setBorderColor(Color c) {
		_colors[COLOR_BORDER] = c;
	}

	
	/**
	 * @return the color used to paint neutral supply centres
	 */
	public Color getNeutralColor() {
		return _colors[COLOR_NEUTRAL];
	}
	

	/**
	 * Set the color used to paint neutral support centres
	 */
	public void setNeutralColor(Color c) {
		_colors[COLOR_NEUTRAL] = c;
	}
	
	
	/**
	 * @return the color used to paint a given player
	 */
	public Color getPlayerColor(int player) {
		if (player < PLAYER_MIN || player > PLAYER_NEUTRAL) {
			throw new IndexOutOfBoundsException();
		}
		return _colors[COLOR_PLAYER_OFFSET + player];
	}
	

	/**
	 * Set the color used to paint a given player
	 */
	public void setPlayerColor(int player, Color c) {
		if (player < PLAYER_MIN || player > PLAYER_NEUTRAL) {
			throw new IndexOutOfBoundsException();
		}
		_colors[COLOR_PLAYER_OFFSET + player] = c;
	}

	
	/**
	 * @return a component which displays the game
	 */
	public JComponent getDisplayComponent() {
		return _display;
	}
		
	
	/**
	 * Paints the game board
	 * @param g the graphics on which to paint
	 * @param game the game to which the board belongs
	 * @param dx the smallest x coordinate to be used
	 * @param dy the smallest y coordinate to be used
	 */
	public void paint(Graphics g) {
		_display.paint(g);
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
	
	/**
	 * Creates a new graph instance representing the default 
	 * 9x9 Haplomacy board with 4 different players.
	 */
	public static NeighbourGraph createDefaultBoard() {
		NeighbourGraph g = new NeighbourGraph();
		Province[] provs = new Province[81];
		
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				int id = 9 * row + col;
				int owner;
				boolean isSupport = true;
				
				switch (id) {
				case 20: case 22: case 24: case 38: case 40: case 42:
						case 56: case 58: case 60:
					owner = PLAYER_NEUTRAL;
					break;
				case 0: case 2: case 18:
					owner = 0;
					break;
				case 8: case 6: case 26:
					owner = 1;
					break;
				case 80: case 62: case 78:
					owner = 2;
					break;
				case 72: case 54: case 74:
					owner = 3;
					break;
				default:
					owner = PLAYER_NEUTRAL;
					isSupport = false;
				}
				
				g.addVertex(provs[9 * row + col] = 
						new DefaultProvince(row, col, owner, isSupport,
								(row == 0 ? DefaultProvince.LABEL_TOP : 0) |
								(row == 8 ? DefaultProvince.LABEL_BOTTOM : 0) |
								(col == 0 ? DefaultProvince.LABEL_LEFT : 0) |
								(col == 8 ? DefaultProvince.LABEL_RIGHT : 0)));
			}
		}
		
		// create the edges
		int[] delta = {-10, -9, -8, -1, 1, 8, 9, 10};
		for (int i = 0; i < provs.length; i++) {
			Province p = provs[i];
			for (int j = 0; j < delta.length; j++) {
				int idx = i + delta[j];
				if (idx < 0 || Math.abs((idx % 9) - (i % 9)) > 1) continue;
				if (idx >= provs.length) break;
				
				g.add(p, provs[idx]);
				g.add(provs[idx], p);
			}
		}

		return g;
	}

	// ------------------------------ protected ------------------------------
	
	// ***********************************************************************

	// =======================================================================
	// Private fields
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	
	// indices to _colors for the different colors used.
	private final static int 
			COLOR_BACKGROUND = 0,
			COLOR_FRAME = 1,
			COLOR_BORDER = 2,
			COLOR_NEUTRAL = 3,
			COLOR_PLAYER_OFFSET = 4;

	// ------------------------------ instance -----------------------------
	
	// the graph of provinces
	private NeighbourGraph _graph;
	
	// the precalculated distance matrix
	private int[][] _distances;

	
	// the zoom level for visualization
	private double _zoomLevel = 1.0;
	
	// the drawing colors 
	private Color[] _colors;

	// the display used to draw the board
	private Display _display; 
	
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------	

	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================

	// a component to display the game board
	private class Display extends JComponent implements Scrollable {
		
		public Display() {
			super();
			
			ArrayList ps = new ArrayList();
			_graph.getVertices(ps);
			Province[] provs = (Province[])ps.toArray(new Province[ps.size()]);
			
			// calculate the bounds of the game board
			_bounds = new Rectangle();
			Rectangle tmp = new Rectangle();
			
			for (int i = 0; i < provs.length; i++) {
				Province p = provs[i];
				p.getBounds(tmp);
				_bounds.add(tmp);
			}
			_bounds.width++;
			_bounds.height++;
			
			// calculate the preferred size
			updatePreferredSize();
		}
		
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}
		
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}
		
		public int getScrollableUnitIncrement(Rectangle rectangle, int orientation,
		int direction) {
			return 32;
		}
		
		public int getScrollableBlockIncrement(Rectangle rectangle, int orientation,
		int direction) {
			return 32;
		}
		
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}

		public void paint(Graphics g) {
			g.setColor(getFrameColor());
			g.fillRect(0, 0, (int)(_zoomLevel * (_bounds.width - _bounds.x)), 
					(int)(_zoomLevel * (_bounds.height - _bounds.y)));

			int dx = (int)(_zoomLevel * _bounds.x);
			int dy = (int)(_zoomLevel * _bounds.y);
			g.translate(-dx, -dy);
			
			// paint the provinces, and save the units for later
			Collection c = new ArrayList();			
			for (Iterator i = _graph.getVertices(null).iterator(); i.hasNext(); ) {
				Province p = (Province)i.next();
				p.paint(g, HaplomacyBoard.this);
				Unit u = p.getOccupant();
				if (u != null) {
					c.add(u);					
				}
			}
			
			// paint the units (two passes are necessary to layer correctly)
			for (Iterator i = c.iterator(); i.hasNext(); ) {
				((Unit)i.next()).paint(g, HaplomacyBoard.this);
			}
			
			g.translate(dx, dy);
		}
		
		
		// recalculate the preferred size based on the zoom level
		private void updatePreferredSize() {
			setPreferredSize(new Dimension(
					(int)(_zoomLevel * (_bounds.width - _bounds.x)),
					(int)(_zoomLevel * (_bounds.height - _bounds.y))));
		}
				
		// the bounds of the game board
		private Rectangle _bounds;
	}
	
}