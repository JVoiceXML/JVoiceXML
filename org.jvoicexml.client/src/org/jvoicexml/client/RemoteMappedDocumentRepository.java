/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client;

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Remote interface to enable remote method calls.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.4
 * @see org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository
 */
public interface RemoteMappedDocumentRepository
        extends Remote {
    /**
     * Gets an URI that can be evaluated by this scheme strategy for the
     * given ssp.
     * @param ssp Scheme specific part
     * @return Valid URI for this strategy, <code>null</code> in case of an
     * error.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    URI getUri(final String ssp)
            throws RemoteException;

    /**
     * Adds the given document to this repository.
     * @param uri URI as a key for later retrieval.
     * @param document Document to be added.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void addDocument(final URI uri, final String document)
            throws RemoteException;

    /**
     * Adds the given document to this repository.
     * @param uri URI as a key for later retrieval.
     * @param document Document to be added.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void addDocument(final URI uri, final VoiceXmlDocument document)
            throws RemoteException;
}
