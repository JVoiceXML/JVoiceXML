/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/callmanager/text/TextApplication.java $
 * Version: $LastChangedRevision: 2528 $
 * Date:    $Date: 2011-01-25 08:55:10 +0100 (Di, 25 Jan 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.text;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A mapping of a port number to a URI that shall be called, once a connection
 * to the call manager is made.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2528 $
 * @since 0.7.3
 */
public final class TextApplication {
    /** Port number. */
    private int port;

    /** URI of the application to call. */
    private URI uri;

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public URI getUriObject() {
        return uri;
    }


    /**
     * Retrieves the port number.
     * @return the port
     */
    public int getPort() {
        return port;
    }


    /**
     * Sets the port number.
     * @param portNumber the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public String getUri() {
        if (uri == null) {
            return null;
        }
        return uri.toString();
    }

    /**
     * Sets the URI of the application to call.
     * @param applicationUri the URI to set
     * @exception IllegalArgumentException
     *            if the given string can not be converted into an URI
     */
    public void setUri(final String applicationUri)
            throws IllegalArgumentException {
        if (applicationUri == null) {
            uri = null;
        } else {
            final URI application;
            try {
                application = new URI(applicationUri);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
            uri = application;
        }
    }
}
