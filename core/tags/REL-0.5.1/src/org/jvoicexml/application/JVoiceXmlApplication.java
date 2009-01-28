/*
 * File:    $RCSfile: JVoiceXmlApplication.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/04/12 10:12:12 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.application;

import java.net.URI;

import org.jvoicexml.Application;

/**
 * Implementation of the <code>Application</code>.
 *
 * @see org.jvoicexml.Application
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlApplication
        implements Application {
    /** The serial version UID. */
    static final long serialVersionUID = 3267591782314320417L;

    /** System wide unique identifier of this application. */
    private final String id;

    /** URI to retrieve the root document from the document server. */
    private final URI uri;

    /**
     * Create a new object.
     *
     * @param applicationId
     *        System wide unique identifier of this application.
     * @param rootUri
     *        URI to retrieve the root document from the document server.
     */
    public JVoiceXmlApplication(final String applicationId, final URI rootUri) {
        super();

        id = applicationId;
        uri = rootUri;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id;
    }
}
