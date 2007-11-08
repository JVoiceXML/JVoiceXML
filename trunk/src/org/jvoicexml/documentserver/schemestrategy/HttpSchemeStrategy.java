/*
 * File:    $RCSfile: HttpSchemeStrategy.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;

/**
 *{@link SchemeStrategy} to read VoiceXML document via the HTTP protocol.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class HttpSchemeStrategy
        implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(HttpSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "http";

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
     * Open a connection to the specified URL.
     * @param uri URI to open.
     * @return Opened connection.
     * @exception BadFetchError
     *            Error opening the connection.
     */
    private URLConnection connect(final URI uri)
            throws BadFetchError {
        final URL url;
        try {
            url = uri.toURL();
        } catch (java.net.MalformedURLException mue) {
            throw new BadFetchError(mue);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("connecting to '" + url + "'...");
        }

        final URLConnection connection;
        try {
            connection = url.openConnection();
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }

        return connection;
    }

    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(final URI uri)
            throws BadFetchError {
        final URLConnection connection = connect(uri);
        try {
            return connection.getInputStream();
        } catch (java.io.IOException ioe) {
            throw new BadFetchError(ioe);
        }
    }
}
