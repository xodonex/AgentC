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
package dk.dtu.imm.cse.agent.act.afc;

import java.util.*;


/**
 * The Actuator is a "plug-in" for the {@link Acme ACME}, bridging between
 * the ACME and the communication infrastructure to be used.
 *
 * @author  Henrik Lauritzen
 */
public interface Messenger {

	// =======================================================================
	// Fields
	// =======================================================================

	// =======================================================================
	// Methods
	// =======================================================================

	/**
	 * Send a message; this corresponds to the SAY primitive in AgentC.
	 * The Messenger determines by itself how to obtain an addressee from 
	 * the given data, and may modify the message if so desired.
	 * @param msg the message to be sent. The keys of the map will be
	 *  {@link String} instances.
	 */
	public void send(Map msg);
	
	// ***********************************************************************

	// =======================================================================
	// Inner classes
	// =======================================================================
	
}
