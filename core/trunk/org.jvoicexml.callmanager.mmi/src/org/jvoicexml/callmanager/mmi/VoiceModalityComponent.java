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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.SessionListener;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.CancelResponse;
import org.jvoicexml.mmi.events.CancelResponseBuilder;
import org.jvoicexml.mmi.events.ContentURLType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StatusResponse;


/**
 * The {@link VoiceModalityComponent} accepts MMI lifecycle events. Internally,
 * it speaks to JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class VoiceModalityComponent
    implements MMIEventListener, SessionListener {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(VoiceModalityComponent.class);

    /** Adpater for the event and transport layer. */
    private ETLProtocolAdapter adapter;

    /** Reference to the call manager. */
    private final MMICallManager callManager;

    /** Created sessions. */
    private final Map<Session, ChannelMMIRequestIdentifier> sessions;

    /**
     * Constructs a new object.
     */
    public VoiceModalityComponent(final MMICallManager cm) {
        callManager = cm;
        sessions = new java.util.HashMap<Session, ChannelMMIRequestIdentifier>();
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
     * Determines the session from the given identifiers.
     * @param requestId the request id
     * @param contextId the context id
     * @return found session, or <code>null</code> if there is no session
     */
    private Session findSession(final String requestId,
            final String contextId) {
        for (ChannelMMIRequestIdentifier ids : sessions.values()) {
            final String reqId = ids.getRequestId();
            final String ctxId = ids.getContexId();
            if (requestId.equals(reqId) || contextId.equals(ctxId)) {
                return ids.getSession();
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final MMIEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received new MMI event: " + event);
        }
        if (event instanceof StartRequest) {
            final StartRequest request = (StartRequest) event;
            startRequest(request);
        } else if (event instanceof CancelRequest) {
            final CancelRequest request = (CancelRequest) event;
            cancelRequest(request);
        }
    }

    /**
     * Processes a start request.
     * @param request the received event
     */
    private void startRequest(final StartRequest request) {
        final ContentURLType contentUrlType = request.getContentURL();
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        final ChannelMMIRequestIdentifier ids =
                new ChannelMMIRequestIdentifier(requestId, contextId);
        final Object channel = request.getSource();
        ids.setChannel(channel);
        final String href = contentUrlType.getHref();
        LOGGER.info("received a start request for " + ids + " to " + href);
        try {
            final URI uri = new URI(href);
            final Session session = callManager.createSession(uri);
            synchronized (sessions) {
                sessions.put(session, ids);
            }
            session.addSessionListener(this);
            ids.setSession(session);
            final StatusResponse response = new StatusResponse();
            response.setContext(contextId);
            response.setRequestID(requestId);
            adapter.sendMMIEvent(channel, response);
        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a cancel request.
     * @param request the cancel request.
     */
    private void cancelRequest(final CancelRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a cancel request for " + requestId + ", "
                + contextId);
        final Session session = findSession(requestId, contextId);
        if (session != null) {
            session.hangup();
        }
        final CancelResponseBuilder builder = new CancelResponseBuilder();
        builder.setRequestId(requestId);
        builder.setContextId(contextId);
        if (session == null) {
            builder.setStatusFailure();
            builder.addStatusInfo(
                    "no running session for the given request and context ids");
        } else {
            builder.setStatusSuccess();
        }
        final CancelResponse response = builder.toCancelResponse();
        final Object channel = request.getSource();
        try {
            adapter.sendMMIEvent(channel, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Stops accepting MMI lifecycle events.
     */
    public void stopAcceptingLifecycleEvents() {
        adapter.stop();
        LOGGER.info("stopped ETL protocol adapter " + adapter.getClass()
                + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionStarted(final Session session) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnded(final Session session) {
        final ChannelMMIRequestIdentifier ids;
        synchronized (sessions) {
            ids = sessions.remove(session);
        }
        if (ids == null) {
            LOGGER.warn("session " + session.getSessionID()
                    + " ended without MMI identifiers");
            return;
        }
        final String requestId = ids.getRequestId();
        final String contextId = ids.getContexId();
        final DoneNotification done = new DoneNotification();
        done.setContext(contextId);
        done.setRequestID(requestId);
        try {
            final Object channel = ids.getChannel();
            adapter.sendMMIEvent(channel, done);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
