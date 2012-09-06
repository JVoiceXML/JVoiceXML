/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/SchemeStrategy.java $
 * Version: $LastChangedRevision: 2839 $
 * Date:    $Date: 2011-10-13 02:33:06 -0500 (jue, 13 oct 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.UnsupportedElementError;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Strategy to get a VoiceXML document from a repository for a
 * given URI scheme.
 *
 * <p>
 * A <code>SchemeStrategy</code> is responsible for only one scheme, e.g.
 * <code>http</code>. They have to register at the <code>DocumentServer</code>
 * via the method <code>DocumentServer.addSchemeStrategy(SchemeStrategy)</code>.
 * </p>
 *
 * <p>
 * A <code>SchemeStrategy</code> may store session relevant data to identify
 * a concrete established session with a server.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 2839 $
 *
 */
public interface SchemeStrategy {
    /**
     * Get the scheme that is handled by this strategy.
     * @return Scheme that is handled by this strategy.
     */
    String getScheme();

    /**
     * Opens the external URI and returns an <code>InputStream</code> to the
     * referenced object.
     * @param sessionId
     *        the Id of the current JVoiceXML session.
     * @param uri
     *        the URI of the object to open.
     * @param method
     *        type of the request method
     * @param timeout
     *        fetch timeout in msec to wait for the content to be returned
     *        before throwing an <code>error.badfetch</code> event.
     * @param parameters
     *        request parameters
     * @return <code>InputStream</code> to the referenced object.
     * @exception BadFetchError
     *         error opening the document or unsupported method type
     * @exception UnsupportedElementError
     *         the requested element is not supported
     * @exception IOException
     *         error creating the input stream
     *
     * @since 0.3
     */
    InputStream getInputStream(final String sessionId, final URI uri,
            final RequestMethod method, final long timeout,
            final Map<String, Object> parameters)
            throws BadFetchError, UnsupportedElementError, IOException;

    /**
     * Notification that the given session is closed. Now the strategy
     * may free any resources related to the given session, e.g. a session with
     * a web server.
     * @param sessionId the Id of the current JVoiceXML session.
     * @since 0.7
     */
    void sessionClosed(final String sessionId);
}
