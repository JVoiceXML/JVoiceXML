/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/MappedDocumentRepositoryStub.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 11:33:10 +0200 (Fr, 09 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.client.jndi;

import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.Context;

import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Stub for the <code>MappedDocumentRepository</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.4
 * @see org.jvoicexml.documentserver.schemestrategy.DocumentMap
 */
public final class MappedDocumentRepositoryStub
        extends AbstractStub<RemoteMappedDocumentRepository>
        implements MappedDocumentRepository, Stub {
    /**
     * Constructs a new object.
     */
    public MappedDocumentRepositoryStub() {
    }

    /**
     * Constructs a new object.
     * @param context The context to use.
     * @since 0.6
     */
    public MappedDocumentRepositoryStub(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return MappedDocumentRepository.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteMappedDocumentRepository> getRemoteClass() {
        return RemoteMappedDocumentRepository.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return MappedDocumentRepository.class;
    }

    /**
     * {@inheritDoc}
     */
    public URI getUri(final String path) throws URISyntaxException {
        final RemoteMappedDocumentRepository repository = getSkeleton();

        URI uri;

        try {
            uri = repository.getUri(path);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            uri = null;

            re.printStackTrace();
        }

        return uri;
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final String document) {
        final RemoteMappedDocumentRepository repository = getSkeleton();

        try {
            repository.addDocument(uri, document);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final VoiceXmlDocument document) {
        final RemoteMappedDocumentRepository repository = getSkeleton();

        try {
            repository.addDocument(uri, document);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }

}
