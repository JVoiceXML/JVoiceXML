/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.jvoicexml.Session;

/**
 * Session identifier factory for the {@link HttpSchemeStrategy}.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.7
 */
final class HttpClientSessionIdentifierFactory
        implements SessionIdentifierFactory<HttpClient> {
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
    public HttpClient createSessionIdentifier(final Session session) {
        final HttpClient client = new HttpClient();
        if (PROXY_HOST != null) {
            final HostConfiguration configuration =
                new HostConfiguration();
            configuration.setProxy(PROXY_HOST, PROXY_PORT);
            client.setHostConfiguration(configuration);
        }
        return client;
    }

}
