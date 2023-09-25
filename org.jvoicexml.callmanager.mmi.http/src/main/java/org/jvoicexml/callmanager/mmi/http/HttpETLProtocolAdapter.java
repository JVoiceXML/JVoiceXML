/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.jvoicexml.callmanager.mmi.ETLProtocolAdapter;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.Mmi;

/**
 * A protocol adapter using the HTTP protocol.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class HttpETLProtocolAdapter implements ETLProtocolAdapter {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(HttpETLProtocolAdapter.class);
    /** The port number to listen on. */
    private int port;

    /** The server. */
    private Server server;

    /** The handler for incoming requests. */
    private final MmiHandler handler;

    /** The default proxy port. */
    private static final int DEFAULT_PROXY_PORT = 80;

    /** The name of the proxy to use. */
    private static final String PROXY_HOST;

    /** The port of the proxy server. */
    private static final int PROXY_PORT;

    static {
        PROXY_HOST = System.getProperty("http.proxyHost");
        final String port = System.getProperty("http.proxyPort");
        if (PROXY_HOST != null && port != null) {
            PROXY_PORT = Integer.parseInt(port);
        } else {
            PROXY_PORT = DEFAULT_PROXY_PORT;
        }
    }
    
    /**
     * Constructs a new object.
     */
    public HttpETLProtocolAdapter() {
        handler = new MmiHandler();
    }

    /**
     * sets the port number to listen on.
     * @param portNumber the port number
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws IOException {
        server = new Server(port);
        server.setHandler(handler);
        try {
            server.start();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        if (server == null) {
            return false;
        }
        return server.isStarted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addMMIEventListener(final MMIEventListener listener) {
        handler.addMMIEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMMIEventListener(final MMIEventListener listener) {
        handler.removeMMIEventListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMMIEvent(final Object channel, final Mmi mmi)
            throws IOException {
        try {
            final URI source = server.getURI();
            final LifeCycleEvent event = mmi.getLifeCycleEvent();
            event.setSource(source.toString());
            final String target = event.getTarget();
            if (target == null) {
                LOGGER.error("unable to send MMI event '" + mmi
                        + "'. No target.");
                return;
            }
            final JAXBContext ctx = JAXBContext.newInstance(Mmi.class);
            final Marshaller marshaller = ctx.createMarshaller();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(mmi, out);
            final URI uri = new URI(target);
            final HttpClientBuilder builder = HttpClientBuilder.create();
            if (PROXY_HOST != null) {
                HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
                builder.setProxy(proxy);
            }
            final CloseableHttpClient client = builder.build(); 
            final HttpPost post = new HttpPost(uri);
            final HttpEntity entity = new StringEntity(out.toString(),
                    ContentType.APPLICATION_XML);
            post.setEntity(entity);
            client.execute(post);
            LOGGER.info("sending " + mmi + " to '" + uri + "'");
        } catch (JAXBException e) {
            throw new IOException(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            server.stop();
        } catch (Exception ignore) {
        } finally {
            server = null;
        }
    }

}
