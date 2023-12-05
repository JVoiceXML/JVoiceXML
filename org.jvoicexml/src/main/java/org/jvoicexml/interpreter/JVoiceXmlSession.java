/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.net.URI;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SessionListener;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.jvxml.ExceptionWrapper;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.event.plain.implementation.NomatchEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.interpreter.datamodel.Connection;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedCollection;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.slf4j.MDC;

/**
 * Implementation of a {@link Session}.
 * 
 * <p>
 * Each session is started in a new thread with the session id as the name.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 */
public class JVoiceXmlSession extends Thread
        implements Session, EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(JVoiceXmlSession.class);

    /** The connection info that was used when connecting to JVoiceXML. */
    private final ConnectionInformation info;

    /** The VoiceXML interpreter context related to this session. */
    private VoiceXmlInterpreterContext context;

    /** Reference to the implementation platform. */
    private final ImplementationPlatform implementationPlatform;

    /** Reference to the document server. */
    private final DocumentServer documentServer;

    /** The application to process. */
    private Application application;

    /** The grammar processor. */
    private final GrammarProcessor grammarProcessor;

    /** The scope observer for this session. */
    private final ScopeObserver scopeObserver;

    /** The universal unique id for this session. */
    private final SessionIdentifier identifier;

    /** An error that occurred, while processing. */
    private ErrorEvent processingError;

    /** Flag, of this session is closed. */
    private boolean closed;

    /** Registered session listeners. */
    private final ScopedCollection<SessionListener> sessionListeners;

    /** Registered detailed session listeners. */
    private final Collection<DetailedSessionListener> detailedSessionListeners;

    /** The profile to use. */
    private final Profile profile;

    /** The configuration to use. */
    private final Configuration configuration;

    /** Semaphore to that is set while the session is running. */
    private final Object sem;

    /** The URI of the application to call. */
    private URI applicationUri;

    /**
     * Constructs a new object.
     * 
     * @param ip
     *            the implementation platform.
     * @param jvxml
     *            the main object to retrieve further resources.
     * @param connectionInformation
     *            the connection information to use
     * @param prof
     *            the profile
     * @param id
     *            the session identifier to use
     */
    public JVoiceXmlSession(final ImplementationPlatform ip,
            final JVoiceXmlCore jvxml,
            final ConnectionInformation connectionInformation,
            final Profile prof, final SessionIdentifier id) {
        // Create a unique session id
        identifier = id;
        // Store it in the MDC so that the session Id can be used by the loggers
        MDC.put("sessionId", identifier.getId());

        // Initialize this object
        info = connectionInformation;
        profile = prof;
        implementationPlatform = ip;
        documentServer = jvxml.getDocumentServer();
        application = null;
        grammarProcessor = jvxml.getGrammarProcessor();
        scopeObserver = new ScopeObserver();
        configuration = jvxml.getConfiguration();
        sem = new Object();
        closed = false;
        sessionListeners = new ScopedCollection<SessionListener>(scopeObserver);
        detailedSessionListeners =
                new java.util.ArrayList<DetailedSessionListener>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSessionListener(final SessionListener listener) {
        synchronized (sessionListeners) {
            sessionListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSessionListener(final SessionListener listener) {
        synchronized (sessionListeners) {
            sessionListeners.remove(listener);
        }
    }

    /**
     * Adds the given session listener to the list of registered session
     * listeners.
     * 
     * @param listener
     *            the listener to add
     */
    public void addSessionListener(final DetailedSessionListener listener) {
        synchronized (detailedSessionListeners) {
            detailedSessionListeners.add(listener);
        }
    }

    /**
     * Removes the given session listener from the list of registered session
     * listeners.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeSessionListener(final DetailedSessionListener listener) {
        synchronized (detailedSessionListeners) {
            detailedSessionListeners.remove(listener);
        }
    }

    /**
     * Retrieves the universal unique identifier for this session.
     * 
     * @return Universal unique identifier for this session.
     */
    public SessionIdentifier getSessionId() {
        return identifier;
    }

    /**
     * Retrieves the profile.
     * 
     * @return the profile
     * @since 0.7.7
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * {@inheritDoc}
     * 
     * Starts this session in a new thread.
     */
    public Application call(final URI uri) throws ErrorEvent {
        if (closed) {
            throw new NoresourceError("Session is already closed");
        }

        // Store the session Id in the MDC
        MDC.put("session Id", identifier.getId());

        // Some initialization stuff
        application = new JVoiceXmlApplication(scopeObserver);
        applicationUri = uri;
        final SessionIdentifier sessionId = getSessionId();
        setName(sessionId.getId());

        // Start processing of the given URI
        start();

        return application;
    }

    /**
     * {@inheritDoc}
     */
    public void hangup() {
        if (closed) {
            return;
        }

        // Generate a hangup event.
        LOGGER.info("initiating a hangup event");
        final EventBus eventbus = context.getEventBus();
        final JVoiceXMLEvent event = new ConnectionDisconnectHangupEvent();
        eventbus.publish(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DtmfInput getDtmfInput() throws NoresourceError,
            ConnectionDisconnectHangupEvent {
        if (closed) {
            throw new NoresourceError("Session is already closed");
        }

        return implementationPlatform.getCharacterInput();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getApplication() {
        return application;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitSessionEnd() throws ErrorEvent {
        // Do not wait, if there is already an error.
        if (processingError != null) {
            throw processingError;
        }
        LOGGER.info("waiting for end of session...");

        // Wait until the session ends.
        synchronized (sem) {
            try {
                if (!closed) {
                    sem.wait();
                }
            } catch (InterruptedException e) {
                throw new NoresourceError(
                        "waiting for end of session interrupted", e);
            }
        }
        LOGGER.info("...session ended");
        if (processingError != null) {
            throw processingError;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEnded() {
        return closed;
    }

    /**
     * Creates a new VoiceXML interpreter context.
     * 
     * @since 0.7.7
     */
    private void createContext() {
        // Create a new context.
        context = new VoiceXmlInterpreterContext(this, configuration);

        // Subscribe to the event bus.
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe(SynthesizedOutputEvent.EVENT_TYPE, this);
        eventbus.subscribe(SpokenInputEvent.EVENT_TYPE, this);
        eventbus.subscribe(NomatchEvent.EVENT_TYPE, this);

        // initialize the profile
        profile.initialize(context);
    }

    /**
     * {@inheritDoc}
     * 
     * Session working method.
     */
    @Override
    public void run() {
        LOGGER.info("starting session " + identifier.getId());
        if (LOGGER.isDebugEnabled()) {
            final Thread thread = Thread.currentThread();
            final ClassLoader loader = thread.getContextClassLoader();
            LOGGER.debug("using class loader " + loader);
        }
        processingError = null;
        createContext();

        try {
            final DocumentDescriptor descriptor = new DocumentDescriptor(
                    applicationUri, DocumentDescriptor.MIME_TYPE_XML);
            final VoiceXmlDocument doc = context.loadDocument(descriptor);
            final URI resolvedUri = descriptor.getUri();
            application.addDocument(resolvedUri, doc);
            logCalledApplication(resolvedUri);
        } catch (ErrorEvent e) {
            LOGGER.error("error processing application '" + application + "'",
                    e);
            processingError = e;
            cleanup();
            return;
        } catch (Throwable e) {
            LOGGER.error("error processing application '" + application + "'",
                    e);
            processingError = new ExceptionWrapper(e.getMessage(), e);
            cleanup();
            return;
        }

        processingError = null;
        createSessionVariables();
        context.setProperty("bargein", "true");
        notifySessionStarted();
        try {
            context.process(application);
        } catch (ErrorEvent e) {
            LOGGER.error("error processing application '" + application + "'",
                    e);
            processingError = e;
        } catch (Exception e) {
            LOGGER.error("error processing application '" + application + "'",
                    e);
            processingError = new ExceptionWrapper(e.getMessage(), e);
        } finally {
            cleanup();
        }
    }

    /**
     * Creates the session variables in the used {@link DataModel}.
     * 
     * @since 0.7.9
     */
    private void createSessionVariables() {
        final DataModel model = getDataModel();
        scopeObserver.enterScope(Scope.SESSION);
        final Connection connection = new Connection(info);
        int rc = model.createVariable("session.connection", connection,
                Scope.SESSION);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error creating session.connection variable: "
                    + model.errorCodeToString(rc));
        }
        rc = model.createVariable("session.sessionid", identifier.getId(),
                Scope.SESSION);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error creating session.sessionid variable: "
                    + model.errorCodeToString(rc));
        }
        
        /// Let the connection info know about the connection status
        final EventBus eventbus = context.getEventBus();
        eventbus.subscribe(ConnectionDisconnectHangupEvent.EVENT_TYPE,
                connection);

    }

    /**
     * Log some info that the application has been called.
     * 
     * @since 0.7.9
     */
    private void logCalledApplication(final URI uri) {
        final URI calledDevice;
        final URI callingDevice;
        final String protocolName;
        final String protocolVersion;
        if (info != null) {
            calledDevice = info.getCalledDevice();
            callingDevice = info.getCallingDevice();
            protocolName = info.getProtocolName();
            protocolVersion = info.getProtocolVersion();
            LOGGER.info("start processing application '" + application
                    + "' with document '" + uri + "' called from '" 
                    + callingDevice + "' to " + "'"
                    + calledDevice + "' using protocol '" + protocolName
                    + "' version '" + protocolVersion + "'...");
        } else {
            calledDevice = null;
            callingDevice = null;
            protocolName = null;
            protocolVersion = null;
            LOGGER.info("start processing application '" + application
                    + "'...");
        }
    }

    /**
     * Releases all acquired resources.
     * 
     * @since 0.7.5
     */
    private void cleanup() {
        LOGGER.info("finished processing application '" + application + "'");
        if (closed) {
            return;
        }
        closed = true;
        LOGGER.info("closing session...");

        profile.terminate(context);
        implementationPlatform.close();
        final SessionIdentifier sessionId = getSessionId();
        documentServer.sessionClosed(sessionId);
        scopeObserver.exitScope(Scope.SESSION);
        context.close();

        LOGGER.info("...session closed");
        notifySessionEnded();
    }

    /**
     * Notifies all session listeners that the session has started.
     * 
     * @since 0.7.3
     */
    private void notifySessionStarted() {
        synchronized (sessionListeners) {
            for (SessionListener listener : sessionListeners) {
                listener.sessionStarted(this);
            }
        }
        synchronized (detailedSessionListeners) {
            for (DetailedSessionListener listener : detailedSessionListeners) {
                listener.sessionStarted(this);
            }
        }
    }

    /**
     * Notifies all session listeners that the session has ended.
     * 
     * @since 0.7.3
     */
    private void notifySessionEnded() {
        // First: notify all listeners
        synchronized (sessionListeners) {
            for (SessionListener listener : sessionListeners) {
                listener.sessionEnded(this);
            }
        }

        synchronized (detailedSessionListeners) {
            for (DetailedSessionListener listener : detailedSessionListeners) {
                listener.sessionEnded(this);
            }
        }

        // Also notify the end of the session via the sem
        synchronized (sem) {
            sem.notifyAll();
        }
    }

    /**
     * Retrieves a reference to the used implementation platform.
     * 
     * @return The used implementation platform.
     */
    public ImplementationPlatform getImplementationPlatform() {
        return implementationPlatform;
    }

    /**
     * Retrieves a reference to the document server.
     * 
     * @return The document server.
     */
    public DocumentServer getDocumentServer() {
        return documentServer;
    }

    /**
     * Retrieves a reference to the grammar processor.
     * 
     * @return The grammar processor.
     * 
     * @since 0.3
     */
    public GrammarProcessor getGrammarProcessor() {
        return grammarProcessor;
    }

    /**
     * Retrieve the <code>VoiceXmlInterpreterContext</code> related to this
     * session.
     * 
     * @return The related context.
     */
    public VoiceXmlInterpreterContext getVoiceXmlInterpreterContext() {
        return context;
    }

    /**
     * Retrieves the scope observer for this session.
     * 
     * @return The scope observer.
     */
    public ScopeObserver getScopeObserver() {
        return scopeObserver;
    }

    /**
     * Retrieves the data model.
     * 
     * @return the data model
     * 
     * @since 0.7.7
     */
    public DataModel getDataModel() {
        return context.getDataModel();
    }

    /**
     * {@inheritDoc}
     */
    public ErrorEvent getLastError() {
        return processingError;
    }

    /**
     * {@inheritDoc} Notifies all detailed session listeners about relevant
     * thing happening on the {@link EventBus}.
     * 
     */
    @Override
    public void onEvent(final JVoiceXMLEvent event) {
        for (DetailedSessionListener listener : detailedSessionListeners) {
            listener.sessionEvent(this, event);
        }
    }
}
