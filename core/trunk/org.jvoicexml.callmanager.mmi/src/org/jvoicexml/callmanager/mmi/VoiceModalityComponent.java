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

import org.apache.log4j.Logger;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.StartRequest;


/**
 * The {@link VoiceModalityComponent} accepts MMI lifecycle events. Internally,
 * it speaks to JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public class VoiceModalityComponent implements MMIEventListener {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(VoiceModalityComponent.class);

    /** Adpater for the event and transport layer. */
    private ETLProtocolAdapter adapter;

    /** Reference to the call manager. */
    private final MMICallManager callManager;

    /**
     * Constructs a new object.
     */
    public VoiceModalityComponent(final MMICallManager cm) {
        callManager = cm;
    }

    /**
     * Starts accepting MMI lifecycle events asynchronously.
     * @param adapter the adapter for the event and transport layer
     * @throws IOException
     *         error starting the modality component
     */
    public void startAcceptingLifecyleEvents(
            final ETLProtocolAdapter protocolAdapter)
            throws IOException {
        adapter = protocolAdapter;
        LOGGER.info("starting ETL protocol adapter " + adapter.getClass()
                + "'");
        adapter.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final MMIEvent event) {
        if (event instanceof StartRequest) {
            final StartRequest request = (StartRequest) event;
            startRequest(request);
        }
    }

    /**
     * Processes a start request.
     * @param request the received event
     */
    private void startRequest(final StartRequest request) {
    }

    /**
     * Stops accepting MMI lifecycle events.
     */
    public void stopAcceptingLifecycleEvents() {
        adapter.stop();
        LOGGER.info("stopped ETL protocol adapter " + adapter.getClass()
                + "'");
    }
}
