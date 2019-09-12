/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.ReadBuffer;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.documentserver.schemestrategy.SessionIdentifierFactory;
import org.jvoicexml.documentserver.schemestrategy.SessionStorage;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * {@link SchemeStrategy} to read VoiceXML document via the HTTP protocol.
 *
 * <p>
 * This implementation uses the proxy settings that are delivered via the
 * environment variables <code>http.proxyHost</code> and
 * <code>http.proxyPort</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 */
public final class HttpSchemeStrategy implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(HttpSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String HTTP_SCHEME_NAME = "http";

    /** the storage of session identifiers. */
    protected static SessionStorage<HttpClientBuilder> SESSION_STORAGE;

    /** Scheme name for this strategy. */
    private String scheme;

    /** The default fetch timeout. */
    private int defaultFetchTimeout;

    static {
        final SessionIdentifierFactory<HttpClientBuilder> factory = new HttpClientSessionIdentifierFactory();
        SESSION_STORAGE = new SessionStorage<HttpClientBuilder>(factory);
    }

    /**
     * Construct a new object.
     */
    public HttpSchemeStrategy() {
        // Initialize with HTTP as default.
        scheme = HTTP_SCHEME_NAME;
    }

    /**
     * Sets the scheme for this strategy.
     * 
     * @param value
     *            the new scheme
     * @since 0.7.4
     */
    public void setScheme(final String value) {
        scheme = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScheme() {
        return scheme;
    }

    /**
     * Sets the default fetch timeout.
     * 
     * @param timeout
     *            the default fetch timeout.
     * @since 0.7
     */
    public void setFetchTimeout(final int timeout) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("default fetch timeout: " + timeout);
        }
        defaultFetchTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(final SessionIdentifier sessionId,
            final URI uri,
            final RequestMethod method, final long timeout,
            final Collection<KeyValuePair> parameters) throws BadFetchError {
        final HttpClientBuilder builder = SESSION_STORAGE
                .getSessionIdentifier(sessionId);
        final RequestConfig config = setTimeout(timeout);
        try (CloseableHttpClient client = builder
                .setDefaultRequestConfig(config).build()) {
            final String fragmentLessUriString = StringUtils.substringBeforeLast(uri.toString(), "#");
            final URI fragmentLessUri = new URI(fragmentLessUriString);
            final URI requestUri = addParameters(parameters, fragmentLessUri);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("connecting to '" + requestUri + "'...");
            }
            final HttpUriRequest request;
            if (method == RequestMethod.GET) {
                request = new HttpGet(requestUri);
            } else {
                request = new HttpPost(requestUri);
            }
            attachFiles(request, parameters);
            final HttpResponse response = client.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            final int status = statusLine.getStatusCode();
            if (status != HttpStatus.SC_OK) {
                final String reasonPhrase = statusLine.getReasonPhrase();
                LOGGER.error("error accessing '" + uri + "': " + reasonPhrase
                        + " (HTTP error code " + status + ")");
                return null;
            }
            final HttpEntity entity = response.getEntity();
            final InputStream input = entity.getContent();
            final ReadBuffer buffer = new ReadBuffer();
            buffer.read(input);
            return buffer.getInputStream();
        } catch (IOException | URISyntaxException | ParseException
                | SemanticError e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * Sets the timeout for the current connection.
     * 
     * @param timeout
     *            timeout as it is declared in the document.
     * @return created request config for the timeout
     * @since 0.7
     */
    private RequestConfig setTimeout(final long timeout) {
        final int usedTimeout;
        if (timeout != 0) {
            usedTimeout = (int) timeout;
        } else if (defaultFetchTimeout != 0) {
            usedTimeout = defaultFetchTimeout;
        } else {
            return RequestConfig.custom().build();

        }
        final RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(usedTimeout * 1000)
                .setConnectionRequestTimeout((int) (timeout * 1000))
                .setSocketTimeout((int) (timeout * 1000)).build();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("timeout set to '" + timeout + "'");
        }
        return config;
    }

    /**
     * Adds the parameters to the HTTP method.
     * 
     * @param parameters
     *            parameters to add
     * @param uri
     *            uri to add parameters to
     * @return URI with the given parameters
     * @throws URISyntaxException
     *             error creating a URI
     * @throws SemanticError
     *             error evaluating a parameter
     */
    private URI addParameters(final Collection<KeyValuePair> parameters,
            final URI uri) throws URISyntaxException, SemanticError {
        if ((parameters == null) || parameters.isEmpty()) {
            return uri;
        }
        final URIBuilder builder = new URIBuilder(uri);
        for (KeyValuePair current : parameters) {
            final Object value = current.getValue();
            if (!(value instanceof File)) {
                final String name = current.getKey();
                builder.addParameter(name, value.toString());
            }
        }

        return builder.build();
    }

    /**
     * Attach the files given in the parameters.
     * 
     * @param request
     *            the current request
     * @param parameters
     *            the parameters
     * @since 0.7.3
     */
    private void attachFiles(final HttpUriRequest request,
            final Collection<KeyValuePair> parameters) {
        if (!(request instanceof HttpPost)) {
            return;
        }
        final HttpPost post = (HttpPost) request;
        for (KeyValuePair current : parameters) {
            final Object value = current.getValue();
            if (value instanceof File) {
                final File file = (File) value;
                final FileEntity fileEntity = new FileEntity(file,
                        ContentType.create("binary/octet-stream"));
                post.setEntity(fileEntity);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(final SessionIdentifier sessionId) {
        SESSION_STORAGE.releaseSession(sessionId);
    }
}
