/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/JVoiceXmlSession.java $
 * Version: $LastChangedRevision: 2910 $
 * Date:    $LastChangedDate: 2012-01-27 14:07:32 -0600 (vie, 27 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.jvoicexml.Application;
import org.jvoicexml.CallControl;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SessionListener;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.jvxml.ExceptionWrapper;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.interpreter.scope.ScopedCollection;
import org.jvoicexml.interpreter.variables.SessionShadowVarContainer;
import org.jvoicexml.interpreter.variables.VariableProviders;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.mozilla.javascript.ScriptableObject;

/**
 * Implementation of a {@link Session}.
 *
 * <p>
 * Each session is started in a new thread with the session id as the
 * name.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2910 $
 */
public final class JVoiceXmlSession
    extends Thread
        implements Session {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlSession.class);

    /** The connection info that was used when connecting to JVoiceXML. */
    private final ConnectionInformation info;

    /** The VoiceXML interpreter context related to this session. */
    private final VoiceXmlInterpreterContext context;

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
    private final UUID uuid;

    /** An error that occurred, while processing. */
    private ErrorEvent processingError;

    /** Flag, of this session is closed. */
    private boolean closed;

    /** Registered session listeners. */
    private final ScopedCollection<SessionListener> sessionListeners;

    /**
     * Semaphore to that is set while the session is running.
     */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     *
     * @param ip
     *        the implementation platform.
     * @param jvxml
     *        the main object to retrieve further resources.
     * @param connectionInformation
     *        the connection information to use
     */
    public JVoiceXmlSession(final ImplementationPlatform ip,
            final JVoiceXmlCore jvxml,
            final ConnectionInformation connectionInformation) {
        // Create a unique session id
        uuid = UUID.randomUUID();
        // Store it in the MDC
        MDC.put("sessionId", uuid.toString());

        // Initialize this object
        info = connectionInformation;
        implementationPlatform = ip;
        documentServer = jvxml.getDocumentServer();
        application = null;
        grammarProcessor = jvxml.getGrammarProcessor();
        scopeObserver = new ScopeObserver();
        final Configuration configuration = jvxml.getConfiguration();
        context = new VoiceXmlInterpreterContext(this, configuration);
        sem = new Semaphore(1);
        closed = false;
        sessionListeners = new ScopedCollection<SessionListener>(scopeObserver);
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
     * Retrieves the universal unique identifier for this session.
     * @return Universal unique identifier for this session.
     */
    public String getSessionID() {
        return uuid.toString();
    }

    /**
     * {@inheritDoc}
     *
     * Starts this session in a new thread.
     */
    public void call(final URI uri)
            throws ErrorEvent {
        if (closed) {
            throw new NoresourceError("Session is already closed");
        }

        // Store the session Id in the MDC
        MDC.put("sessionId", uuid.toString());

        try {
            sem.acquire();
            application = new JVoiceXmlApplication(scopeObserver);
            final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
            final VoiceXmlDocument doc = context.loadDocument(descriptor);
            final URI resolvedUri = descriptor.getUri();
            application.addDocument(resolvedUri, doc);

            final String sessionId = getSessionID();
            setName(sessionId);

            start();
        } catch (InterruptedException ie) {
            throw new NoresourceError("error acquiring session semaphore", ie);
        } catch (ErrorEvent e) {
            LOGGER.error("error while calling '" + uri + "'", e);
            cleanup();
            throw e;
        }
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
        final EventHandler handler = context.getEventHandler();
        final JVoiceXMLEvent event = new ConnectionDisconnectHangupEvent();
        handler.notifyEvent(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacterInput getCharacterInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        if (closed) {
            throw new NoresourceError("Session is already closed");
        }

        return implementationPlatform.getCharacterInput();
    }

    /**
     * {@inheritDoc}
     */
    public void waitSessionEnd()
            throws ErrorEvent {
        if (closed) {
            throw new NoresourceError("Session is already closed");
        }

        LOGGER.info("waiting for end of session...");

        // Do not wait, if there is already an error.
        if (processingError != null) {
            throw processingError;
        }

        // Wait until the session ends.
        try {
            sem.acquire();
        } catch (InterruptedException ie) {
            throw new NoresourceError("error acquiring session semaphore", ie);
        } finally {
            sem.release();
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
     * {@inheritDoc}
     *
     * Session working method.
     */
    @Override
    public void run() {
        // Store the session Id in the MDC
        MDC.put("sessionId", uuid.toString());

        final URI calledDevice;
        final URI callingDevice;
        final String protocolName;
        final String protocolVersion;
        if (info != null) {
            calledDevice = info.getCalledDevice();
            callingDevice = info.getCallingDevice();
            protocolName = info.getProtocolName();
            protocolVersion = info.getProtocolVersion();
            LOGGER.info("start processing application '"
                    + application + "' called from '" + callingDevice + "' to "
                    + "'" + calledDevice + "' using protocol '"
                    + protocolName + "' version '" + protocolVersion + "'...");
        } else {
            calledDevice = null;
            callingDevice = null;
            protocolName = null;
            protocolVersion = null;
            LOGGER.info("start processing application '"
                    + application + "'...");
        }

                
        processingError = null;
        final ScriptingEngine scripting = getScriptingEngine();
        scopeObserver.enterScope(Scope.SESSION);
        try {
            final SessionShadowVarContainer session =
                scripting.createHostObject(
                        SessionShadowVarContainer.VARIABLE_NAME,
                        SessionShadowVarContainer.class);
            session.setLocalCallerDevice(calledDevice);
            session.setRemoteCallerDevice(callingDevice);
            session.protocol(protocolName, protocolVersion);
            session.setSessionIdentifier(uuid);
            createHostObjects();
            notifySessionStarted();
            context.process(application);
        } catch (ErrorEvent e) {
            LOGGER.error("error processing application '"
                    + application + "'", e);

            processingError = e;
        } catch (Exception e) {
            LOGGER.error("error processing application '"
                    + application + "'", e);
            processingError = new ExceptionWrapper(e.getMessage(), e);
            final CallControl call;
            try {
                call = implementationPlatform.getCallControl();
                call.hangup();
            } catch (NoresourceError ignore) {
            } catch (ConnectionDisconnectHangupEvent ignore) {
            }
        } finally {
            cleanup();
        }
    }

    /**
     * Releases all acquired resources.
     * @since 0.7.5
     */
    private void cleanup() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("finished processing application '"
                    + application + "'");
        }
        if (closed) {
            return;
        }
        closed = true;
        LOGGER.info("closing session...");

        implementationPlatform.close();
        final String sessionId = getSessionID();
        documentServer.sessionClosed(sessionId);
        scopeObserver.exitScope(Scope.SESSION);
        context.close();

        LOGGER.info("...session closed");
        notifySessionEnded();
        sem.release();
    }

    /**
     * Creates custom host objects.
     * @exception ConfigurationException
     *         error loading a configuration
     * @exception SemanticError
     *         error creating a host object
     * @since 0.7.5
     */
    private void createHostObjects()
        throws ConfigurationException, SemanticError {
        final Configuration configuration = context.getConfiguration();
        final ScriptingEngine scripting = getScriptingEngine();
        final Collection<VariableProviders> providers =
            configuration.loadObjects(VariableProviders.class,
                    "variableprovider");
        for (VariableProviders provider : providers) {
            final Collection<ScriptableObject> created =
                provider.createHostObjects(scripting, Scope.SESSION);
            for (ScriptableObject o : created) {
                if (o instanceof SessionListener) {
                    final SessionListener listener =
                        (SessionListener) o;
                    sessionListeners.add(listener);
                }
            }
        }
    }

    /**
     * Notifies all session listeners that the session has ended.
     * 
     * @since 0.7.3
     */
    private void notifySessionStarted() {
        final Collection<SessionListener> listeners;
        synchronized (sessionListeners) {
            listeners =
                new java.util.ArrayList<SessionListener>(sessionListeners);
        }
        for (SessionListener listener : listeners) {
            listener.sessionStarted(this);
        }
    }

    /**
     * Notifies all session listeners that the session has ended.
     * 
     * @since 0.7.3
     */
    private void notifySessionEnded() {
        final Collection<SessionListener> listeners;
        synchronized (sessionListeners) {
            listeners =
                new java.util.ArrayList<SessionListener>(sessionListeners);
        }
        for (SessionListener listener : listeners) {
            listener.sessionEnded(this);
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
     * @return The scope observer.
     */
    public ScopeObserver getScopeObserver() {
        return scopeObserver;
    }

    /**
     * Retrieves the scripting engine.
     * @return The scripting engine.
     *
     * @since 0.4
     */
    public ScriptingEngine getScriptingEngine() {
        return context.getScriptingEngine();
    }

    /**
     * {@inheritDoc}
     */
    public ErrorEvent getLastError() {
        return processingError;
    }
}
