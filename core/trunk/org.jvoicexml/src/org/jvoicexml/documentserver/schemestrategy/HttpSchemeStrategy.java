/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.EncodingUtil;
import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 *{@link SchemeStrategy} to read VoiceXML document via the HTTP protocol.
 *
 * <p>
 * This implementation uses the proxy settings that are delivered via the
 * environment variables <code>http.proxyHost</code> and
 * <code>http.proxyPort</code>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class HttpSchemeStrategy
        implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(HttpSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "http";

    /** the storage of session identifiers. */
    private static final SessionStorage<HttpClient> SESSION_STORAGE;

    /** Encoding that should be used to encode/decode URLs. */
    private static String encoding =
        System.getProperty("jvoicexml.xml.encoding", "UTF-8");

    /** The default fetch timeout. */
    private long defaultFetchTimeout;

    static {
        final SessionIdentifierFactory<HttpClient> factory =
            new HttpClientSessionIdentifierFactory();
        SESSION_STORAGE = new SessionStorage<HttpClient>(factory);
    }

    /**
     * Construct a new object.
     */
    public HttpSchemeStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public String getScheme() {
        return SCHEME_NAME;
    }

    /**
     * Sets the default fetch timeout.
     * @param timeout the default fetch timeout.
     * @since 0.7
     */
    public void setFetchTimeout(final long timeout) {
        defaultFetchTimeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(final Session session, final URI uri,
            final RequestMethod method, final long timeout,
            final Map<String, Object> parameters)
            throws BadFetchError {
        final HttpClient client = SESSION_STORAGE.getSessionIdentifier(session);
        final String url = uri.toString();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + url + "'...");
        }

        final HttpMethod httpMethod;
        if (method == RequestMethod.GET) {
            httpMethod = new GetMethod(url);
        } else {
            httpMethod = new PostMethod(url);
        }
        addParameters(parameters, httpMethod);
        int status;
        try {
            final HttpConnectionManager manager =
                client.getHttpConnectionManager();
            final HttpConnectionManagerParams params =
                manager.getParams();
            setTimeout(timeout, params);
            status = client.executeMethod(httpMethod);
            if (status != HttpStatus.SC_OK) {
                throw new BadFetchError(httpMethod.getStatusText());
            }
            return httpMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * Sets the timeout for the current connection.
     * @param timeout timeout as it is declared in the document.
     * @param params connection parameters.
     * @since 0.7
     */
    private void setTimeout(final long timeout,
            final HttpConnectionManagerParams params) {
        if (timeout != 0) {
            params.setSoTimeout((int) timeout);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("timeout set to '" + timeout + "'");
            }
        } else if (defaultFetchTimeout != 0) {
            params.setSoTimeout((int) defaultFetchTimeout);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("timeout set to default '"
                        + defaultFetchTimeout + "'");
            }
        }
    }

    /**
     * Adds the parameters to the HTTP method.
     * @param parameters parameters to add
     * @param httpMethod method where to set the parameters.
     * @exception BadFetchError
     *            error loading a file for post
     */
    private void addParameters(final Map<String, Object> parameters,
            final HttpMethod httpMethod) throws BadFetchError {
        if (parameters == null) {
            return;
        }
        final boolean isPost = httpMethod instanceof PostMethod;
        final ArrayList<NameValuePair> queryParameters =
            new ArrayList<NameValuePair>();
        final ArrayList<Part> parts = new ArrayList<Part>();
        final Collection<String> parameterNames = parameters.keySet();
        for (String name : parameterNames) {
            final Object value = parameters.get(name);
            if ((value instanceof File) && isPost) {
                final File file = (File) value;
                Part part;
                try {
                    part = new FilePart(file.toURI().toString(),
                            file);
                } catch (FileNotFoundException e) {
                    throw new BadFetchError(e.getMessage(), e);
                }
                parts.add(part);
            } else {
                final NameValuePair pair = new NameValuePair(name,
                        value.toString());
                queryParameters.add(pair);
            }
        }
        NameValuePair[] query = new NameValuePair[queryParameters.size()];
        query = queryParameters.toArray(query);
        httpMethod.setQueryString(EncodingUtil.formUrlEncode(query, encoding));
        if (isPost && !parts.isEmpty()) {
            final PostMethod post = (PostMethod) httpMethod;
            Part[] queryParts = new Part[parts.size()];
            queryParts = parts.toArray(queryParts);
            final RequestEntity entity =
                new MultipartRequestEntity(queryParts, httpMethod.getParams());
            post.setRequestEntity(entity);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(final Session session) {
        SESSION_STORAGE.releaseSession(session);
    }
}
