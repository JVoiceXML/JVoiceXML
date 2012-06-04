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
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.ClearContextResponseBuilder;
import org.jvoicexml.mmi.events.ContentURLType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.MMIEvent;
import org.jvoicexml.mmi.events.PauseRequest;
import org.jvoicexml.mmi.events.PauseResponse;
import org.jvoicexml.mmi.events.PauseResponseBuilder;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.PrepareResponseBuilder;
import org.jvoicexml.mmi.events.ResumeRequest;
import org.jvoicexml.mmi.events.ResumeResponse;
import org.jvoicexml.mmi.events.ResumeResponseBuilder;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.mmi.events.StartResponseBuilder;


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

    /** Active contexts. */
    private final Map<Session, MMIContext> contexts;

    /**
     * Constructs a new object.
     */
    public VoiceModalityComponent(final MMICallManager cm) {
        callManager = cm;
        contexts = new java.util.HashMap<Session, MMIContext>();
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
        adapter.addMMIEventListener(this);
        LOGGER.info("starting ETL protocol adapter " + adapter.getClass()
                + "'");
        adapter.start();
        
    }

    /**
     * Determines the {@link MMIContext} from the given
     * identifiers.
     * @param requestId the request id
     * @param contextId the context id
     * @return found identifiers, or <code>null</code> if there is no identifier
     */
    private MMIContext findMMIContext(
            final String requestId, final String contextId) {
        if (contextId == null) {
            LOGGER.warn("no context id given");
            return null;
        }
        for (MMIContext ctx : contexts.values()) {
            final String ctxId = ctx.getContexId();
            if (contextId.equals(ctxId)) {
                return ctx;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final DecoratedMMIEvent evt) {
        final MMIEvent event = evt.getEvent();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received new MMI event: " + event);
        }
        final Object channel = evt.getChannel();
        if (event instanceof PrepareRequest) {
            final PrepareRequest request = (PrepareRequest) event;
            prepare(channel, request);
        } else if (event instanceof StartRequest) {
            final StartRequest request = (StartRequest) event;
            start(channel, request);
        } else if (event instanceof CancelRequest) {
            final CancelRequest request = (CancelRequest) event;
            cancel(channel, request);
        } else if (event instanceof ClearContextRequest) {
            final ClearContextRequest request = (ClearContextRequest) event;
            clearContext(channel, request);
        } else if (event instanceof PauseRequest) {
            final PauseRequest request = (PauseRequest) event;
            pause(channel, request);
        } else if (event instanceof ResumeRequest) {
            final ResumeRequest request = (ResumeRequest) event;
            resume(channel, request);
        }
    }

    /**
     * Processes a start request.
     * @param request the received event
     */
    private void prepare(final Object channel, final PrepareRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        MMIContext context =
                findMMIContext(requestId, contextId);
        if (context == null) {
            context = new MMIContext(requestId, contextId);
        }
        String statusInfo = null;
        final ContentURLType contentUrlType = request.getContentURL();
        if (contentUrlType != null) {
            final String href = contentUrlType.getHref();
            try {
                context.setContentURL(href);
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
                statusInfo = e.getMessage();
            }
        }
        final URI uri = context.getContentURL();
        try {
            if (statusInfo == null) {
                LOGGER.info("creating session for URI '" + uri + "'");
                final Session session = callManager.createSession();
                synchronized (contexts) {
                    contexts.put(session, context);
                }
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final PrepareResponseBuilder builder = new PrepareResponseBuilder();
        final String target = request.getSource();
        builder.setTarget(target);
        builder.setContextId(contextId);
        builder.setRequestId(requestId);
        if (statusInfo == null) {
            builder.setStatusSuccess();
        } else {
            builder.setStatusFailure();
            builder.addStatusInfo(statusInfo);
        }
        final PrepareResponse response = builder.toPrepareResponse();
        try {
            adapter.sendMMIEvent(channel, response);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a start request.
     * @param request the received event
     */
    private void start(final Object channel, final StartRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        MMIContext context =
                findMMIContext(requestId, contextId);
        if (context == null) {
            context = new MMIContext(requestId, contextId);
        } else {
            final ModalityComponentState state = context.getState();
            if (state == ModalityComponentState.RUNNING) {
                LOGGER.info("terminating old session");
                final Session session = context.getSession();
                session.hangup();
            }
        }
        final String source = request.getSource();
        context.setTarget(source);
        context.setChannel(channel);
        String statusInfo = null;
        final ContentURLType contentUrlType = request.getContentURL();
        URI uri = context.getContentURL();
        if (contentUrlType != null) {
            final String href = contentUrlType.getHref();
            try {
                if (href != null) {
                    uri = new URI(href);
                }
            } catch (URISyntaxException e) {
                LOGGER.error(e.getMessage(), e);
                statusInfo = e.getMessage();
            }
        }
        if (uri == null) {
            statusInfo = "no URI given. Unable to start";
        }
        try {
            if (statusInfo == null) {
                LOGGER.info("calling '" + uri + "'");
                final Session session = callManager.createSession();
                session.call(uri);
                synchronized (contexts) {
                    contexts.put(session, context);
                }
                session.addSessionListener(this);
                context.setSession(session);
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final StartResponseBuilder builder = new StartResponseBuilder();
        final String target = request.getSource();
        builder.setTarget(target);
        builder.setContextId(contextId);
        builder.setRequestId(requestId);
        if (statusInfo == null) {
            builder.setStatusSuccess();
        } else {
            builder.setStatusFailure();
            builder.addStatusInfo(statusInfo);
        }
        final StartResponse response = builder.toStartResponse();
        try {
            adapter.sendMMIEvent(channel, response);
            context.setState(ModalityComponentState.RUNNING);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a cancel request.
     * @param request the cancel request.
     */
    private void cancel(final Object channel, final CancelRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        final MMIContext context =
                findMMIContext(requestId, contextId);
        String statusInfo = null;
        if (context == null) {
            statusInfo =
                    "no running session for the given context " + contextId;
        } else {
            final ModalityComponentState state = context.getState();
            if (state == ModalityComponentState.IDLE) {
                statusInfo = "session is idle: ignoring cancel request";
            } else {
                LOGGER.info("hanging up session");
                final Session session = context.getSession();
                session.hangup();
            }
        }
        final CancelResponseBuilder builder = new CancelResponseBuilder();
        builder.setRequestId(requestId);
        builder.setContextId(contextId);
        final String target = request.getSource();
        builder.setTarget(target);
        if (statusInfo == null) {
            builder.setStatusSuccess();
        } else {
            LOGGER.info(statusInfo);
            builder.setStatusFailure();
            builder.addStatusInfo(statusInfo);
        }
        final CancelResponse response = builder.toCancelResponse();
        try {
            adapter.sendMMIEvent(channel, response);
            if (statusInfo == null) {
                context.setState(ModalityComponentState.IDLE);
                LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a clear context request.
     * @param request the clear context request.
     */
    private void clearContext(final Object channel,
            final ClearContextRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        final MMIContext context =
                findMMIContext(requestId, contextId);
        String statusInfo = null;
        if (context == null) {
            statusInfo =
                    "no running session for the given context " + contextId;
        } else {
            synchronized (contexts) {
                contexts.remove(context);
            }
            final ModalityComponentState state = context.getState();
            if (state == ModalityComponentState.RUNNING) {
                LOGGER.info("hanging up session");
                final Session session = context.getSession();
                if (session != null) {
                    session.hangup();
                }
            }
        }
        final ClearContextResponseBuilder builder =
                new ClearContextResponseBuilder();
        builder.setRequestId(requestId);
        builder.setContextId(contextId);
        final String target = request.getSource();
        builder.setTarget(target);
        if (statusInfo == null) {
            builder.setStatusSuccess();
        } else {
            LOGGER.info(statusInfo);
            builder.setStatusFailure();
            builder.addStatusInfo(statusInfo);
        }
        final ClearContextResponse response =
                builder.toClearContextResponse();
        try {
            adapter.sendMMIEvent(channel, response);
            if (statusInfo == null) {
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a clear context request.
     * @param request the clear context request.
     */
    private void pause(final Object channel, final PauseRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        final PauseResponseBuilder builder = new PauseResponseBuilder();
        builder.setRequestId(requestId);
        builder.setContextId(contextId);
        final String target = request.getSource();
        builder.setTarget(target);
        builder.setStatusFailure();
        builder.addStatusInfo(
                "The JVoiceXML modality component is unable to pause");
        final PauseResponse response = builder.toPauseResponse();
        try {
            adapter.sendMMIEvent(channel, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a clear context request.
     * @param request the clear context request.
     */
    private void resume(final Object channel, final ResumeRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestID();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        final ResumeResponseBuilder builder = new ResumeResponseBuilder();
        builder.setRequestId(requestId);
        builder.setContextId(contextId);
        final String target = request.getSource();
        builder.setTarget(target);
        builder.setStatusFailure();
        builder.addStatusInfo(
                "The JVoiceXML modality component is unable to resume");
        final ResumeResponse response = builder.toResumeResponse();
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
        final MMIContext context;
        synchronized (contexts) {
            context = contexts.get(session);
        }
        if (context == null) {
            LOGGER.warn("session " + session.getSessionID()
                    + " ended without MMI identifiers");
            return;
        }
        final String requestId = context.getRequestId();
        final String contextId = context.getContexId();
        final String target = context.getTarget();
        final DoneNotification done = new DoneNotification();
        done.setContext(contextId);
        done.setRequestID(requestId);
        done.setTarget(target);
        try {
            final Object channel = context.getChannel();
            adapter.sendMMIEvent(channel, done);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
