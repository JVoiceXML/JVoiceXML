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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
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
    private int defaultFetchTimeout;

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
    public void setFetchTimeout(final int timeout) {
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
        final URI fullUri;
        try {
            fullUri = addParameters(parameters, uri);
        } catch (URISyntaxException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        final String url = fullUri.toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + url + "'...");
        }

        final HttpUriRequest request;
        if (method == RequestMethod.GET) {
            request = new HttpGet(url);
        } else {
            request = new HttpPost(url);
        }
        attachFiles(request, parameters);
        try {
            final HttpParams params = client.getParams();
            setTimeout(timeout, params);
            final HttpResponse response = client.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            final int status = statusLine.getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new BadFetchError(statusLine.getReasonPhrase());
            }
            final HttpEntity entity = response.getEntity();
            return entity.getContent();
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
            final HttpParams params) {
        if (timeout != 0) {
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                    new Integer((int) timeout));
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("timeout set to '" + timeout + "'");
            }
        } else if (defaultFetchTimeout != 0) {
            params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                   defaultFetchTimeout);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("timeout set to default '"
                        + defaultFetchTimeout + "'");
            }
        }
    }

    /**
     * Adds the parameters to the HTTP method.
     * @param parameters parameters to add
     * @param uri the given URI
     * @return URI with the given parameters
     * @throws URISyntaxException
     *         error creating a URI 
     */
    private URI addParameters(final Map<String, Object> parameters,
            final URI uri) throws URISyntaxException {
        if ((parameters == null) || parameters.isEmpty()) {
            return uri;
        }
        final ArrayList<NameValuePair> queryParameters =
            new ArrayList<NameValuePair>();
        final Collection<String> parameterNames = parameters.keySet();
        for (String name : parameterNames) {
            final Object value = parameters.get(name);
            if (!(value instanceof File)) {
                final NameValuePair pair = new BasicNameValuePair(name,
                        value.toString());
                queryParameters.add(pair);
            }
        }

        final Collection<NameValuePair> parameterList =
            URLEncodedUtils.parse(uri, encoding);
        queryParameters.addAll(parameterList);

        final String query = URLEncodedUtils.format(queryParameters, encoding);
        return URIUtils.createURI(uri.getScheme(), uri.getHost(), uri.getPort(),
                uri.getPath(), query, uri.getFragment());
    }

    /**
     * Attach the files given in the parameters.
     * @param request the current request
     * @param parameters the parameters
     * @since 0.7.3
     */
    private void attachFiles(final HttpUriRequest request,
            final Map<String, Object> parameters) {
        if (!(request instanceof HttpPost)) {
            return;
        }
        final HttpPost post = (HttpPost) request;
        final Collection<String> parameterNames = parameters.keySet();
        for (String name : parameterNames) {
            final Object value = parameters.get(name);
            if (value instanceof File) {
                final File file = (File) value;
                final FileEntity fileEntity = new FileEntity(file,
                        "binary/octet-stream");
                post.setEntity(fileEntity);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(final Session session) {
        SESSION_STORAGE.releaseSession(session);
    }
}
