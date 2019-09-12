/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import javax.naming.NamingException;

import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Stub for the <code>MappedDocumentRepository</code>.
 *
 * @author Dirk Schnelle-Walka
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
        try {
            final RemoteMappedDocumentRepository repository = getSkeleton();
            return repository.getUri(path);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();

            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final String document) {
        try {
            final RemoteMappedDocumentRepository repository = getSkeleton();
            repository.addDocument(uri, document);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDocument(final URI uri, final VoiceXmlDocument document) {
        try {
            final RemoteMappedDocumentRepository repository = getSkeleton();
            repository.addDocument(uri, document);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }

}
