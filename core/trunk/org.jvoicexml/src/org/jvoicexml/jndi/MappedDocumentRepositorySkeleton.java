/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.jndi;

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jvoicexml.client.jndi.RemoteMappedDocumentRepository;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Skeleton for the <code>MappedDocumentRepository</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 * @see org.jvoicexml.documentserver.schemestrategy.DocumentMap
 */
class MappedDocumentRepositorySkeleton
        extends UnicastRemoteObject implements RemoteMappedDocumentRepository,
        Skeleton {
    /** The serial version UID.*/
    static final long serialVersionUID = 7441889311828468580L;

    /** The encapsulated <code>DocumentMap</code>. */
    private DocumentMap map;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public MappedDocumentRepositorySkeleton()
            throws RemoteException {
    }

    /**
     * Constructs a new object.
     * @param documentMap The document map.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public MappedDocumentRepositorySkeleton(final DocumentMap documentMap)
            throws RemoteException {
        map = documentMap;
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName() {
        return RemoteMappedDocumentRepository.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public URI getUri(final String path) throws RemoteException {
        if (map == null) {
            return null;
        }

        try {
            return map.getUri(path);
        } catch (URISyntaxException e) {
            throw new RemoteException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final String document)
            throws RemoteException {
        if (map == null) {
            return;
        }

        map.addDocument(uri, document);
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final VoiceXmlDocument document)
            throws RemoteException {
        if (map == null) {
            return;
        }

        map.addDocument(uri, document);
    }

}
