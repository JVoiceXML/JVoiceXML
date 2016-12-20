/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.Session;
import org.jvoicexml.client.jndi.ApplicationStub;
import org.jvoicexml.client.jndi.DtmfInputStub;
import org.jvoicexml.client.jndi.RemoteSession;
import org.jvoicexml.client.jndi.Stub;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Skeleton for the {@link Session}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 * @see org.jvoicexml.Session
 */
final class SessionSkeleton extends UnicastRemoteObject
        implements RemoteSession, Skeleton {
    /** The serial version UID. */
    private static final long serialVersionUID = 7903915853416003896L;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SessionSkeleton.class);
    /** The encapsulated <code>ApplicationRegistry</code>. */
    private Session session;

    /** The JNDI context to use. */
    private final Context context;

    /**
     * Constructs a new object.
     * 
     * @throws RemoteException
     *             Error creating the remote object.
     */
    public SessionSkeleton() throws RemoteException {
        context = null;
    }

    /**
     * Constructs a new object with the given session.
     * 
     * @param ctx
     *            the current JNDI context
     * @param sess
     *            The session
     * @throws RemoteException
     *             Error creating the remote object.
     */
    public SessionSkeleton(final Context ctx, final Session sess)
            throws RemoteException {
        context = ctx;
        session = sess;
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName() {
        return RemoteSession.class.getSimpleName() + "."
                + session.getSessionID();
    }

    /**
     * {@inheritDoc}
     */
    public Application call(final URI uri) throws RemoteException {
        if (session == null) {
            return null;
        }

        try {
            final Application application = session.call(uri);
            final String id = session.getSessionID();
            final ApplicationSkeleton skeleton = new ApplicationSkeleton(id,
                    application);
            final ApplicationStub stub = new ApplicationStub(id);
            JVoiceXmlJndiSupport.bind(context, skeleton, stub);
            return stub;
        } catch (ErrorEvent event) {
            LOGGER.error(event.getMessage(), event);

            throw new RemoteException(event.getMessage(), event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getApplication() throws RemoteException {
        if (session == null) {
            return null;
        }

        return session.getApplication();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DtmfInput getDtmfInput() throws RemoteException {
        if (session == null) {
            return null;
        }

        try {
            final String id = session.getSessionID();
            final DtmfInput input = session.getDtmfInput();
            final Skeleton skeleton = new DtmfInputSkeleton(id, input);
            final DtmfInput characterInput = new DtmfInputStub(id);
            final Stub stub = (Stub) characterInput;

            JVoiceXmlJndiSupport.bind(context, skeleton, stub);

            return characterInput;
        } catch (NoresourceError error) {
            throw new RemoteException(error.getMessage(), error);
        } catch (ConnectionDisconnectHangupEvent error) {
            throw new RemoteException(error.getMessage(), error);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void waitSessionEnd() throws RemoteException {
        if (session == null) {
            return;
        }

        try {
            session.waitSessionEnd();
        } catch (ErrorEvent event) {
            LOGGER.error(event.getMessage(), event);

            throw new RemoteException(event.getMessage(), event);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEnded() throws RemoteException {
        if (session == null) {
            return true;
        }

        return session.hasEnded();
    }

    /**
     * {@inheritDoc}
     */
    public ErrorEvent getLastError() throws RemoteException {
        if (session == null) {
            return null;
        }
        try {
            return session.getLastError();
        } catch (ErrorEvent event) {
            LOGGER.error(event.getMessage(), event);

            throw new RemoteException(event.getMessage(), event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void hangup() throws RemoteException {
        if (session == null) {
            return;
        }

        session.hangup();

        UnicastRemoteObject.unexportObject(this, true);
    }
}
