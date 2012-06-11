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
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.client.ConnectionInformationController;
import org.jvoicexml.client.ConnectionInformationFactory;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A callmanager for MMI integration.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class MMICallManager implements CallManager {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(MMICallManager.class);

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /** The adapter for the used ETL protocol. */
    private ETLProtocolAdapter adapter;

    /** Reference to the voice modality component. */
    private VoiceModalityComponent mc;

    /** A factory for connection information objects. */
    private ConnectionInformationFactory factory;

    /** Created sessions. */
    private final Map<Session, ConnectionInformationController> sessions;

    /** Identifier for the call control to use. */
    private String call;

    /** Identifier for the spoken input to use. */
    private String input;
    
    /** Identifier for the system output to use. */
    private String output;

    /**
     * Constructs a new object.
     */
    public MMICallManager() {
        sessions =
             new java.util.HashMap<Session, ConnectionInformationController>();
    }

    /**
     * Sets the identifier for the call control.
     * @param value identifier for the call control
     */
    public void setCall(final String value) {
        call = value;
    }

    /**
     * Sets the identifier for the spoken input.
     * @param call identifier for the spoken input
     */
    public void setInput(final String value) {
        input = value;
    }

    /**
     * Sets the identifier for the system output.
     * @param call identifier for the system output
     */
    public void setOutput(final String value) {
        output = value;
    }


    /**
     * Sets the adapter for the ETL specific protocol.
     * @param protocolAdapter the adapter to use.
     */
    public void setProtocolAdapter(final ETLProtocolAdapter protocolAdapter) {
        adapter = protocolAdapter;
    }

    /**
     * Sets the connection information factory.
     * @param connectionInformationFactory the connection information factory
     * 
     */
    public void setConnectionInformationFactory(
            final ConnectionInformationFactory connectionInformationFactory) {
        factory = connectionInformationFactory;
    }

    /**
     * {@link}
     */
    @Override
    public void setJVoiceXml(JVoiceXml jvoicexml) {
        jvxml = jvoicexml;

    }

    /**
     * Retrieves the voice modality component.
     * @return the voice modality component. 
     */
    VoiceModalityComponent getVoiceModalityComponent() {
        return mc;
    }

    /**
     * {@link}
     */
    @Override
    public void start() throws NoresourceError, IOException {
        if (adapter == null) {
            throw new IOException(
                    "Unable to hook to the ETL without a protocol adapter!");
        }
        mc = new VoiceModalityComponent(this);
        mc.startAcceptingLifecyleEvents(adapter);
    }

    /**
     * Creates a session and calls the given URI. Created sessions must be
     * cleaned up after the session has ended using
     * {@link #cleanupSession(Session)}.
     * @param uri the URI to call
     * @return created session
     * @throws ErrorEvent
     *         error calling the URI 
     * @throws UnsupportedResourceIdentifierException 
     *         error in the URI scheme
     */
    public Session createSession()
            throws ErrorEvent, UnsupportedResourceIdentifierException {
        final ConnectionInformationController controller =
                factory.createConnectionInformation(call, output, input);
        final ConnectionInformation info =
                controller.getConnectionInformation();
        final Session session = jvxml.createSession(info);
        sessions.put(session, controller);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("session '" + session.getSessionID() + "' created");
        }
        return session;
    }

    /**
     * Cleanup of the resources when creating the session.
     * @param session the session
     */
    public void cleanupSession(final Session session) {
        final ConnectionInformationController controller =
                sessions.get(session);
        if (session == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("no controller for session '" + session.getSessionID()
                        + "'");
            }
            return;
        }
        controller.cleanup();
    }

    /**
     * {@link}
     */
    @Override
    public void stop() {
        if (mc == null) {
            return;
        }
        mc.stopAcceptingLifecycleEvents();
    }

}
