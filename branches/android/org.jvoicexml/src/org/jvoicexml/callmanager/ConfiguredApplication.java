/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/ConfiguredApplication.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * @author Dirk Schnelle-Walka
 *
 * @version $Revision: 2129 $
 * @since 0.6
 */
public class ConfiguredApplication {
    /** Name of the terminal. */
    private String terminal;

    /** URI of the application to call. */
    private URI uri;

    /** Type of input that should be used. */
    private String inputType;

    /** Type of output that should be used. */
    private String outputType;

    /**
     * Constructs a new object.
     */
    public ConfiguredApplication() {
    }

    /**
     * Retrieves the name of the terminal.
     * @return the terminal
     */
    public final String getTerminal() {
        return terminal;
    }

    /**
     * Sets the name of the terminal.
     * @param term the terminal to set
     */
    public final void setTerminal(final String term) {
        terminal = term;
    }

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public final URI getUriObject() {
        return uri;
    }

    /**
     * Retrieves the URI of the application to call.
     * @return the uri
     */
    public final String getUri() {
        return uri.toString();
    }

    /**
     * Sets the URI of the application to call.
     * @param applicationUri the URI to set
     */
    public final  void setUri(final String applicationUri) {
        final URI application;
        try {
            application = new URI(applicationUri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        uri = application;
    }

    /**
     * Retrieves the input type that will be used for the application.
     * @return the input type.
     */
    public final String getInputType() {
        return inputType;
    }

    /**
     * Sets the input type of this application.
     * @param type the new input type.
     */
    public final void setInputType(final String type) {
        inputType = type;
    }

    /**
     * Retrieves the output type that will be used for the application.
     * @return the output type.
     */
    public final String getOutputType() {
        return outputType;
    }

    /**
     * Sets the output type of this application.
     * @param type the new output type.
     */
    public final void setOutputType(final String type) {
        outputType = type;
    }
}
