package org.jvoicexml.documentserver.jetty;
/*
 * Copyright (C) 2015-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jvoicexml.documentserver.schemestrategy.builtin.GrammarCreator;
import org.jvoicexml.event.error.BadFetchError;

/**
 * A handler for builtin grammars.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class BuiltinGrammarHandler extends AbstractHandler {
    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(BuiltinGrammarHandler.class);

    /** The context path of this handler. */
    public static String CONTEXT_PATH = "/builtin";

    /** Known grammar creators. */
    private final Map<String, GrammarCreator> creators;

    /**
     * Adds the specified grammar creators to the list of known grammar
     * creators.
     * 
     * @param col
     *            the creators to add
     */
    public void setGrammarCreators(final Collection<GrammarCreator> col) {
        for (GrammarCreator creator : col) {
            addGrammarCreator(creator);
        }
    }

    /**
     * Adds the specified grammar creator to the list of known grammar creators.
     * 
     * @param creator
     *            the creator to add
     */
    public void addGrammarCreator(final GrammarCreator creator) {
        final String type = creator.getTypeName();
        creators.put(type, creator);
        LOGGER.info("added builtin grammar creator '" + creator.getClass()
                + "' for type '" + type + "'");
    }

    /**
     * Constructs a new object.
     */
    public BuiltinGrammarHandler() {
        creators = new java.util.HashMap<String, GrammarCreator>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        LOGGER.info("request from " + request.getRemoteAddr()
                + " to builtin grammar handler");
        final String requestUri = request.getRequestURI();
        final String builtintUri = requestUri.substring(CONTEXT_PATH.length() + 1);
        final String type = extractBuiltinType(builtintUri);
        final GrammarCreator creator = creators.get(type);
        if (creator == null) {
            LOGGER.warn("builtin type '" + type + "' is not supported!");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            final URI uri = new URI("builtin:" + builtintUri);
            final byte[] bytes = creator.createGrammar(uri);
            final OutputStream out = response.getOutputStream();
            out.write(bytes);
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (URISyntaxException | BadFetchError e) {
            LOGGER.warn("invalid uri '" + builtintUri + "'");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        
    }

    /**
     * Extracts the builtin type from the URI.
     * 
     * @param uri
     *            the given URI.
     * @return extracted builtin type
     */
    private String extractBuiltinType(final String requestUri) {
        final String[] path = requestUri.split("/");
        String type = path[1];
        final int pos = type.indexOf('?');
        if (pos >= 0) {
            type = type.substring(0, pos);
        }
        return type.toLowerCase();
    }

}
