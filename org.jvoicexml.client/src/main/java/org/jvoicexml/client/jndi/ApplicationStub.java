/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jvoicexml.Application;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Stub for the {@link Application}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class ApplicationStub extends AbstractStub<RemoteApplication>
        implements Application, Stub, Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 6891917167049375298L;

    /** The session ID. */
    private SessionIdentifier sessionIdentifier;

    /**
     * Constructs a new object.
     */
    public ApplicationStub() {
    }

    /**
     * Constructs a new object.
     * @param id the session id
     */
    public ApplicationStub(final SessionIdentifier id) {
        sessionIdentifier = id;
    }

    /**
     * Constructs a new object.
     * 
     * @param context
     *            The context to use.
     * @since 0.6
     */
    public ApplicationStub(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStubName() {
        return Application.class.getSimpleName() + "." + sessionIdentifier.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDocument(final URI uri, final VoiceXmlDocument doc)
            throws BadFetchError {
        try {
            final RemoteApplication application = getSkeleton();
            application.addDocument(uri, doc);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlDocument getCurrentDocument() {
        try {
            final RemoteApplication application = getSkeleton();
            return application.getCurrentDocument();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<URI> getLoadedDocuments() {
        try {
            final RemoteApplication application = getSkeleton();
            return application.getLoadedDocuments();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    @Override
    public URI getApplication() {
        try {
            final RemoteApplication application = getSkeleton();
            return application.getApplication();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootDocument(final VoiceXmlDocument document)
            throws BadFetchError {
        try {
            final RemoteApplication application = getSkeleton();
            application.setRootDocument(document);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded(final URI uri) {
        try {
            final RemoteApplication application = getSkeleton();
            return application.isLoaded(uri);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getXmlBase() {
        try {
            final RemoteApplication application = getSkeleton();
            return application.getXmlBase();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI uri) {
        try {
            final RemoteApplication application = getSkeleton();
            return application.resolve(uri);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(final URI baseUri, final URI uri) {
        try {
            final RemoteApplication application = getSkeleton();
            return application.resolve(baseUri, uri);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteApplication> getRemoteClass() {
        return RemoteApplication.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return Application.class;
    }
}
