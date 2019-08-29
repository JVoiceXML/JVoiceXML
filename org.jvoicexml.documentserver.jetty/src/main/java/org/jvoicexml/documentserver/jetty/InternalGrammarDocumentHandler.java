package org.jvoicexml.documentserver.jetty;
/*
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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



import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * Jetty handler to server generated documents from the {@link DocumentStorage}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
class InternalGrammarDocumentHandler extends AbstractHandler  {
    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(InternalGrammarDocumentHandler.class);

    /** The context path of this handler. */
    public static String CONTEXT_PATH = "/grammars";
    
    /** Reference to the document storage. */
    private final DocumentStorage storage;

    /**
     * Constructs a new object.
     * 
     * @param documentStorage
     *            the document storage
     */
    public InternalGrammarDocumentHandler(final DocumentStorage documentStorage) {
        storage = documentStorage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        LOGGER.info("request from " + request.getRemoteAddr()
                + " to internal grammar handler");
        final String requestUri = request.getRequestURI();
        try {
            final URI uri = new URI(requestUri);
            final GrammarDocument document = storage.getDocument(uri);
            if (document == null) {
                LOGGER.warn("no document with URI '" + uri + "'");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            final GrammarType type = document.getMediaType();
            final String contentType = getContentType(type);
            if (contentType != null) {
                response.setContentType(contentType);
            }
            final byte[] buffer = document.getBuffer();
            if (buffer == null) {
                LOGGER.warn("no document found at '" + uri + "'");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            final OutputStream out = response.getOutputStream();
            out.write(buffer);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (URISyntaxException e) {
            LOGGER.warn("unabale to create request uri '" + requestUri + "'",
                    e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    /**
     * Determines the content type for the given grammar type.
     * @param type the current grammar type
     * @return content type for the grammar
     */
    private String getContentType(final GrammarType type) {
        if (type == GrammarType.SRGS_XML) {
            return "text/xml";
        } else if (type == GrammarType.SRGS_ABNF) {
            return "text/plain";
        } else if (type == GrammarType.GSL) {
            return "text/plain";
        } else {
            return null;
        }
    }
}
