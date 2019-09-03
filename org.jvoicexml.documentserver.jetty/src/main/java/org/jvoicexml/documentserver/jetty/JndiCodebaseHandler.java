package org.jvoicexml.documentserver.jetty;
/*
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 * A handler for RMI dynamic code download.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JndiCodebaseHandler extends AbstractHandler
    implements ContextHandlerProvider{
    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(JndiCodebaseHandler.class);

    /** The context path of this handler. */
    public static String CONTEXT_PATH = "/jndi";

    /** The class loader to load the requested class files. */
    private final ClassLoader loader;
    
    /**
     * Constructs a new object.
     */
    public JndiCodebaseHandler() {
        loader = getClass().getClassLoader();
        LOGGER.info("using JNDI class loader '" + loader + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(final String target, final Request baseRequest,
            final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, ServletException {
        final String requestUri = request.getRequestURI();
        final String path = getPath(requestUri);
        if (path == null) {
            LOGGER.warn("unable to extract Java path from '" + requestUri
                    + "'");
            baseRequest.setHandled(true);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.info("Loading '" + path + "'");
        }
        final OutputStream out = response.getOutputStream();
        writeClassToOutputStream(path, out);
        baseRequest.setHandled(true);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Returns the path to the class file obtained from parsing the request URI.
     * @param request URI
     */
    private String getPath(final String requestUri) {
        if (requestUri.length() < CONTEXT_PATH.length() + 2) {
            return null;
        }
        // Classloaders start searching from the root, so the initial slash
        // should not be needed.
        return requestUri.substring(CONTEXT_PATH.length() + 1);
    }

    /**
     * Retrieves an input stream to read the resource at the given path.
     * @param path the path of the resource to load
     * @return associated input stream, {@code null} if the resource cannot be
     * loaded.
     */
    private InputStream getInputStream(final String path) {
        InputStream in = loader.getResourceAsStream(path);
        if (in == null) {
            in = loader.getResourceAsStream("/" + path);
        }
        if (in == null) {
            in = getClass().getResourceAsStream("/" + path);
        }
        return in;
    }
    
    /**
     * Writes the class to the output stream.
     * @param path path of the class to load
     * @param out output stream to write to
     * @throws IOException
     *         the class could not be written
     * @since 0.7.9
     */
    private void writeClassToOutputStream(final String path,
            final OutputStream out) throws IOException {
        final InputStream in2 = getInputStream("org/jvoicexml/Application.class");
        LOGGER.info("org/jvoicexml/Application.class: " + in2);
        final InputStream in3 = getInputStream("org/jvoicexml/documentserver/jetty/DocumentStorage.class");
        LOGGER.info("org/jvoicexml/documentserver/jetty/DocumentStorage.class:" + in3);
        final InputStream in = getInputStream(path);
        if (in == null) {
            LOGGER.info("unable to load '" + path + "' from " + loader);
            throw new IOException("unable to load '" + path + "'");
        }
        final byte[] bytes = new byte[1024];
        int read = 0;;
        do
        {
            read = in.read(bytes);
            if (read > 0) {
                out.write(bytes, 0, read);
            }
        } while (read > 0);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ContextHandler> getContextHandlers() {
        final Collection<ContextHandler> handlers =
                new java.util.ArrayList<ContextHandler>();
        ContextHandler context = new ContextHandler(CONTEXT_PATH);
        context.setHandler(this);
        handlers.add(context);
        return handlers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerUri(final URI uri) {
    }
}
