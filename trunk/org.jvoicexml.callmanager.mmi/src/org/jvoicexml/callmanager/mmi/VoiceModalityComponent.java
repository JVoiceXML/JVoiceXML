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
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.ContentURLType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.LifeCycleRequest;
import org.jvoicexml.mmi.events.LifeCycleResponse;
import org.jvoicexml.mmi.events.PauseRequest;
import org.jvoicexml.mmi.events.PauseResponse;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.PrepareResponse;
import org.jvoicexml.mmi.events.ResumeRequest;
import org.jvoicexml.mmi.events.ResumeResponse;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.StartResponse;
import org.jvoicexml.mmi.events.StatusRequest;
import org.jvoicexml.mmi.events.StatusType;


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

    /** Adapter for the event and transport layer. */
    private ETLProtocolAdapter adapter;

    /** Reference to the call manager. */
    private final MMICallManager callManager;

    /** Active contexts. */
    private final Map<String, MMIContext> contexts;

    /**
     * Constructs a new object.
     * @param cm the call manager
     */
    public VoiceModalityComponent(final MMICallManager cm) {
        callManager = cm;
        contexts = new java.util.HashMap<String, MMIContext>();
    }

    /**
     * Starts accepting MMI lifecycle events asynchronously.
     * @param protocolAdapter the adapter for the event and transport layer
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
     * Checks, if this modality component is currently accepting lifecycle
     * events.
     * @return <code>true</code> if lifecycle events are not accepted.
     */
    boolean isAcceptingLifecycleEvents() {
        return adapter.isStarted();
    }

    /**
     * Sends a response to the given channel.
     * @param channel the channel to use
     * @param event the response to send
     * @exception IOException
     *            if an error occurs when sending the response
     */
    void sendResponse(final Object channel, final LifeCycleEvent event) 
        throws IOException {
        try {
            adapter.sendMMIEvent(channel, event);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            if (event instanceof LifeCycleResponse) {
                final LifeCycleResponse response = (LifeCycleResponse) event;
                final String contextId = response.getContext();
                if (contextId != null) {
                    removeContext(contextId);
                }
            }
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivedEvent(final DecoratedMMIEvent evt) {
        final LifeCycleEvent event = evt.getEvent();
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
        } else if (event instanceof StatusRequest) {
            final StatusRequest request = (StatusRequest) event;
            status(channel, request);
        }
    }

    /**
     * Retrieves the MMI context for the given context id.
     * @param contextId the context id to look up
     * @return the context, maybe <code>null</code>
     * @since 0.7.6
     */
    MMIContext getContext(final URI contextId) {
        synchronized (contexts) {
            return contexts.get(contextId.toString());
        }
    }

    /**
     * Obtains the MMI context to use for the given request. If no previous
     * context exists for the given context id specified in the received
     * MMI message, a new one will be created if specified and added to the
     * list of known
     * contexts.
     * @param request the received MMI event
     * @param create create a new context if it was previously unknown
     * @return associated MMI context
     * @throws MMIMessageException
     *         if either the context id or the request id are missing
     */
    private MMIContext getContext(final LifeCycleRequest request,
            final boolean create) throws MMIMessageException {
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            throw new MMIMessageException("No request id given");
        }
        MMIContext context;
        synchronized (contexts) {
            context = contexts.get(contextId);
        }
        if (context == null) {
            if (!create) {
                throw new MMIMessageException("Context '" + contextId
                        + "' refers to an unknown context");
            }
            try {
                context = new MMIContext(contextId);
            } catch (URISyntaxException e) {
                throw new MMIMessageException(e.getMessage(), e);
            }
            synchronized (contexts) {
                contexts.put(contextId, context);
            }
        }
        return context;
    }

    /**
     * Processes a start request.
     * @param channel the channel that was used to send the request
     * @param request the received event
     */
    private void prepare(final Object channel, final PrepareRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a prepare request for context " + contextId
                + " with request id " + requestId);
        MMIContext context = null;
        try {
            context = getContext(request, true);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        if (statusInfo == null) {
            final ContentURLType contentUrlType = request.getContentURL();
            if (contentUrlType != null) {
                final String href = contentUrlType.getHref();
                try {
                    context.setContentURL(href);
                    LOGGER.info("preparing URI '" + href + "'");
                } catch (URISyntaxException e) {
                    LOGGER.error(e.getMessage(), e);
                    statusInfo = e.getMessage();
                }
            }
        }
        try {
            if (statusInfo == null) {
                final URI uri = context.getContentURL();
                LOGGER.info("creating session for URI '" + uri + "'");
                final Session session = callManager.createSession();
                context.setSession(session);
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final PrepareResponse response = new PrepareResponse();
        final String target = request.getSource();
        response.setTarget(target);
        response.setContext(contextId);
        response.setRequestId(requestId);
        if (statusInfo == null) {
            response.setStatus(StatusType.SUCCESS);
        } else {
            LOGGER.info("prepare failed: " + statusInfo);
            response.setStatus(StatusType.FAILURE);
            response.addStatusInfo(statusInfo);
        }
        try {
            adapter.sendMMIEvent(channel, response);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a start request.
     * @param channel the channel that was used to send the request
     * @param request the received event
     */
    private void start(final Object channel, final StartRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a start request for context " + contextId
                + " with request id " + requestId);
        MMIContext context = null;
        try {
            context = getContext(request, true);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final ModalityComponentState state = context.getState();
        if (state == ModalityComponentState.RUNNING) {
            LOGGER.info("terminating old session");
            final Session session = context.getSession();
            context.setSession(null);
            session.hangup();
            callManager.cleanupSession(session);
        }
        final String source = request.getSource();
        context.setTarget(source);
        context.setChannel(channel);
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
                Session session = context.getSession();
                if (session == null) {
                    session = callManager.createSession();
                    context.setSession(session);
                }
                session.call(uri);
                session.addSessionListener(this);
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final StartResponse response = new StartResponse();
        final String target = request.getSource();
        response.setTarget(target);
        response.setContext(contextId);
        response.setRequestId(requestId);
        if (statusInfo == null) {
            response.setStatus(StatusType.SUCCESS);
            context.setRequestId(requestId);
        } else {
            LOGGER.info("start failed: " + statusInfo);
            response.setStatus(StatusType.FAILURE);
            response.addStatusInfo(statusInfo);
        }
        try {
            adapter.sendMMIEvent(channel, response);
            context.setState(ModalityComponentState.RUNNING);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a cancel request.
     * @param channel the channel that was used to send the request
     * @param request the cancel request.
     */
    private void cancel(final Object channel, final CancelRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a cancel request for context " + contextId
                + " with request id " + requestId);
        MMIContext context = null;
        try {
            context = getContext(request, false);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final ModalityComponentState state = context.getState();
        if ((state == ModalityComponentState.RUNNING)
                || (state == ModalityComponentState.PAUSED)) {
            LOGGER.info("hanging up session");
            final Session session = context.getSession();
            context.setSession(null);
            session.hangup();
            callManager.cleanupSession(session);
        } else if (state == ModalityComponentState.IDLE) {
            statusInfo = "session is idle: ignoring cancel request";
        } else {
            statusInfo =
                    "no running session for the given context " + contextId;
        }
        final CancelResponse response = new CancelResponse();
        response.setRequestId(requestId);
        response.setContext(contextId);
        final String target = request.getSource();
        response.setTarget(target);
        if (statusInfo == null) {
            response.setStatus(StatusType.SUCCESS);
        } else {
            LOGGER.info("cancel failed: " + statusInfo);
            response.setStatus(StatusType.FAILURE);
            response.addStatusInfo(statusInfo);
        }
        try {
            adapter.sendMMIEvent(channel, response);
            if (statusInfo == null) {
                context.setState(ModalityComponentState.IDLE);
                LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Removes the context with the given id from the list of known contexts.
     * @param contextId the context to remove
     */
    private void removeContext(final String contextId) {
        synchronized (contexts) {
            contexts.remove(contextId);
            LOGGER.info("cleared context '" + contextId + "'");
        }
    }

    /**
     * Processes a clear context request.
     * @param channel the channel that was used to send the request
     * @param request the clear context request.
     */
    private void clearContext(final Object channel,
            final ClearContextRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a clear context request for context " + contextId
                + " with request id " + requestId);
        MMIContext context = null;
        try {
            context = getContext(request, false);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        if (statusInfo != null) {
            removeContext(contextId);
            final ModalityComponentState state = context.getState();
            final Session session = context.getSession();
            if (state == ModalityComponentState.RUNNING) {
                LOGGER.info("hanging up session");
                if (session != null) {
                    context.setSession(null);
                    session.hangup();
                }
            }
            if (session != null) {
                callManager.cleanupSession(session);
            }
        }
        final ClearContextResponse response =
                new ClearContextResponse();
        response.setRequestId(requestId);
        response.setContext(contextId);
        final String target = request.getSource();
        response.setTarget(target);
        if (statusInfo == null) {
            response.setStatus(StatusType.SUCCESS);
        } else {
            LOGGER.info("clear failed: " + statusInfo);
            response.setStatus(StatusType.FAILURE);
            response.addStatusInfo(statusInfo);
        }
        try {
            adapter.sendMMIEvent(channel, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a clear context request.
     * @param channel the channel that was used to send the request
     * @param request the clear context request.
     */
    private void pause(final Object channel, final PauseRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a pause request for context " + contextId
                + " with request id " + requestId);
        try {
            getContext(request, false);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final PauseResponse response = new PauseResponse();
        response.setRequestId(requestId);
        response.setContext(contextId);
        final String target = request.getSource();
        response.setTarget(target);
        response.setStatus(StatusType.FAILURE);
        if (statusInfo != null) {
            statusInfo = "The JVoiceXML modality component is unable to pause";
        }
        response.addStatusInfo(statusInfo);
        try {
            adapter.sendMMIEvent(channel, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a clear context request.
     * @param channel the channel that was used to send the request
     * @param request the clear context request.
     */
    private void resume(final Object channel, final ResumeRequest request) {
        String statusInfo = null;
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a resume request for context " + contextId
                + " with request id " + requestId);
        try {
            getContext(request, false);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final ResumeResponse response = new ResumeResponse();
        response.setRequestId(requestId);
        response.setContext(contextId);
        final String target = request.getSource();
        response.setTarget(target);
        response.setStatus(StatusType.FAILURE);
        if (statusInfo != null) {
            statusInfo = "The JVoiceXML modality component is unable to resume";
        }
        response.addStatusInfo(statusInfo);
        try {
            adapter.sendMMIEvent(channel, response);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a clear context request.
     * @param channel the channel that was used to send the request
     * @param request the clear context request.
     */
    private void status(final Object channel, final StatusRequest request) {
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        LOGGER.info("received a status request for context " + contextId
                + " with request id " + requestId);
        final boolean automaticUpdate = request.isRequestAutomaticUpdate();
        URI context = null;
        if (contextId != null) {
            try {
                context = new URI(contextId);
            } catch (URISyntaxException e) {
                LOGGER.warn("context '" + contextId
                        + "' does not denote a valid URI. Unable to send status"
                        + " response messages");
                return;
            }
        }
        final String target = request.getSource();
        final StatusUpdateThread thread = new StatusUpdateThread(this, channel,
                target, context, requestId, automaticUpdate);
        thread.start();
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
     * Tries to find the context for the given session.
     * @param session the session that maybe associated with an MMI context
     * @return found context, <code>null</code> if there is no context for that
     *          session
     */
    private MMIContext findContext(final Session session) {
        final String sessionId = session.getSessionID();
        synchronized (contexts) {
            for (String contextId : contexts.keySet()) {
                final MMIContext context = contexts.get(contextId);
                final Session other = context.getSession();
                if (other != null) {
                    final String otherSessionId = other.getSessionID();
                    if (otherSessionId.equals(sessionId)) {
                        return context;
                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnded(final Session session) {
        final MMIContext context = findContext(session);
        if (context == null) {
            LOGGER.warn("session " + session.getSessionID()
                    + " ended without MMI identifiers");
            return;
        }
        final String requestId = context.getRequestId();
        final String contextId = context.getContextId();
        final String target = context.getTarget();
        final DoneNotification done = new DoneNotification();
        done.setContext(contextId);
        done.setRequestId(requestId);
        done.setTarget(target);
        try {
            final Object channel = context.getChannel();
            adapter.sendMMIEvent(channel, done);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
