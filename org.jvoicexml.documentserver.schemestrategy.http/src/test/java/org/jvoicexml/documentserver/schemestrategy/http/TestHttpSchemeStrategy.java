/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifer;
import org.jvoicexml.documentserver.schemestrategy.SessionStorage;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test cases for {@link HttpSchemeStrategy}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.3
 */
@RunWith(MockitoJUnitRunner.class)
public final class TestHttpSchemeStrategy {

    private static final SessionIdentifier SESSION_ID =
            new UuidSessionIdentifer();
    private static final String BASE_URL = "http://example.com:8080";
    private static final long TIMEOUT = 0;

    @Mock private SessionStorage<HttpClientBuilder> sessionStorage;
    @Mock private HttpClientBuilder httpClientBuilder;
    @Mock private CloseableHttpClient httpClient;
    @Captor private ArgumentCaptor<HttpUriRequest> httpRequestCaptor;

    /**
     * Class under test
     */
    private final HttpSchemeStrategy strategy = new HttpSchemeStrategy();

    @Before
    public void initSessionStorage() throws Exception {
        when(sessionStorage.getSessionIdentifier(SESSION_ID)).thenReturn(httpClientBuilder);
        when(httpClientBuilder.build()).thenReturn(httpClient);
        when(httpClient.execute(any())).thenReturn(new MockHttpResponse());
        HttpSchemeStrategy.SESSION_STORAGE = sessionStorage;
    }

    /**
     * Test method for
     * {@link org.jvoicexml.documentserver.schemestrategy.HttpSchemeStrategy#getInputStream(String, java.net.URI, org.jvoicexml.xml.vxml.RequestMethod, long, Collection<KeyValuePair>)}
     * .
     * 
     * @exception Exception
     *                test failed
     * @exception BadFetchError
     *                expected error
     */
    @Test(expected = BadFetchError.class)
    public void testBadFetch() throws Exception, BadFetchError {

        when(httpClient.execute(any())).thenThrow(new IOException("Simulated HTTP IOException"));

        final URI uri = new URI(BASE_URL + "?session=id");
        final Collection<KeyValuePair> parameters = Arrays.asList(
                new KeyValuePair("firstName", "Horst"),
                new KeyValuePair("lastName", "Buchholz")
        );
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);
    }

    /**
     * Verify that parameters passed in the parameters collection are appended to the URI
     * String on GET requests.
     */
    @Test
    public void testGetWithParameterCollection() throws Exception, BadFetchError {
        final URI uri = new URI(BASE_URL);
        final Collection<KeyValuePair> parameters = Arrays.asList(
                new KeyValuePair("firstName", "Horst"),
                new KeyValuePair("lastName", "Buchholz")
        );
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);

        verify(httpClient).execute(httpRequestCaptor.capture());
        HttpUriRequest httpRequest = httpRequestCaptor.getValue();
        assertEquals("GET", httpRequest.getMethod());
        assertEquals("http://example.com:8080?firstName=Horst&lastName=Buchholz", httpRequest.getURI().toString());
    }

    /**
     * Verify that parameters passed in the URI string are included in the actual GET request
     */
    @Test
    public void testGetWithUriParameters() throws Exception, BadFetchError {
        final String uriString = BASE_URL + "?firstName=Horst&lastName=Buchholz";
        final URI uri = new URI(uriString);
        final Collection<KeyValuePair> parameters = Collections.emptyList();
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);

        // Verify that URI string is not modified
        String httpRequestString = captureHttpRequestString();
        assertEquals(uriString, httpRequestString);
    }

    /**
     * Verify that encoded spaces in URL parameters remain encoded in actual request.
     * Expect space to be encoded as %20
     */
    @Test
    public void testUrlParameterWithSpace() throws Exception, BadFetchError {
        final String uriString = BASE_URL + "?param1=blah%20blah";
        final URI uri = new URI(uriString);
        final Collection<KeyValuePair> parameters = Collections.emptyList();
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);

        // Verify that URI string is not modified
        String httpRequestString = captureHttpRequestString();
        assertEquals(uriString, httpRequestString);
    }

    /**
     * Test for issue #27. Parameters in URL contain encoded + as %2B.
     * Note specifically that parameters are in URL string, not parameters collection.
     */
    @Test
    public void testUrlParameterWithPlus() throws Exception, BadFetchError {
        final String uriString = BASE_URL + "?param1=blah%2Bblah";
        final URI uri = new URI(uriString);
        final Collection<KeyValuePair> parameters = Collections.emptyList();
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);

        // Verify that URI string is not modified
        String httpRequestString = captureHttpRequestString();
        assertEquals(uriString, httpRequestString);
    }

    @Test
    public void testUrlFragmentIsRemoved() throws Exception, BadFetchError {
        final String uriString = BASE_URL + "#thisIsTheFragment";
        final URI uri = new URI(uriString);
        final Collection<KeyValuePair> parameters = Collections.emptyList();
        strategy.getInputStream(SESSION_ID, uri, RequestMethod.GET, TIMEOUT, parameters);

        // Verify that fragment is removed from URI string
        String httpRequestString = captureHttpRequestString();
        assertEquals(BASE_URL, httpRequestString);
    }

    private String captureHttpRequestString() throws IOException {
        verify(httpClient).execute(httpRequestCaptor.capture());
        HttpUriRequest httpRequest = httpRequestCaptor.getValue();
        return httpRequest.getURI().toString();
    }
}
