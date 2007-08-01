/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Application configuration settings. Maps a terminal name to a URI.
 *
 * @author Dirk Schnelle
 *
 * @version $Revision: 206 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class ConfiguredApplication {
    /** Name of the terminal. */
    private String terminal;

    /** URI of the application to call. */
    private URI uri;

    /**
     * Constructs a new object.
     */
    public ConfiguredApplication() {
    }

    /**
     * Retrieves the name of the terminal.
     * @return the terminal
     */
    public String getTerminal() {
        return terminal;
    }

    /**
     * Sets the name of the terminal.
     * @param term the terminal to set
     */
    public void setTerminal(final String term) {
        terminal = term;
    }

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public URI getUriObject() {
        return uri;
    }

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public String getUri() {
        return uri.toString();
    }

    /**
     * Sets the URI of the application to call.
     * @param applicationUri the URI to set
     */
    public void setUri(final String applicationUri) {
        final URI application;
        try {
            application = new URI(applicationUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        uri = application;
    }
}
