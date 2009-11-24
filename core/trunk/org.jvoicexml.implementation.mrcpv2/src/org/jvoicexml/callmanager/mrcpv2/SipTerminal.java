/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.callmanager.mrcpv2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.callmanager.BaseCallManager;
import org.jvoicexml.callmanager.Terminal;
import org.jvoicexml.event.ErrorEvent;

/**
 * A SIP terminal manages a SIP phone that runs on the server side of JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class SipTerminal implements Terminal {
    /** Name of this terminal. */
    private final String name;

    /** The call manager. */
    private final BaseCallManager manager;

    /**
     * Constructs a new object.
     * @param terminalName name of this terminal.
     * @param callManager the call manager
     */
    public SipTerminal(final String terminalName,
            final BaseCallManager callManager) {
        name = terminalName;
        manager = callManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForConnections() throws IOException {
        // TODO Initialize the SIP stack for this terminal
    }

    /**
     * Called, when a SIP phone connects to this terminal.
     * @throws URISyntaxException
     *         if the called or caller id can not be converted into a URI.
     * @throws ErrorEvent
     *         error creating the session
     */
    private void connect() throws URISyntaxException, ErrorEvent {
        // TODO adapt the parameters
        final SipCallParameters parameters = new SipCallParameters();
        final URI calledId = new URI(name);
        parameters.setCalledId(calledId);
        final URI callerId = new URI("sip:spencer@jvoicexml.org");
        parameters.setCallerId(callerId);
        parameters.setClientPort(0);
        parameters.setClientAddress(null);
        // TODO Do we need to store the session?
        manager.createSession(this, parameters);
    }

    /**
     * Called when a SIP phone is disconnected.
     */
    private void disconnect() {
        manager.terminalDisconnected(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWaiting() {
        // TODO shutdown the SIP stack for this terminal
    }
}
