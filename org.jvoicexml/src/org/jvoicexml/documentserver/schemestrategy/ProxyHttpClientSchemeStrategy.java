/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/interpreter/form/ExecutableMenuForm.java $
 * Version: $LastChangedRevision: 709 $
 * Date:    $Date: 2008-02-26 17:35:13 +0100 (Di, 26 Feb 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * A <code>http</code> {@link SchemeStrategy} that is able to accept a
 * cookie to identify a session.
 *
 * <p>
 * This can be used in scenarios where we have a state aware server that
 * identifies a session via cookies.
 * </p>
 *
 * <p>
 * Note: This can not be used in general since there might be multiple
 * sessions in parallel, all using the same session
 * </p>
 *
 * TODO Make the scheme strategies session aware.
 * @author Neil Steinbuch
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 */
public final class ProxyHttpClientSchemeStrategy implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(ProxyHttpClientSchemeStrategy.class);

    /** Name of the scheme that is accepted by this strategy. */
    public static final String SCHEME_NAME = "http";

    /** The delegate. */
    private final ProxyHttpDelegate delegate;

    /**
     * Constructs a new object.
     */
    public ProxyHttpClientSchemeStrategy() {
        delegate = new ProxyHttpDelegate(true);
    }
    /**
     * {@inheritDoc}
     */
    public InputStream getInputStream(final Session session, final URI uri,
            final RequestMethod method, final long timeout,
            final Map<String, Object> parameters)
        throws BadFetchError {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning input stream.....");
        }
        delegate.setUri(uri);
        try {
            return delegate.getHtmlAsInputStream();
        } catch (URISyntaxException e) {
            throw new BadFetchError(e);
        }
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
    public void sessionClosed(final Session session) {
    }
}
