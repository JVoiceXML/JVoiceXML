/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/interpreter/form/ExecutableMenuForm.java $
 * Version: $LastChangedRevision: 709 $
 * Date:    $Date: 2008-02-26 17:35:13 +0100 (Di, 26 Feb 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 * Retrieve a page from a web server.
 *
 * @author Neil Steinbuch
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class ProxyHttpDelegate {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ProxyHttpDelegate.class);

    /** Number of retries to obtain a page. */
    private static final int MAXIMUM_RETRIES = 3;

    /** The HTTP client. */
    private HttpClient client;

    /** The get method to retrieve the site. */
    private GetMethod method = null;

    /** <code>true</code> if cookies should be saved. */
    private boolean saveCookies;

    /** Accepted cookies. */
    private Cookie[] cookies;

    /** The URI to read from. */
    private URI uri;

    /**
     * Constructs a new object.
     * @param accpeptCookies <code>true</code> if cookies should be accepted.
     */
    public ProxyHttpDelegate(final boolean accpeptCookies) {
        super();
        setSaveCookies(accpeptCookies);
    }

    /**
     * Retrieves the page as an input stream.
     * @return input stream to read the page.
     * @throws URISyntaxException
     *         Unable to determine the URI of the VoiceXML document.
     */
    public InputStream getHtmlAsInputStream() throws URISyntaxException {

        String html = getData();

        // we must now check, if this is an html document and not vxml
        // then we must get the <anchor> tag since the VoicePortal is
        // really whacked and returns an html document on the
        // first interaction
        // IE
        //
        // <?xml version="1.0" encoding="UTF-8"?>
        // <HTML xmlns="http://www.w3.org/2001/vxml">
        // <HEAD> <TITLE>Answer Document</TITLE>
        // </HEAD>
        // <BODY>
        // <form name="inputform">
        // <a href="http://localhost:8080/VoicePortal/FNBCARD_1.vxml">
        //    http://localhost:8080/VoicePortal/FNBCARD_1.vxml</a>
        // </form>
        // </BODY>
        // </HTML>
        // TODO check if we need to stick with this whacked voice portal
        while (isHtmlDocument(html)) {
            // we must get the vxml
            final String vxmlAnchor = getVXMLAnchorFromHTML(html);
            setUri(new URI(vxmlAnchor));
            html = getData();
        }

        final byte[] buffer = html.getBytes();
        return new ByteArrayInputStream(buffer);
    }

    /**
     * Checks if the given document is a HTML document.
     * @param html the document to check.
     * @return <code>true</code> if the document is a HTML document.
     */
    private boolean isHtmlDocument(final String html) {
        return html.toUpperCase().indexOf("<HTML") > -1
                || html.toUpperCase().indexOf("</HTML>") > -1;
    }

    /**
     * Retrieves the page.
     * @return read page.
     */
    private String getData() {
        String html = null;
        client = new HttpClient();

        // Create a method instance.
        method = new GetMethod(getUri().toString());
        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(MAXIMUM_RETRIES, false));

        // if we must load cookies
        if (isSaveCookies()) {
            if (cookies != null) {
                client.getState().addCookies(cookies);
            }
        }

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Method failed: " + method.getStatusLine());
            }
            // Read the response body.
            byte[] responseBody = method.getResponseBody();
            html = new String(responseBody);
            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary
            // data
            if (LOGGER.isDebugEnabled()) {
                printCookies();
            }

            // if we must save cookies
            if (isSaveCookies()) {
                cookies = client.getState().getCookies();
            }

        } catch (Exception e) {
            LOGGER.error("Fatal protocol violation: " + e.getMessage(), e);
        }

        return html;
    }

    /**
     * Facility to print the saved cookies.
     */
    private void printCookies() {
        final Cookie[] currentCookies = client.getState().getCookies();

        for (int i = 0; i < currentCookies.length; i++) {
            Cookie cookie = currentCookies[i];
            LOGGER.debug("HttpDelegate.Cookie[" + i + "] = "
                    + cookie.toString());
        }
    }

    /**
     * Releases the HTTP connection.
     */
    public void release() {
        method.releaseConnection();
    }

    /**
     * Checks if cookies are saved.
     * @return <code>true</code> if cookies are saved.
     */
    private boolean isSaveCookies() {
        return saveCookies;
    }

    /**
     * Set cookie saving mode.
     * @param save <code>true</code> if cookies are to be saved.
     */
    private void setSaveCookies(final boolean save) {
        saveCookies = save;
    }

    /**
     * Retrieves the URI from which to retrieve the page.
     * @return URI from which to retrieve the page.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Sets the URI from which to retrieve the page.
     * @param newUri URI from which to retrieve the page.
     */
    public void setUri(final URI newUri) {
        uri = newUri;
    }

    /**
     * Retrieves the URI of the VoiceXML document within an HTML anchor tag.
     * @param html current page.
     * @return URI of the VoiceXML document.
     */
    private String getVXMLAnchorFromHTML(final String html) {
        String startSequence = "<a href=\"";
        int start = html.indexOf(startSequence);

        // get rid of everything before the start
        final String htmlSubstring = html.substring(start
                + startSequence.length(), html.length());

        // now find the next index of ">
        String endSequence = "\">";
        int end = htmlSubstring.indexOf(endSequence);

        // substring again
        return html.substring(0, end);
    }

}
