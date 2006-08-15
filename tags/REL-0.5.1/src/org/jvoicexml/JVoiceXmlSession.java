/*
 * File:    $RCSfile: JVoiceXmlSession.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.net.URI;
import java.util.UUID;

import org.jvoicexml.documentserver.DocumentServer;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CharacterInput;
import org.jvoicexml.implementation.ImplementationPlatform;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.ScopeObserver;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Implementation of a <code>Session</code>.
 *
 * <p>
 * Each session is started in a new thread.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlSession
        implements Session, Runnable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlSession.class);

    /** The VoiceXML interpreter context related to this session. */
    private final VoiceXmlInterpreterContext context;

    /** Reference to the implementation platform. */
    private final ImplementationPlatform implementationPlatform;

    /** Reference to the docment server. */
    private final DocumentServer documentServer;

    /** The application to process within this session. */
    private final Application application;

    /** The grammar procesor. */
    private final GrammarProcessor grammarProcessor;

    /** The scope observer for this session. */
    private final ScopeObserver scopeObserver;

    /** The universal unique id for this session. */
    private final UUID uuid;

    /** The variable container. */
    private ScriptingEngine scripting;

    /** This session's thread. */
    private Thread thread;

    /** An error that occured, while processing. */
    private ErrorEvent processingError;

    /**
     * Constructs a new opbject.
     *
     * @param ip
     *        The implementation platform.
     * @param app
     *        The application to process.
     * @param jvxml
     *        The main object to retrieve further resources.
     */
    JVoiceXmlSession(final ImplementationPlatform ip, final Application app,
                     final JVoiceXmlCore jvxml) {
        uuid = UUID.randomUUID();

        implementationPlatform = ip;
        documentServer = jvxml.getDocumentServer();
        application = app;
        grammarProcessor = jvxml.getGrammarProcessor();
        scopeObserver = new ScopeObserver();

        context = new VoiceXmlInterpreterContext(this);
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
    public void call()
            throws ErrorEvent {
        thread = new Thread(this);
        thread.setName(getSessionID());

        thread.start();
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this method.
     */
    public void hangup() {
        LOGGER.warn("not implemented yet.");
    }

    /**
     * {@inheritDoc}
     */
    public CharacterInput getCharacterInput()
            throws NoresourceError {
        return implementationPlatform.getCharacterInput();
    }

    /**
     * {@inheritDoc}
     */
    public void waitSessionEnd()
            throws ErrorEvent {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("waiting for end of session...");
        }

        synchronized (thread) {
            // Do not wait, if there is already an error.
            if (processingError != null) {
                throw processingError;
            }

            try {
                thread.wait();
            } catch (InterruptedException ie) {
                LOGGER.error("error waiting for end of session", ie);
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...session ended");
        }

        if (processingError != null) {
            throw processingError;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        context.close();
        implementationPlatform.close();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("session closed");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Starts this session in a new thread.
     */
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("start processing application '" + application.getId()
                         + "'...");
        }

        processingError = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating scripting engine...");
        }

        scripting = new org.jvoicexml.interpreter.variables.
                    RhinoScriptingEngine(context);

        final URI uri = application.getUri();
        try {
            context.process(uri);
        } catch (ErrorEvent ee) {
            LOGGER.error("error processing application '" + application.getId()
                         + "'", ee);

            processingError = ee;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("finished processing application '"
                         + application.getId() + "'");
        }

        synchronized (thread) {
            thread.notify();
        }
    }

    /**
     * Retrieves a reference to the used imlementation platform.
     *
     * @return The used imlementation platform.
     */
    public ImplementationPlatform getImplementationPlatform() {
        return implementationPlatform;
    }

    /**
     * Retrieves a reference tothe document server.
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
     * Retreive the scope observer for this session.
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
        return scripting;
    }

}
