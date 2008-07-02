/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Implementation of a <code>Session</code>.
 *
 * <p>
 * Each session is started in a new thread with the session Id as the
 * name.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlSession
    extends Thread
        implements Session {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlSession.class);

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

    /**
     * Semaphore to that is set while the session is running.
     */
    private final Semaphore sem;

    /**
     * Constructs a new object.
     *
     * @param ip
     *        The implementation platform.
     * @param jvxml
     *        The main object to retrieve further resources.
     */
    public JVoiceXmlSession(final ImplementationPlatform ip,
            final JVoiceXmlCore jvxml) {
        uuid = UUID.randomUUID();

        implementationPlatform = ip;
        documentServer = jvxml.getDocumentServer();
        application = null;
        grammarProcessor = jvxml.getGrammarProcessor();
        scopeObserver = new ScopeObserver();

        context = new VoiceXmlInterpreterContext(this);

        sem = new Semaphore(1);

        closed = false;
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

        try {
            sem.acquire();
        } catch (InterruptedException ie) {
            throw new NoresourceError("error acquiring session semaphore", ie);
        }

        application = new JVoiceXmlApplication(scopeObserver);
        final FetchAttributes attributes = application.getFetchAttributes();
        final VoiceXmlDocument doc =
                context.acquireVoiceXmlDocument(uri, attributes);
        application.addDocument(uri, doc);

        final String sessionId = getSessionID();
        setName(sessionId);

        start();
    }

    /**
     * {@inheritDoc}
     */
    public void hangup() {
        if (closed) {
            return;
        }

        closed = true;
        LOGGER.info("closing session...");

        implementationPlatform.close();
        documentServer.sessionClosed(this);
        context.close();

        LOGGER.info("...session closed");
    }

    /**
     * {@inheritDoc}
     */
    public CharacterInput getCharacterInput()
            throws NoresourceError {
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

        try {
            sem.acquire();
        } catch (InterruptedException ie) {
            throw new NoresourceError("error acquiring session semaphore", ie);
        }

        sem.release();

        LOGGER.info("...session ended");

        if (processingError != null) {
            throw processingError;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Session working method.
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start processing application '"
                    + application + "'...");
        }

        processingError = null;

        try {
            context.process(application);
        } catch (ErrorEvent ee) {
            LOGGER.error("error processing application '"
                    + application + "'", ee);

            processingError = ee;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("finished processing application '"
                         + application + "'");
        }

        sem.release();
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
}
