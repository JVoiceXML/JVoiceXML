/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 2493 $
 * Date:    $Date: 2011-01-10 11:25:46 +0100 (Mo, 10 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.callmanager.mmi;


import java.io.IOException;

/**
 * Adapter to the actual used protocol used in the event and transport layer
 * to receive and respond to MMI events.
 * Events can be received between calls to {@link #start()} and {@link stop}.
 * Once the adapter receives an event, it is propagated to all
 * registered {@link MMIEventListener}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public interface ETLProtocolAdapter {
    /**
     * Starts the protocol adapter.
     * @throws IOException
     *         error starting the protocol adapter
     */
    void start() throws IOException;

    /**
     * Registers the given listener for MMI events.
     * @param listener the listener to add
     */
    void addMMIEventListener(final MMIEventListener listener);

    /**
     * Deregisters the given listener for MMI events.
     * @param listener the listener to remove
     */
    void removeMMIEventListener(final MMIEventListener listener);

    /**
     * Sends the given MMI event to the event and transport layer.
     * @param event the event to send
     * @exception IllegalArgumentException
     */
    void sendMMIEvent(final Object event) throws IllegalArgumentException;

    /**
     * Stops the protocol adapter.
     */
    void stop();
}
