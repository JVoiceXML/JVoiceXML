/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * {@link SchemeStrategy} to read VoiceXML document from the file system. The
 * files are retrieved by their {@link URI} which has to be
 * <em>hierarchical</em>.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.3
 */
public final class FileSchemeStrategy implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(FileSchemeStrategy.class);

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
    @Override
    public String getScheme() {
        return SCHEME_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(final SessionIdentifier sessionId,
            final URI uri, final RequestMethod method, final long timeout,
            final Collection<KeyValuePair> parameters) throws BadFetchError {

        try {
            // Remove the fragment.
            // A file based URI may only have a scheme and a path.
            final String scheme = uri.getScheme();
            final String path = uri.getPath();
            final URI fragmentLessUri = new URI(scheme, null, path, null);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("reading '" + fragmentLessUri + "'...");
            }
            final File file = new File(fragmentLessUri);
            return new FileInputStream(file);
        } catch (java.io.FileNotFoundException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (java.lang.IllegalArgumentException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(final SessionIdentifier sessionId) {
    }
}
