package org.jvoicexml.documentserver.jetty;
/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */


import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.DocumentRepository;

/**
 * An integrated web server for JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public final class JVoiceXmlWebServer implements DocumentRepository {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(JVoiceXmlWebServer.class);

    /** The integrated web server. */
    private Server server;

    /** Port of the document storage. */
    private int storagePort;
    
    private final Collection<ContextHandlerProvider> providers;
    
    /**
     * Constructs a new web server with the default port {@code 9595}.
     */
    public JVoiceXmlWebServer() {
        storagePort = 9595;
        providers = new java.util.ArrayList<ContextHandlerProvider>();
    }
    
    
    /**
     * Sets the storage port.
     * 
     * @param port
     *            port number for the integrated web server
     */
    public void setStoragePort(final int port) {
        storagePort = port;
    }

    /**
     * Sets the context handler providers.
     * @param contextHandlerProviders
     */
    public void setContextHandlerProviders(
            List<ContextHandlerProvider> contextHandlerProviders) {
        providers.addAll(contextHandlerProviders);
    }

    /**
     * Retrieves the document storage.
     * @return the document storag, {@code null} if none is available
     */
    public DocumentStorage getDocumentStorage() {
        for (ContextHandlerProvider provider : providers) {
            if (provider instanceof DocumentStorage) {
                return (DocumentStorage) provider;
            }
        }
        return null;
    }
    
    /**
     * Starts the web server. 
     * 
     * @throws Exception
     *             error starting the web server
     */
    public void start() throws Exception {
        if (storagePort < 0) {
            return;
        }
        server = new Server(storagePort);
        final Collection<ContextHandler> contextHandlers =
                addContextHandlers();
        ContextHandler[] handlers = new ContextHandler[contextHandlers.size()];
        handlers = contextHandlers.toArray(handlers);
        final ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(handlers);
        server.setHandler(contexts);
        try {
            server.start();
        } catch (BindException e) {
            LOGGER.error("Unable to start the JVoiceXML internal web server.");
            LOGGER.error("Is another JVoiceXML instance running?");
            throw e;
        }
        populateServerUri();
        LOGGER.info("JVoiceXML internal web server started on port "
                + storagePort);
    }

    /**
     * Register the context handlers from the providers
     * @return created context handlers.
     */
    private Collection<ContextHandler> addContextHandlers() {
        final Collection<ContextHandler> handlers =
                new java.util.ArrayList<ContextHandler>();
        for (ContextHandlerProvider provider : providers) {
            final Collection<ContextHandler> providedHandlers =
                    provider.getContextHandlers();
            for (ContextHandler handler : providedHandlers) {
                handler.setUsingSecurityManager(false);
                handlers.add(handler);
                LOGGER.debug("adding handler for path "
                        + handler.getContextPath());
            }
        }
        return handlers;
    }

    /**
     * Forwards the server URI to the context providers.
     */
    private void populateServerUri() {
        final URI uri = server.getURI();
        for (ContextHandlerProvider provider : providers) {
            provider.setServerUri(uri);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI addGrammarDocument(final SessionIdentifier sessionId,
            final GrammarDocument document)
            throws URISyntaxException {
        final DocumentStorage storage = getDocumentStorage();
        if (storage == null) {
            return null;
        }
        return storage.addGrammarDocument(sessionId, document);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolveBuiltinUri(final URI uri) {
        final DocumentStorage storage = getDocumentStorage();
        if (storage == null) {
            return uri;
        }
        return storage.resolveBuiltinUri(uri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(SessionIdentifier sessionId) {
        final DocumentStorage storage = getDocumentStorage();
        if (storage == null) {
            return;
        }
        try {
            storage.clear(sessionId);
        } catch (URISyntaxException e) {
            LOGGER.warn("error clearing session", e);
        }
    }
    

    /**
     * Stops the web server..
     * 
     * @throws Exception
     *             error closing the web server
     */
    public void stop() throws Exception {
        if (storagePort < 0) {
            return;
        }
        server.stop();
        LOGGER.info("JVoiceXML internal web server stopped");
    }
}
