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

package org.jvoicexml.documentserver;

import java.io.InputStream;
import java.net.URI;

import org.jvoicexml.event.error.BadFetchError;

/**
 * Strategy to get a VoiceXML document from a repository for a
 * given URI scheme.
 *
 * <p>
 * A <code>SchemeStrategy</code> is responsible for only one scheme, i.e.
 * <code>http</code>. They have to register at the <code>DocumentServer</code>
 * via the method <code>DocumentServer.addSchemeStrategy(SchemeStrategy)</code>.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface SchemeStrategy {
    /**
     * Get the schme that is handled by this strategy.
     * @return Scheme that is handled by this strategy.
     */
    String getScheme();

    /**
     * Opens the external URI and returns an <code>InputStream</code> to the
     * referenced object.
     * @param uri
     *        The uri of the object to open.
     * @return <code>InputStream</code> to the referenced object.
     * @exception BadFetchError
     *         Error opening the document.
     *
     * @since 0.3
     */
    InputStream getInputStream(final URI uri)
            throws BadFetchError;
}
