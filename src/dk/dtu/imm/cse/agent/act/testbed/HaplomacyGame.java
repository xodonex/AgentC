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

import java.util.*;

import dk.dtu.imm.cse.agent.act.util.*;


/**
 * The HaplomacyGame is responsible for the dynamics of the game. It provides
 * facilities for resolution and execution of orders, in addition to some
 * <strong>very</strong> simple strategies for giving orders.
 *
 * <strong>Note:</strong> neither the HaplomacyGame nor the objects from which 
 *  it is comprised can be used by multiple threads.
 *
 * @author  Henrik Lauritzen
 */
public class HaplomacyGame implements HaplomacyConstants {

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
	 * Creates a new Haplomacy game using the default game board.
	 */
	public HaplomacyGame() {
		this(null);
	}
	
	
	/**
	 * Crates a new Haplomacy game using the given game board.
	 */
	public HaplomacyGame(HaplomacyBoard board) {
		if ((_board = board) == null) {
			_board = new HaplomacyBoard();
		}
				
		// extract the support centres, units and player IDs from the
		// board.
		_supportCentres = new HashSet();
		_units = new LinkedHashSet();
		_players = new HashSet();
		_playerUnits = new Set[MAX_PLAYERS];
		_homeCountries = new List[MAX_PLAYERS];
		for (int i = 0; i < _playerUnits.length; i++) {
			_playerUnits[i] = new HashSet();
			_homeCountries[i] = new ArrayList();
		}
		
		Collection c = _board.getProvinces(null);
		Province[] provs = (Province[])c.toArray(new Province[c.size()]);
		
		for (int i = 0; i < provs.length; i++) {
			Province p = provs[i];
			if (p.isSupportCentre()) {
				_supportCentres.add(p);
			}
			
			Unit u = p.getOccupant();
			if (u != null) {				
				_units.add(u);
				int owner = u.getOwner();
				_playerUnits[owner].add(u);
				_homeCountries[owner].add(p);
				_players.add(new Integer(owner));
			}
		}
		_playerCount = _players.size();		
	}
	
	// ------------------------------ protected ------------------------------
	// ------------------------------- private -------------------------------

	// ***********************************************************************
	
	// =======================================================================
	// New instance methods
	// =======================================================================
	
	// ------------------------------- public --------------------------------
	
	/**
	 * @return the current year of the game (the value is initially 0)
	 */
	public int getYear() {
		return _year;
	}
	
	
	/**
	 * Determine the current season of the game (spring/fall).
	 * @return true iff the current season is the fall season.
	 */
	public boolean isFall() {
		return _fall;
	}
	
	
	/**
	 * @return the board used for the game
	 */
	public HaplomacyBoard getBoard() {
		return _board;
	}

	
	/**
	 * @return the number of players in the game
	 */
	public int getPlayerCount() {
		return _playerCount;
	}
	
	
	/**
	 * Retreive the units for a given player.
	 * @param player the player whose units should be retreived
	 * @param c the collection to which the players will be added; if the
	 *  value is null, a new collection will be created.
	 * @return c (or the newly created collection)
	 * @exception ArrayIndexOutOfBoundsException if the player ID is out of 
	 *  bounds
	 */
	public Collection getUnits(int player, Collection c) 
			throws IndexOutOfBoundsException {
		if (c == null) {
			return new ArrayList(_playerUnits[player]);
		}
		else {
			c.addAll(_playerUnits[player]);
			return c;
		}
	}


	/**
	 * Count the units for a given player.
	 * @param player the player whose units should be counted
	 * @return the number of units owned by the given player
	 * @exception ArrayIndexOutOfBoundsException if the player ID is out of 
	 *  bounds
	 */
	public int countUnits(int player) throws IndexOutOfBoundsException {
		return _playerUnits[player].size();
	}

	
	/**
	 * Count support centres owned by a given player.
	 * @param player the player for which the number of owned support
	 *  centres should be determined
	 * @return the number of support centres owned by the given player
	 */
	public int countSupportCentres(int player) {
		int result = 0;
		for (Iterator i = _supportCentres.iterator(); i.hasNext(); ) {
			if (((Province)i.next()).getOwner() == player) {
				result++;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Equivalent to {@link #update(boolean) update(true))}
	 */
	public Set[][] update() {
		return update(true);
	}
	
	
	/**
	 * Adjudicate the given orders and update the game state accordingly.
	 * The method executes three different phases:
	 * <ol><li value="1">Adjudicate existing orders and update the unit positions
	 * <li value="2">Handle dispands/retreats
	 * <li value="3">In the fall, adjust army sizes
	 * </ol>
	 * @param advanceTime whether the season should be updated.
	 * @return an array whose elements contain information for each player,
	 * or null if the game is over.
	 * The information for each player is itself a Map[] array instance, 
	 * containing the following information:
	 * In case the player was eliminated, the length of the array will be 0.
	 * Otherwise, the following information is found in the array:
	 * <li value="0">A map which for each player lists the players
	 *  who were attacking some of that player's units.
	 * <li value="1">A map which for each player lists the players
	 *   who were attacking some of that player's support centres.
	 * <li value="2">A map which for each player lists the players
	 *  who gained control of some of that player's support centres.
	 * </ol>
	 */
	public Set[][] update(boolean advanceTime) {
		// extract all units executing valid move or hold orders.
		Set m = new HashSet();
		Set s = new HashSet();		
		validateOrders(m, s);
		
		List tl = new ArrayList(m);
		tl.addAll(s);
		
		if (DEBUG) {
			System.out.println("orders: " + tl);				
		}
		
		// determine which provinces and units are under attack. At the
		// same time, determine which which units are performing the attacks
		Unit[] moves = (Unit[])m.toArray(new Unit[m.size()]);
		Map attackedProvs = new HashMap();
		Map attackedUnits = new HashMap();
		findAttacks(moves, attackedProvs, attackedUnits);
				
		// generate the results
		Set[][] result = new Set[MAX_PLAYERS][];
		for (int i = 0; i < result.length; i++) {
			if (_playerUnits[i] == null || _playerUnits[i].size() == 0) {
				result[i] = ELIMINATED;
			}
			else {
				result[i] = new Set[] { 
					new HashSet(8), new HashSet(4), new HashSet(2)
				};
			}
		}
		for (Iterator i = attackedProvs.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			Province p = (Province)e.getKey();
			if (p.isSupportCentre()) {
				int pOwner = p.getOwner();
				if (pOwner != PLAYER_NEUTRAL) {
					addResult(result, pOwner, (List)e.getValue(), 1);
				}
			}
		}
		for (Iterator i = attackedUnits.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			Unit u = (Unit)e.getKey();
			List units = (List)e.getValue();
			addResult(result, u.getOwner(), units, 0);
		}
		
		// generate a map from each attacking unit to its supports.
		Map supports = findSupports(s);

		if (DEBUG) {
			System.out.println("attackedProvs: " + attackedProvs);
			System.out.println("attackedUnits: " + attackedUnits);
			System.out.println("supports: " + supports);
		}

		// Cut support.
		for (Iterator i = s.iterator(); i.hasNext(); ) {
			Unit u = (Unit)i.next();
			List ats = (List)attackedUnits.get(u);
			if (ats == null) {
				continue;
			}
			
			int suppOwner = u.getOwner();
			Unit us = u.getSupportedUnit();
			Province suppDest = us.getMoveDestination();
			for (int j = 0, max = ats.size(); j < max; j++) {
				Unit a = (Unit)ats.get(j);
				/* 13. Support is cut if the unit giving support is attacked 
				 * from any province except the one where support is being given.
				 * 16. An attack by a country on one of its own units does not 
				 * cut support.
				 */
				if (a.getOwner() != suppOwner && a.getLocation() != suppDest) {
					// uppdate the supports map					
					if (DEBUG) {
						System.out.println("Support is cut for " + us);					
					}
					List l = (List)supports.get(us);
					if (l != null) {
						if (l.size() == 1) {
							supports.remove(us);
						}
						else {
							l.remove(u);
						}
					}
					if (DEBUG) {
						System.out.println(u + " : support cut by " + a);
					}
					// clear the units's orders
					u.hold();					
				}
			}			
		}
		
		// Repeat resolving moves by
		//  1. First moving all units whose destination is empty and not
		//   itself the destination of other units.
		// 2. For for the province attacked by the strongest combined army,
		//   determine the result and execute the dislodgement or standoff.
		// During this phase a map of dislodged unit to their possible retreats
		//   as well as a set of the provinces in which a standoff took place
		//  are built.
		Map retreats = new HashMap();
		Set standoffs = new HashSet();
		Province[] targetContainer = new Province[1];
		
		while (attackedProvs.size() > 0) {
			// move units that are the sole attackers on an empty province.
			if (moveUndisputed(attackedProvs, supports)) continue;
			
			// find the province under most heavy attack
			List[] attacks = findLargestDispute(targetContainer, 
					attackedProvs, supports);
			Province target = targetContainer[0];

			// remove the province from the list of provinces to be considered
			attackedProvs.remove(target);

			// extract the relevant data
			List l = attacks[attacks.length - 1];
			Unit largestAttack = (Unit)l.get(0);
			Province source = largestAttack.getLocation();
			Unit occupant = target.getOccupant();
			
			// determine the attack size necessary to win
			int sizeToWin;
			if (occupant == null) {
				sizeToWin = 1;
			}
			else if (occupant.isHolding() || 
					occupant.getMoveDestination() == target) {
				List ss = (List)supports.get(occupant);				
				sizeToWin = 2 + (ss == null ? 0 : ss.size());
			}
			else {
				sizeToWin = 2;
			}
			
			if (DEBUG) {
				System.out.print("next to be resolved: " + 
						Arrays.asList(attacks) + ". Size to win: " + sizeToWin);
			}

			boolean dislodge = false, standoff = false;
			
			// 1. All units have the same strength
			// 3. Equal strength units trying to occupy the same province
			//   cause all of those units to remain in their origina provinces.
			if (attacks.length >= sizeToWin && l.size() == 1 &&
					largestAttack.isMoving()) {
				//  12. A country cannot dislodge or support the dislodgement
				//    of one of its own units, even if that support is
				//    unexpected.
				boolean selfDislodgement = false;
				if (occupant != null) {
					int ocOwner = occupant.getOwner();
					if (ocOwner == largestAttack.getOwner()) {
						selfDislodgement = true;
					}
					else {
						List sups = (List)supports.get(largestAttack);
						if (sups != null) {
							for (Iterator it = sups.iterator(); it.hasNext(); ) {
								Unit su = (Unit)it.next();
								if (su.getOwner() == ocOwner) {
									selfDislodgement = true;
									break;
								}
							}
						}
					}
				} // occupant != null
				
				if (!selfDislodgement) {
					// the unit should be dislodged
					if (DEBUG) {
						System.out.println(": " + largestAttack + 
							" wins. Dislodging " + occupant + " from " + source);
					}
					if (occupant != null) {
						// Check that:
						//  10. A dislodged unit can still cause a standoff in 
						// a province different from the one that dislodged it.
						//  11. A dislodged unit, even with support, has no 
						//  effect on the province that dislodged it.
						if (occupant.getInfluenceArea() == source) {
							if (occupant.isMoving()) {
								if (DEBUG) {
									System.out.println(occupant + 
										"' orders fail because of dislodgement");								
								}
								// remove the attack
								List al = (List)attackedProvs.get(source);
								if (al != null) {
									al.remove(occupant);
									if (al.size() == 0) {
										attackedProvs.remove(source);
									}
								}
							}
							// stop support to the dislodged unit
							holdAndUpdateSupport(supports, occupant);
						}
						else if (occupant.isHolding()) {
							// update the supports if the unit was holding
							// (if it was attacking, the order will be resolved
							// at a later time.
							holdAndUpdateSupport(supports, occupant);
						}

						// the winner dislodges any unit present in the province.
						// dislodge the unit and record which retreats are
						// possible
						Collection neighbours = _board.getNeighbours(target);
						neighbours.remove(source);
						retreats.put(occupant, neighbours);						
						dislodge = true;
						target.setOccupant(occupant = null);
					}					

					// move the winner into place
					target.setOccupant(largestAttack);
				}
				else {
					if (DEBUG) {
						System.out.println(": standoff due to self dislodgement");
					}
					standoff = true;
				}
			}
			else {
				if (l.size() > 1) {
					standoff = true;
					standoffs.add(target);
				}
				if (DEBUG) {
					System.out.println(standoff ? ": simple standoff" :
							": cannot move");
				}
			}
			
			if (occupant != null && (occupant.isHolding() ||
					occupant.getMoveDestination() == source)) {
				// the occupant is causing the standoff - clear its orders
				if (DEBUG) {
					System.out.println("occupant (" + occupant + 
							") fails because of standoff");				
				}
				holdAndUpdateSupport(supports, occupant);
			}
			// all other units' orders are treated as a standoff
			for (int i = 0; i < attacks.length; i++) {
				for (Iterator j = attacks[i].iterator(); j.hasNext(); ) {
					holdAndUpdateSupport(supports, (Unit)j.next());
				}
			}
			
		}

		// handle the retreats and disbanding
		retreatOrDisband(retreats, standoffs);
		
		// update the current season and year
		boolean finished = false; 
		if (_fall) {
			finished = xeqFallAdjustments(result);
			if (advanceTime) {
				_fall = false;
				_year++;
			}
		}
		else {			
			_fall = advanceTime;
		}
		
		_board.getDisplayComponent().repaint();
		return finished ? null : result;
	}
	
	
	/**
	 * Find the province which minimises/maximises the sum of distances
	 *   to a given set of provinces.
	 * @param options the provinces whose distances should be considered
	 * @param relativeTo the provinces to which the distance should be evaluated.
	 * @param closest whether the minimal or the maximal distance should be
	 *  considered.
	 * @return the province which best matches the given criteria.
	 */
	public Province findProvince(Collection options, Collection relativeTo, 
			boolean minimise) {
		// used for the sum of distances
		int comp = minimise ? Integer.MAX_VALUE : -1;
		
		// used for the minimal/maximal distance
		int comp2 = comp;
		
		// the result to be returned
		Province result = null;
				
		// randomise the order of the provinces in order to avoid
		// cyclic situations
		Object[] opts = options.toArray();
		Random rnd = new Random();
		for (int i = 0; i <opts.length; i++) {
			int i1 = rnd.nextInt(opts.length);
			int i2 = rnd.nextInt(opts.length);
			if (i1 != i2) {
				Object tmp = opts[i2];
				opts[i2] = opts[i1];
				opts[i1] = tmp;
			}
		}
		
		for (int i = 0; i < opts.length; i++) {
			Object n = opts[i];			
			Province target = n instanceof Unit ? ((Unit)n).getLocation() :
					(Province)n;
			int targetId = _board.getProvinceId(target);
			int sumDist = 0, minDist = Integer.MAX_VALUE, maxDist = 0;
			
			for (Iterator rt = relativeTo.iterator(); rt.hasNext(); ) {
				Object r = rt.next();			
				Province prov = r instanceof Unit ? ((Unit)r).getLocation() :
					(Province)r;
				int d = _board.getDistance(targetId, 
					_board.getProvinceId(prov));
				sumDist += d;
				if (d < minDist) {
					minDist = d;
				}
				if (d > maxDist) {
					maxDist = d;
				}
			} // for
			
			if (minimise) {
				if (sumDist < comp || (sumDist == comp && minDist < comp2)) {
					comp = sumDist;
					comp2 = minDist;
					result = target;
				}
			}
			else {
				if (sumDist > comp || (sumDist == comp && maxDist > comp2)) {
					comp = sumDist;
					comp2 = maxDist;
					result = target;
				}
			}
		}
		
		return result;		
	}

	
	/**
	 * Give defensive orders for a certain player. Defensive orders
	 *  mean the following:
	 * <ul><li>All units occupying a support centre are ordered to hold
	 * <li>For every empty owned support centre, exactly one unit will be
	 * ordered to move towards it.
	 * </ul>
	 * @param player the player whose orders should be given
	 * @param friendly a set of the players who should not be attacked
	 *  as a result of the orders.
	 * @param neutral as set of the provinces which cannot be occupied by
	 *  the player.
	 */
	public void giveDefensiveOrders(int player, Collection friendly, 
			Collection neutral) {
		Set units = new HashSet(_playerUnits[player]);
		if (units.size() == 0) {
			return;
		}
		
		// make a copy of the collection, since it may be modified 
		neutral = new HashSet(neutral);
		
		List forcedMoves = new ArrayList();
		List centres = new ArrayList();
		
		for (Iterator i = _supportCentres.iterator(); i.hasNext(); ) {
			Province p = (Province)i.next();
			Unit occ = p.getOccupant();
			if (p.getOwner() != player) {
				if (occ != null && occ.getOwner() == player) {
					// ensure that a unit occupying a friendly 
					// support centre moves out of it
					forcedMoves.add(occ);
				}
				continue;
			}
			else if (neutral.contains(p)) {
				// don't consider other players' support centres, or support
				// centres which cannot be occupied
				continue;
			}			
			
			if (occ != null) {
				if (occ.getOwner() == player) {
					// let the unit occupy the support centre
					units.remove(occ);
					occ.hold();
					if (_homeCountries[player].contains(p)) {
						// try to defend the province if it is
						// a home country
						centres.add(p);
					}
				}
				else if (!friendly.contains(PLAYER_IDs[occ.getOwner()]) &&
						_homeCountries[player].contains(p)) {
					// try to attack the province if it is a home country
					// and is occupied by an enemy
					centres.add(p);
				}
			}
			else {
				// the support centre is empty - try to occupy it
				centres.add(p);
			}
		}
		
		// give orders for each available unit
		List tmp = new ArrayList();
		tmp.add(null);		
		for (Iterator i = centres.iterator(); i.hasNext(); ) {
			if (units.size() == 0) {
				break;
			}
			
			// find the unit closest to the support centre
			Province centre = (Province)i.next();
			tmp.set(0, centre);
			Province nextProv = findProvince(units, tmp, true);

			Unit unit = nextProv.getOccupant();
			units.remove(unit);
			
			if (_board.hasBorder(nextProv, centre) && 
					centre.getOccupant() != null &&
					centre.getOccupant().getOwner() == player) {
				// let the unit support the defender holding the home
				// support centre
				unit.support(centre.getOccupant());
			}	
			else {
				Province target = findRoute(nextProv, centre, friendly, neutral);
				if (target != null) {
					// move the unit to the specified province, if possible
					unit.hold();
					unit.moveTo(target);
				
					// don't order other units to enter the province which
					// the unit is entering
					neutral.add(target);
				}
			}
		}
		
		// ensure that all forced moves are carried out
		for (Iterator i = forcedMoves.iterator(); i.hasNext(); ) {
			Unit u = (Unit)i.next();
			if (u.isMoving()) continue;
			
			Province target = findRoute(u.getLocation(), 
					(Province)_homeCountries[player].iterator().next(), 
					friendly, neutral);
			if (target != null) {
				u.hold();
				u.moveTo(target);
				neutral.add(target);
			}			
		}
	}
	
	
	/**
	 * Give offensive orders for a certain player. Offensive orders
	 *  mean the following:
	 * <ul><li>The nearest support centre not owned by the player, and which
	 *  is not owned by a friendly player or otherwise is illegal to enter,
	 *  is selected to be attacked.
	 * <li>A number of units, corresponding to the given percentage of the
	 *  player's units, will be selected to attack the province. 
	 * <li>If more than one unit are adjacent to the province under attack,
	 *  one will be selected to attack and the other units will support it.
	 * </ul>
	 * @param player the player whose orders should be given
	 * @param size the fraction of the player's units which should be used
	 * in the attack.
	 * @param friendly a set of the players who should not be attacked
	 *  as a result of the orders.
	 * @param neutral as set of the provinces which cannot be occupied by
	 *  the player.
	 * @return true iff it was possible to find a support centre to attack 
	 *  under the given constraints, and there was units available to attack it.
	 */
	public boolean giveOffensiveOrders(int player, double offenseSize, 
			Collection friendly, Collection neutral) {
		Set units = new HashSet(_playerUnits[player]);
		if (offenseSize > 1.0) {
			offenseSize = 1.0;
		}
		else if (offenseSize < 0.0 || offenseSize != offenseSize) {
			offenseSize = 0.0;
		}
		
		int attackSize = (int)Math.round(offenseSize * units.size());
		if (attackSize == 0) {
			// not enough units to execute the attack
			return false;
		}

		// make a copy of the neutral positions, since the set may be modified
		neutral = new HashSet(neutral);
		
		// determine support centres eligible for an attack
		Set eligibleCentres = new HashSet(_supportCentres);
		List forcedMoves = new ArrayList();
		
		for (Iterator i = eligibleCentres.iterator(); i.hasNext(); ) {
			Province p = (Province)i.next();
			int owner = p.getOwner();
			Unit occ = p.getOccupant();
			
			if (owner == player || neutral.contains(p) || 
					friendly.contains(PLAYER_IDs[owner])) {
				// don't use this province
				i.remove();
				if (friendly.contains(PLAYER_IDs[owner]) && occ != null &&
						occ.getOwner() == player) {
					// if the player is occupying a friendly support centre,
					// then make the unit move
					forcedMoves.add(occ);
				}
				continue;
			}
			
			if (occ != null) {
				if (friendly.contains(PLAYER_IDs[occ.getOwner()])) {
					// don't use this support centre
					i.remove();
					continue;
				}
				else if (occ.getOwner() == player) {
					// the player should try to hold this province for the 
					// next turn.
					occ.hold();
					units.remove(occ);
					attackSize--;
					eligibleCentres.clear();
					eligibleCentres.add(p);
					break;
				}
			}
		}
		
		// target the eligible support centre which minimizes the sum of 
		// distances to the player's units, preferring a home country
		// if one is eligible for an attack.		
		Set test = new HashSet(_homeCountries[player]);
		test.retainAll(eligibleCentres);		
		Province target = findProvince(test.size() > 0 ? test : eligibleCentres, 
				_playerUnits[player], true);
		
		// categorize the player's units based on their distance to the target
		// but with a certain amout of randomization
		Set[] distSets = new Set[(int)(System.currentTimeMillis() % 3) + 1];
		for (int i = 0; i < distSets.length; i++) {
			distSets[i] = new HashSet();
		}

		// sort the units by their distance, but ensure that some
		// randomization is left in the ordering
		for (Iterator i = units.iterator(); i.hasNext(); ) {
			Unit u = (Unit)i.next();
			int d = _board.getDistance(u.getLocation(), target);
			if (d < distSets.length) {
				distSets[d].add(u);
			}
			else {
				distSets[distSets.length - 1].add(u);
			}
		}
		
		List sorted = new ArrayList(units.size());
		for (int i = 0; i < distSets.length; i++) {
			sorted.addAll(distSets[i]);
		}

		// give the orders
		Unit attacker = null;
		for (int i = 0; i < attackSize && i < sorted.size(); i++) {
			Unit u = (Unit)sorted.get(i);
			
			// select an eligible route to the target
			Province dest = findRoute(u.getLocation(), target, 
					friendly, neutral);
			if (dest == null) {
				// no route : leave the unit and select another
				attackSize++;
				continue;
			}
			else if (dest == target) {
				if (attacker == null) {
					// let the unit attack the province
					attacker = u;
					if (u.getLocation() != target) {
						u.moveTo(dest);
					}
					else {
						u.hold();
					}
				}
				else {
					// support the attacker
					u.support(attacker);
				}
			}
			else {
				// move nearer to the province
				u.moveTo(dest);
				// don't order other units into that province
				neutral.add(dest);
			}
		}

		// ensure that all forced moves are carried out
		for (Iterator i = forcedMoves.iterator(); i.hasNext(); ) {
			Unit u = (Unit)i.next();
			if (u.isMoving()) continue;
			
			target = findRoute(u.getLocation(), 
					(Province)_homeCountries[player].iterator().next(), 
					friendly, neutral);
			if (target != null) {
				u.moveTo(target);
				neutral.add(target);
			}			
		}
		
		return true;
	}
	
	
	/**
	 * Determine which player is the strongest.
	 * @param relativeTo the ID of the player which should be omitted from
	 *  the comparison. If the value is out of range, the strongest of
	 *  all players will be returned.
	 * @return the player having the maximal {@link #strengthOf(int) strength}.
	 *  In case of a tie a random choice will be made.
	 */
	public int findStrongestPlayer(int relativeTo) {
		List strongest = new ArrayList();
		double maxStrength = 0.0;
		
		for (int i = 0; i < _playerUnits.length; i++) {
			if (i == relativeTo) {
				continue;
			}
			double tmp = strengthOf(i);
			if (tmp >= maxStrength) {
				if (tmp > maxStrength) {
					strongest.clear();
				}
				strongest.add(PLAYER_IDs[i]);
				maxStrength = tmp;
			}
		}

		// make a random choice
		return (strongest.size() == 0) ? PLAYER_NEUTRAL :
				((Integer)strongest.get(new Random().nextInt(
						strongest.size()))).intValue();
	}
	
	
	/**
	 * Determine which player is the weakest.
	 * @param relativeTo the ID of the player which should be omitted from
	 *  the comparison. If the value is out of range, the strongest of
	 *  all players will be returned.
	 * @return the player having the minimal {@link #strengthOf(int) strength}.
	 *  In case of a tie an arbitrary player will be returned.
	 */
	public int findWeakestPlayer(int relativeTo) {
		List weakest = new ArrayList();
		double minStrength = Double.POSITIVE_INFINITY;
		
		for (int i = 0; i < _playerUnits.length; i++) {
			if (i == relativeTo) {
				continue;
			}
			double tmp = strengthOf(i);
			if (tmp > 0.0 && tmp <= minStrength) {
				if (tmp < minStrength) {
					weakest.clear();
				}
				weakest.add(PLAYER_IDs[i]);
				minStrength = tmp;
			}
		}

		return (weakest.size() == 0) ? PLAYER_NEUTRAL :
				((Integer)weakest.get(new Random().nextInt(
						weakest.size()))).intValue();
	}
	
	
	/**
	 * Determine the strength of a given player. 
	 * @return the number of units the player owns, divided by
	 *  the total number of units in the game.
	 */
	public double strengthOf(int player) {
		return (double)_playerUnits[player].size() / _units.size();
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
		return "Haplomacy - " + (_fall ? "Fall " : "Spring ") + _year;
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
	
	// save an Integer instance for each of the players
	private final static Integer[] PLAYER_IDs = new Integer[PLAYER_NEUTRAL + 1];
	static {
		for (int i = 0; i < PLAYER_IDs.length; i++) {
			PLAYER_IDs[i] = new Integer(i);			
		}
	}
	
	// use a shared Map[] instance to represent an eliminated player
	private final static Set[] ELIMINATED = {};
	
	// whether debug information should be printed
	private final static boolean DEBUG = false;
	
	// ------------------------------ instance -----------------------------

	// the game board
	private HaplomacyBoard _board;
	
	// the number of players
	private int _playerCount;
	
	// the current year
	private int _year = 0;
	
	// the season
	private boolean _fall = false;
	

	// contains all the support centres of the board
	private Set _supportCentres;
	
	// contains the home country support centres of each player
	private List[] _homeCountries;
	
	// contains the units currently on the board
	private Set _units;

	// contains the units indexed by their owner
	private Set[] _playerUnits;
	
	// contains the player IDs which are used in the game
	private Set _players;
	
	
	// whether debugging messages should be logged
	private boolean _logged = false;
	
	// =======================================================================
	// Private methods
	// =======================================================================
		
	// ------------------------------- class -------------------------------
	// ------------------------------ instance -----------------------------

	// check all units' orders for validity, and classify the units not holding
	// into the moves and supports
	private void validateOrders(Set moves, Set supports) {
		for (Iterator i = _units.iterator(); i.hasNext(); ) {
			Unit u = (Unit)i.next();
			Province prov;
			
			if (u.isSupporting()) {
				Unit su = u.getSupportedUnit();
				if (su.isSupporting()) {
					// cannot support a support
					u.hold();
				}
				else {
					prov = su.isMoving() ? su.getMoveDestination() : 
							su.getLocation();
					if (_board.hasBorder(u.getLocation(), prov) &&
							prov.allowsUnit(u)) { 
						// NOTE: it is assumed that no province is adjacent
						// to itself!
						supports.add(u);
					}
					else {
						// invalid order
						u.hold();
					}				
				}
			}
			else if (u.isMoving()) {
				prov = u.getMoveDestination();
				if (!_board.hasBorder(u.getLocation(), prov) 
						|| !prov.allowsUnit(u) || prov == u.getLocation()) {
					// invalid order
					u.hold();
				}
				else {
					moves.add(u);						
				}					
			}
		} // for
	}

	
	// determine which provinces and units are attacked, and by whom.
	private void findAttacks(Unit[] moves, Map attackedProvs, Map attackedUnits) {
		for (int i = 0; i < moves.length; i++) {
			Province p = moves[i].getMoveDestination();
			List l = (List)attackedProvs.get(p);
			if (l == null) {
				l = new ArrayList();
				attackedProvs.put(p, l);
			}
			l.add(moves[i]);
			
			Unit u = p.getOccupant();
			if (u != null) {
				l = (List)attackedUnits.get(u);
				if (l == null) {
					l = new ArrayList();
					attackedUnits.put(u, l);
				}
				l.add(moves[i]);
			}
		}		
	}
	
	
	// find out which units receive support, and by whom
	private Map findSupports(Collection supporters) {
		Map supports = new HashMap();
		for (Iterator i = supporters.iterator(); i.hasNext(); ) {
			Unit supp = (Unit)i.next();
			Object key = supp.getSupportedUnit();
			List l = (List)supports.get(key);
			if (l == null) {
				l = new ArrayList();
				supports.put(key, l);
			}
			l.add(supp);
		}
		return supports;
	}

	
	// set a unit to hold, and update the support map accordingly (remove
	// supports that will be unused)
	private void holdAndUpdateSupport(Map supports, Unit u) {
		if (u.isSupporting()) {
			List l = (List)supports.get(u.getSupportedUnit());
			if (l != null) {
				if (l.size() == 1) {
					supports.remove(u.getSupportedUnit());
				}
				else {
					l.remove(u);
				}
			}
			u.hold();
		}
		else {
			List l = (List)supports.remove(u);
			if (l != null) {
				for (int i = 0, max = l.size(); i < max; i++) {
					((Unit)l.get(i)).hold();
				}
			}		
			u.hold();
		}
	}

	
	// evaluate moves into empty provinces which are not under attack by
	// other units; return true iff something changed
	private boolean moveUndisputed(Map attackedProvs, Map supports) {
		boolean result = false;
		
		for (Iterator i = attackedProvs.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			Province p = (Province)e.getKey();
			if (p.getOccupant() != null) continue;
			List l = (List)e.getValue();
			if (l.size() != 1) continue;
						
			// remove the attack
			i.remove();
			
			// execute the move
			Unit u = (Unit)l.get(0);
			if (DEBUG) {
				System.out.println("moving " + u + " without dispute");
			}
			u.hold();			
			p.setOccupant(u);
			
			// remove any supports given to the moved unit
			l = (List)supports.remove(u);
			if (l != null) {
				for (int j = 0; j < l.size(); j++) {
					((Unit)l.get(j)).hold();
				}
			}								

			// indicate a change
			result = true;
		}		
		
		return result;
	}
	
	
	// find the largest combined army or number of armies attacking a given
	// province, and classify them according to their size
	private List[] findLargestDispute(Province[] p, Map attacks, Map supports) {
		int largestSize = 0;
		Unit largest = null;

		// look among the supporting units first
		for (Iterator i = supports.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			List s = (List)e.getValue();
						
			Unit u = (Unit)e.getKey();
			Province dest = u.getMoveDestination();
			Unit occupant = dest == null ? null : dest.getOccupant();

			// prefer a province where a supporting unit is under attack
			// by another player
			if (s != null && (s.size() >= largestSize ||
					occupant != null && s.size() >= largestSize - 1 && 
					occupant.isSupporting())) {
				largestSize = s.size() + 1;
				largest = u;
				if ((p[0] = dest) == null) {
					// the occupant is holding - use its location
					p[0] = largest.getLocation();
				}
			}
		}
		
		if (largest == null) {
			// no supports - find the province attacked by the most units,
			// including any unit occupying the province in the consideration
			for (Iterator i = attacks.entrySet().iterator(); i.hasNext(); ) {
				Map.Entry e = (Map.Entry)i.next();
				List a = (List)e.getValue();
				Province prov = (Province)e.getKey();
				int size = (prov.getOccupant() != null ? 1 : 0) + a.size();
				
				for (Iterator it = a.iterator(); it.hasNext(); ) {
					Unit u = (Unit)it.next();
					if (size >= largestSize) {
						largestSize = size;
						largest = u;
						p[0] = prov;
						if (u.isMoving()) break;
					}
				}
			}
			// the largest army will be of strength 1
			largestSize = 1;
		}
		
		// classify every unit attacking the province into their size
		List[] result = new List[largestSize];		
		for (int i = 0; i < result.length; i++) {
			result[i] = new ArrayList(2);
		}
		if (!largest.isMoving()) {
			// the largest army is supporting a hold. 
			result[result.length - 1].add(largest);
		}
		else {
			for (Iterator i = ((List)attacks.get(p[0])).iterator(); 
					i.hasNext(); ) {
				Unit u = (Unit)i.next();
				List l = (List)supports.get(u);
				int idx = l == null ? 0 : l.size();
				result[idx].add(u);
			}
		}
		return result;
	}

	
	// disband or retreat all the dislodged units
	private void retreatOrDisband(Map retreats, Set standoffs) {
		for (Iterator i = retreats.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			
			// first, remove all provinces that are not vacant or
			// in which a standoff took place from the list of possible retreats
			Collection options = (Collection)e.getValue();
			for (Iterator it = options.iterator(); it.hasNext(); ) {
				Province p = (Province)it.next();
				if (p.getOccupant() != null || standoffs.contains(p)) {
					it.remove();
				}
			}
			
			Unit u = (Unit)e.getKey();
			
			// if no options remain, then the unit must be disbanded
			if (options.size() == 0) {				
				i.remove();
				disbandUnit(u);
			}
			else {
				// Find the province closest to the homeland of the unit
				Province closest = findProvince(options, 
						_homeCountries[u.getOwner()], true);

				// Order the unit to the province closest to one of its homeland
				// (there may be more than one, though. Use the first one found)
				u.moveTo(closest);
			}
		}
		
		// Now retreats contains only the units ordered to retreat. 
		// Find out which units have been ordered to retreat to the same 
		// province
		Set s = retreats.keySet();
		Map attackedProvs = new HashMap();
		findAttacks((Unit[])s.toArray(new Unit[s.size()]), attackedProvs, null);
		
		// Move or disband the units as appropriate
		for (Iterator i = attackedProvs.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			List units = (List)e.getValue();
			
			if (units.size() == 1) {
				// the unit can retreat
				if (DEBUG) {
					System.out.println("retreating " + units.get(0) + 
							" to " + e.getKey());
				}
				Unit u = (Unit)units.get(0);
				u.hold();
				((Province)e.getKey()).setOccupant(u);
			}
			else {
				// all units must be disbanded
				for (int j = 0, max = units.size(); j < max; j++) {
					disbandUnit((Unit)units.get(j));
				}
			}
		}
	}

	
	// disband the unit, that is, remove it from the internal unit lists.
	private void disbandUnit(Unit u) {
		if (DEBUG) {
			System.out.println("disbanding unit " + u);		
		}
		Province p = u.getLocation();
		if (p != null) {
			p.setOccupant(null);
		}
		
		_units.remove(u);
		_playerUnits[u.getOwner()].remove(u);
	}
		
	
	// execute the fall adjustments; return true iff a winner is found
	private boolean xeqFallAdjustments(Set[][] result) {
		int[] owned = new int[MAX_PLAYERS];
		int max = _supportCentres.size() / 2 + 1;
		boolean finished = false;
		
		// update ownership of each province
		for (Iterator i = _supportCentres.iterator(); i.hasNext(); ) {
			Province p = (Province)i.next();
			int prevOwner = p.getOwner();
			p.updateOwnership();
			int owner = p.getOwner();
			if (owner != PLAYER_NEUTRAL && ++owned[owner] >= max) {
				finished = true;
			}
			if (prevOwner != owner && prevOwner != PLAYER_NEUTRAL) {
				// generate a result for a province being overtaken
				List tmp = new ArrayList(1);
				tmp.add(PLAYER_IDs[owner]);
				addResult(result, prevOwner, tmp, 2);
			}
		}

		// adjust the size of each army
		for (int player = 0; player < _playerUnits.length; player++) {
			Set units = _playerUnits[player];
			if (units == null) continue;
			if (owned[player] == 0) {
				// record that a player was eliminated
				result[player] = ELIMINATED;
			}
			if (DEBUG) {
				System.out.println("adjusting player " + player + " from " + 
						units.size() + " to " + owned[player]);
			}

			// build a map from each of the player's units to it's location
			Map unitLocs = new HashMap();
			for (Iterator i = units.iterator(); i.hasNext(); ) {
				Unit u = (Unit)i.next();
				unitLocs.put(u, u.getLocation());
			}
			
			// disband excess units, selecting units farthest from their
			// homeland first.
			while (units.size() > owned[player]) {
				Province selected = findProvince(unitLocs.values(),
						_homeCountries[player], false);					
				Unit u = selected.getOccupant();
				unitLocs.remove(u);
				disbandUnit(u);
			}
			
			if (units.size() == owned[player]) {
				// no (further) adjustments are necessary
				continue;
			}

			// determine the possible build locations
			Set buildSites = new HashSet(_homeCountries[player]);
			for (Iterator i = buildSites.iterator(); i.hasNext(); ) {
				Province p = (Province)i.next();
				if (p.getOwner() != player || p.getOccupant() != null) {
					i.remove();
				}
			}
			
			// build the units
			for (int i = owned[player] - units.size(); i > 0; i--) {
				if (buildSites.size() == 0) {
					if (DEBUG) {
						System.out.println("no build site available.");
					}
					break;
				}
				
				// select a random location and build the unit
				Iterator it = buildSites.iterator();
				Province site = (Province)it.next();
				it.remove();
				
				Unit nu = new Unit(player, site);
				_playerUnits[player].add(nu);
				_units.add(nu);
				if (DEBUG) {
					System.out.println("built unit " + nu);
				}
			}
		}
		
		return finished;
	}

	
				
	// find the neighbour province to move to such that its
	// distance to the target is minimized,
	// while avoiding to enter neutral provinces, attacking friendly
	// units, and only attacking enemy or own units when no other possibility
	// exists
	private Province findRoute(Province source, Province dest,
			Collection friendly, Collection neutral) {
		Collection ns = _board.getNeighbours(source);
		
		Province target = null;
		int leastDist = Integer.MAX_VALUE;
		
		for (Iterator j = ns.iterator(); j.hasNext(); ) {
			Province p = (Province)j.next();
			if (neutral.contains(p)) {
				// cannot use p
				continue;
			}
			Unit u = p.getOccupant();
			int dist;
			if (u != null) {
				if (friendly.contains(PLAYER_IDs[u.getOwner()])) {
					// can't use a province occupied by a friendly unit
					continue;
				}
				// use 10 times the distance if the square is occupied
				// - i.e., only attack a unit if no other possibility
				// exists.
				dist = 10 * _board.getDistance(p, dest);
			}
			else {
				dist = _board.getDistance(p, dest);
			}

			if (dist < leastDist) {
				target = p;
				leastDist = dist;
			}
		}
		
		return target;
	}		

	
	// add a result from a given list of data
	private void addResult(Set[][] result, int player, Collection data, int index) {
		if (player < 0 || player > result.length) {
			return;
		}
		
		Set[] results = result[player];
		if (results.length == 0) {
			return;
		}
		
		Set s = results[index];
		
		for (Iterator i = data.iterator(); i.hasNext(); ) {
			Object d = i.next();
			if (d instanceof Unit) {
				int o = ((Unit)d).getOwner();
				if (o != player) {
					s.add(PLAYER_IDs[o]);
				}
			}
			else {
				s.add(d);
			}
		}		
	}

	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}