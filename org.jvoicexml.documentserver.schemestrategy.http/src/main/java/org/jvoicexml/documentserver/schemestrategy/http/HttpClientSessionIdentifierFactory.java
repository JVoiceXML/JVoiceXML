/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.http;

import org.apache.http.HttpHost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.schemestrategy.SessionIdentifierFactory;

/**
 * Session identifier factory for the {@link HttpSchemeStrategy}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
final class HttpClientSessionIdentifierFactory
        implements SessionIdentifierFactory<HttpClientBuilder> {
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
     * {@inheritDoc}
     */
    @Override
    public HttpClientBuilder createSessionIdentifier(
            final SessionIdentifier sessionId) {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        if (PROXY_HOST != null) {
            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            builder.setProxy(proxy);
        }
        return builder;
    }

}
