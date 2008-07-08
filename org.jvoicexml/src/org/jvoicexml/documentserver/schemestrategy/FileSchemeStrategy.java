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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * {@link SchemeStrategy} to read VoiceXML document from the file system.
 * The files are retrieved by their URI, which has to be <em>hierarchical</em>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.3
 *
 * @see java.net.URI
 */
public final class FileSchemeStrategy
        implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FileSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "file";

    /**
     * Construct a new object.
     */
    public FileSchemeStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public String getScheme() {
        return SCHEME_NAME;
    }


    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(final Session session, final URI uri,
            final RequestMethod method, final Map<String, Object> parameters)
            throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reading '" + uri + "'...");
        }

        final File file = new File(uri);

        final InputStream input;
        try {
            input = new FileInputStream(file);
        } catch (java.io.FileNotFoundException fnfe) {
            throw new BadFetchError(fnfe);
        } catch (java.lang.IllegalArgumentException iae) {
            throw new BadFetchError(iae);
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void sessionClosed(final Session session) {
    }
}
