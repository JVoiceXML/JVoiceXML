/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SessionListener;
import org.jvoicexml.client.UnsupportedResourceIdentifierException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.interpreter.DetailedSessionListener;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.CancelRequest;
import org.jvoicexml.mmi.events.CancelResponse;
import org.jvoicexml.mmi.events.ClearContextRequest;
import org.jvoicexml.mmi.events.ClearContextResponse;
import org.jvoicexml.mmi.events.ContentURLType;
import org.jvoicexml.mmi.events.DoneNotification;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.LifeCycleRequest;
import org.jvoicexml.mmi.events.LifeCycleResponse;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.mmi.events.NewContextRequest;
import org.jvoicexml.mmi.events.NewContextResponse;
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
import org.w3c.dom.Element;

/**
 * The {@link VoiceModalityComponent} accepts MMI lifecycle events. Internally,
 * it speaks to JVoiceXML.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class VoiceModalityComponent
        implements MMIEventListener, SessionListener {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(VoiceModalityComponent.class);

    /** Encoding that should be used to encode/decode URLs. */
    private static String encoding = System.getProperty(
            "jvoicexml.xml.encoding", "UTF-8");

    /** Adapter for the event and transport layer. */
    private ETLProtocolAdapter adapter;

    /** Reference to the call manager. */
    private final MMICallManager callManager;

    /** Active contexts. */
    private final Map<String, MMIContext> contexts;

    /** The basic URI of the MMI servlet. */
    private final String servletBaseUri;

    /** The extension notification data converter. */
    private final ExtensionNotificationDataConverter converter;

    /** The extension notification data extractor. */
    private final ExtensionNotificationDataExtractor extractor;

    /**
     * Constructs a new object.
     * 
     * @param cm
     *            the call manager
     * @param conv
     *            the extension notification data converter
     * @param ext
     *            the extension notification data extractor
     * @param baseUri
     *            base URI of the MMI servlet
     */
    public VoiceModalityComponent(final MMICallManager cm,
            final ExtensionNotificationDataConverter conv,
            final ExtensionNotificationDataExtractor ext,
            final String baseUri) {
        callManager = cm;
        servletBaseUri = baseUri;
        converter = conv;
        extractor = ext;
        contexts = new java.util.HashMap<String, MMIContext>();
    }

    /**
     * Starts accepting MMI lifecycle events asynchronously.
     * 
     * @param protocolAdapter
     *            the adapter for the event and transport layer
     * @throws IOException
     *             error starting the modality component
     */
    public void startAcceptingLifecyleEvents(
            final ETLProtocolAdapter protocolAdapter) throws IOException {
        adapter = protocolAdapter;
        adapter.addMMIEventListener(this);
        LOGGER.info("starting ETL protocol adapter " + adapter.getClass()
                + "'");
        adapter.start();
    }

    /**
     * Checks, if this modality component is currently accepting lifecycle
     * events.
     * 
     * @return <code>true</code> if lifecycle events are not accepted.
     */
    boolean isAcceptingLifecycleEvents() {
        return adapter.isStarted();
    }

    /**
     * Sends a {@link LifeCycleEvent} to the given channel.
     * 
     * @param channel
     *            the channel to use
     * @param event
     *            the event to send
     * @exception IOException
     *                if an error occurs when sending the response
     */
    void sendLifeCycleEvent(final Object channel, final LifeCycleEvent event)
            throws IOException {
        try {
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(event);
            adapter.sendMMIEvent(channel, mmi);
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
    public void receivedEvent(final DecoratedMMIEvent evt,
            final CallMetadata data) {
        final LifeCycleEvent event = evt.getLifeCycleEvent();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received new MMI event: " + event);
        }
        final Object channel = evt.getChannel();
        if (event instanceof PrepareRequest) {
            final PrepareRequest request = (PrepareRequest) event;
            prepare(channel, request, data);
        } else if (event instanceof StartRequest) {
            final StartRequest request = (StartRequest) event;
            start(channel, request, data);
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
        } else if (event instanceof NewContextRequest) {
            final NewContextRequest request = (NewContextRequest) event;
            newContext(channel, request);
        } else if (evt.getExtensionNotification() != null) {
            final Mmi mmi = evt.getMmi();
            final ExtensionNotification ext = evt.getExtensionNotification();
            extensionInformation(channel, mmi, ext);
        } else {
            LOGGER.warn("unable to handle that MMI event: " + event);
        }
    }

    /**
     * Retrieves the MMI context for the given context id.
     * 
     * @param contextId
     *            the context id to look up
     * @return the context, maybe <code>null</code>
     * @since 0.7.7
     */
    private MMIContext getContext(final String contextId) {
        synchronized (contexts) {
            return contexts.get(contextId);
        }
    }

    /**
     * Retrieves the MMI context for the given context id.
     * 
     * @param contextId
     *            the context id to look up
     * @return the context, maybe <code>null</code>
     * @since 0.7.6
     */
    MMIContext getContext(final URI contextId) {
        final String str = contextId.toString();
        return getContext(str);
    }

    /**
     * Obtains the MMI context to use for the given request. If no previous
     * context exists for the given context id specified in the received MMI
     * message, a new one will be created if specified and added to the list of
     * known contexts.
     * 
     * @param request
     *            the received MMI event
     * @param create
     *            create a new context if it was previously unknown
     * @return associated MMI context
     * @throws MMIMessageException
     *             if either the context id or the request id are missing
     */
    private MMIContext getContext(final LifeCycleRequest request,
            final boolean create) throws MMIMessageException {
        final String contextId = request.getContext();
        final String requestId = request.getRequestId();
        if (requestId == null || requestId.isEmpty()) {
            throw new MMIMessageException("No request id given");
        }
        MMIContext context = getContext(contextId);
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
     * Copies source and target of the request into the metadata.
     * 
     * @param event
     *            the received event
     * @param data
     *            the container to copy the call meta data
     * @throws URISyntaxException
     *            error converting the call metatdata into a URL
     * @since 0.7.7
     */
    private void copyCallMetadata(final LifeCycleEvent event,
            final CallMetadata data) throws URISyntaxException {
        final String source = event.getSource();
        data.setCallingDevice(source);
        final String target = event.getTarget();
        data.setCalledDevice(target);
    }

    /**
     * Processes a start request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the received event
     * @param data
     *            call meta data
     */
    private void prepare(final Object channel, final PrepareRequest request,
            final CallMetadata data) {
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
            URI uri = null;
            try {
                uri = getUri(context, request);
                context.setContentURL(uri);
                LOGGER.info("preparing URI '" + uri + "'");
            } catch (URISyntaxException | MMIMessageException e) {
                LOGGER.error(e.getMessage(), e);
                statusInfo = e.getMessage();
            }
        }
        try {
            if (statusInfo == null) {
                final URI uri = context.getContentURL();
                LOGGER.info("creating session for URI '" + uri + "'");
                copyCallMetadata(request, data);
                final Session session = callManager.createSession(data);
                context.setSession(session);
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (URISyntaxException e) {
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Retrieves a URI of the given prepare request.
     * 
     * @param context
     *            current MMI context
     * @param request
     *            the start request
     * @return URI where to start the VoiceXML application
     * @throws URISyntaxException
     *             error creating the URI
     * @throws MMIMessageException
     *             error in the MMI message
     */
    private URI getUri(final MMIContext context, final PrepareRequest request)
            throws URISyntaxException, MMIMessageException {
        final ContentURLType contentUrlType = request.getContentURL();
        if (contentUrlType != null) {
            final String href = contentUrlType.getHref();
            if (href != null) {
                return new URI(href);
            }
        }
        final AnyComplexType content = request.getContent();
        if (content != null) {
            return createTemporaryVoiceXmlDocumentUri(context, content);
        }
        return context.getContentURL();
    }

    /**
     * Processes a start request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the received event
     * @param data
     *            the call meta data
     */
    private void start(final Object channel, final StartRequest request,
            final CallMetadata data) {
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
        URI uri = null;
        try {
            uri = getUri(context, request);
        } catch (URISyntaxException | MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        if (uri == null) {
            statusInfo = "Neither URI nor content given. Unable to start";
        } else {
            context.setContentURL(uri);
        }
        try {
            if (statusInfo == null) {
                LOGGER.info("calling '" + uri + "'");
                Session session = context.getSession();
                if (session == null) {
                    copyCallMetadata(request, data);
                    session = callManager.createSession(data);
                    context.setSession(session);
                }
                final ExtensionNotificationDataConverter converter =
                        callManager.getExtensionNotificationDataConverter();
                final DetailedSessionListener listener =
                        new MmiDetailedSessionListener(adapter, context,
                                converter);
                final JVoiceXmlSession jvxmlSession =
                        (JVoiceXmlSession) session;
                jvxmlSession.addSessionListener(listener);
                session.call(uri);
                session.addSessionListener(this);
            }
        } catch (ErrorEvent e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (UnsupportedResourceIdentifierException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        } catch (URISyntaxException e) {
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
            context.setState(ModalityComponentState.RUNNING);
            LOGGER.info(context + ": " + ModalityComponentState.RUNNING);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Retrieves a URI of the given start request.
     * 
     * @param context
     *            current MMI context
     * @param request
     *            the start request
     * @return URI where to start the VoiceXML application
     * @throws URISyntaxException
     *             error creating the URI
     * @throws MMIMessageException
     *             error in the MMI message
     */
    private URI getUri(final MMIContext context, final StartRequest request)
            throws URISyntaxException, MMIMessageException {
        final ContentURLType contentUrlType = request.getContentURL();
        if (contentUrlType != null) {
            final String href = contentUrlType.getHref();
            if (href != null && !href.isEmpty()) {
                return new URI(href);
            }
        }
        final AnyComplexType content = request.getContent();
        if (content != null) {
            return createTemporaryVoiceXmlDocumentUri(context, content);
        }
        return context.getContentURL();
    }

    /**
     * Creates a URI for the markup given in the content.
     * 
     * @param context
     *            current MMI context
     * @param content
     *            the content from the request
     * @return URI to point at a servlet generating a VoiceXML document for the
     *         VoiceXML snippet given in the content
     * @throws URISyntaxException
     *             error creating a URI
     * @throws MMIMessageException
     *             error in the MMI message
     */
    private URI createTemporaryVoiceXmlDocumentUri(final MMIContext context,
            final AnyComplexType content) throws URISyntaxException,
            MMIMessageException {
        final List<Object> list = content.getContent();
        if (list.isEmpty()) {
            return null;
        }
        final Object object = list.get(0);
        final String str = parseContent(object);
        if (str == null) {
            return null;
        }
        String encodedContentString;
        try {
            encodedContentString = URLEncoder.encode(str, encoding);
            if (isField(object)) {
                return new URI(servletBaseUri + "/VoiceXmlSnippet?field="
                        + encodedContentString);
            } else {
                return new URI(servletBaseUri + "/VoiceXmlSnippet?prompt="
                        + encodedContentString);
            }
        } catch (UnsupportedEncodingException e) {
            throw new URISyntaxException(str, e.getMessage());
        }
    }

    /**
     * Parses the content of a start request into VoiceXML snippets.
     * 
     * @param object
     *            the content element
     * @return parsed VoiceXML snippet
     * @since 0.7.7
     */
    private String parseContent(final Object object) {
        if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Element) {
            final Element element = (Element) object;
            return element.getTextContent();
        }
        return null;
    }

    /**
     * Checks if a given content in a start request denotes a field.
     * 
     * @param object
     *            the content element
     * @return {@code true} if the content denotes a field
     * @since 0.7.7
     */
    private boolean isField(final Object object) {
        if (!(object instanceof Element)) {
            return false;
        }
        final Element element = (Element) object;
        final String name = element.getLocalName();
        return "field".equalsIgnoreCase(name);
    }

    /**
     * Processes a cancel request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the cancel request.
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
            try {
                session.waitSessionEnd();
            } catch (ErrorEvent e) {
                LOGGER.error(e.getMessage(), e);
                statusInfo = e.getMessage();
            }
            callManager.cleanupSession(session);
        } else if (state == ModalityComponentState.IDLE) {
            statusInfo = "session is idle: ignoring cancel request";
        } else {
            statusInfo = "no running session for the given context "
                    + contextId;
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
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
     * 
     * @param contextId
     *            the context to remove
     */
    private void removeContext(final String contextId) {
        synchronized (contexts) {
            contexts.remove(contextId);
            LOGGER.info("cleared context '" + contextId + "'");
        }
    }

    /**
     * Processes a clear context request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the clear context request.
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
        if (context == null) {
            LOGGER.error("Context '" + contextId
                    + "' refers to an unknown context");
            return;
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
        final ClearContextResponse response = new ClearContextResponse();
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Processes a clear context request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the clear context request.
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a clear context request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the clear context request.
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
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Processes a clear context request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the clear context request.
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
                LOGGER.warn("context '"
                        + contextId
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
     * Handles an extension notification.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param mmi
     *            the original incoming MMI message
     * @param ext
     *            the extension notification
     * @since 0.7.7
     */
    public void extensionInformation(final Object channel, final Mmi mmi,
            final ExtensionNotification ext) {
        final String contextId = ext.getContext();
        final String requestId = ext.getRequestId();
        LOGGER.info("received an extension notification for context "
                + contextId + " with request id " + requestId);
        try {
            final RecognitionResult result = extractor.getRecognitionResult(
                    mmi, ext);
            final MMIContext context = getContext(contextId);
            if (context == null) {
                LOGGER.error("no context with id '" + contextId
                        + "' known. Ignoring event");
                return;
            }
            final JVoiceXmlSession session = (JVoiceXmlSession) context
                    .getSession();
            final VoiceXmlInterpreterContext interpreterContext = session
                    .getVoiceXmlInterpreterContext();
            final EventBus bus = interpreterContext.getEventBus();
            final RecognitionEvent event = new RecognitionEvent(null,
                    session.getSessionId(), result);
            bus.publish(event);
        } catch (ConversionException e) {
            LOGGER.error("error parsing the recognition result", e);
        }
    }

    /**
     * Processes a new context request.
     * 
     * @param channel
     *            the channel that was used to send the request
     * @param request
     *            the new context request.
     */
    private void newContext(final Object channel,
            final NewContextRequest request) {
        String statusInfo = null;
        final String contextId = UUID.randomUUID().toString();
        request.setContext(contextId);
        final String requestId = request.getRequestId();
        LOGGER.info("received a new context request with request id "
                + requestId);
        try {
            getContext(request, true);
        } catch (MMIMessageException e) {
            LOGGER.error(e.getMessage(), e);
            statusInfo = e.getMessage();
        }
        final NewContextResponse response = new NewContextResponse();
        final String target = request.getSource();
        response.setTarget(target);
        response.setContext(contextId);
        response.setRequestId(requestId);
        if (statusInfo == null) {
            response.setStatus(StatusType.SUCCESS);
        } else {
            LOGGER.info("new context failed: " + statusInfo);
            response.setStatus(StatusType.FAILURE);
            response.addStatusInfo(statusInfo);
        }
        try {
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(response);
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            removeContext(contextId);
        }
    }

    /**
     * Stops accepting MMI lifecycle events.
     */
    public void stopAcceptingLifecycleEvents() {
        adapter.stop();
        LOGGER.info("stopped ETL protocol adapter " + adapter.getClass() + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionStarted(final Session session) {
    }

    /**
     * Tries to find the context for the given session.
     * 
     * @param session
     *            the session that maybe associated with an MMI context
     * @return found context, <code>null</code> if there is no context for that
     *         session
     */
    private MMIContext findContext(final Session session) {
        final SessionIdentifier sessionId = session.getSessionId();
        synchronized (contexts) {
            for (String contextId : contexts.keySet()) {
                final MMIContext context = contexts.get(contextId);
                final Session other = context.getSession();
                if (other != null) {
                    final SessionIdentifier otherSessionId =
                            other.getSessionId();
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
            LOGGER.warn("session " + session.getSessionId()
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
            final ErrorEvent error = session.getLastError();
            if (error == null) {
                done.setStatus(StatusType.SUCCESS);
                // TODO obtain the last result and add it to the status info
            } else {
                done.setStatus(StatusType.FAILURE);
                done.addStatusInfo(error.getMessage());
            }
        } catch (ErrorEvent e) {
            done.setStatus(StatusType.FAILURE);
            done.addStatusInfo(e.getMessage());
        }
        try {
            final Object channel = context.getChannel();
            final Mmi mmi = new Mmi();
            mmi.setLifeCycleEvent(done);
            adapter.sendMMIEvent(channel, mmi);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
