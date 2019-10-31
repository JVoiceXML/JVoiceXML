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

package org.jvoicexml.jndi;

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.client.jndi.ApplicationStub;
import org.jvoicexml.client.jndi.DtmfInputStub;
import org.jvoicexml.client.jndi.RemoteApplication;
import org.jvoicexml.client.jndi.RemoteDtmfInput;
import org.jvoicexml.client.jndi.RemoteSession;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Skeleton for the {@link Session}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.4
 * @see org.jvoicexml.Session
 */
final class SessionSkeleton
        implements RemoteSession {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
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
    SessionSkeleton() throws RemoteException {
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
    SessionSkeleton(final Context ctx, final Session sess)
            throws RemoteException {
        context = ctx;
        session = sess;
    }

    /**
     * Retrieves the name of this skeleton.
     * @return name of the skeleton
     */
    public String getSkeletonName() {
        return RemoteSession.class.getSimpleName() + "."
                + session.getSessionId().getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application call(final URI uri) throws RemoteException {
        if (session == null) {
            return null;
        }

        try {
            final Application application = session.call(uri);
            final SessionIdentifier id = session.getSessionId();
            final RemoteApplication skeleton = new ApplicationSkeleton(id,
                    application);
            final RemoteApplication stub = (RemoteApplication) 
                    UnicastRemoteObject.exportObject(skeleton, 0);
            final String name = 
                    ((ApplicationSkeleton) skeleton).getSkeletonName();
            try {
                context.rebind(name, stub);
            } catch (NamingException e) {
                throw new RemoteException(e.getMessage(), e);
            }
            LOGGER.info("bound '" + name + "' to '" 
                    + stub.getClass().getCanonicalName() + "'"
                    + "(" + ApplicationSkeleton.class.getCanonicalName()
                    + ")'");
            return new ApplicationStub(id);
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
            final SessionIdentifier id = session.getSessionId();
            final DtmfInput input = session.getDtmfInput();
            final RemoteDtmfInput skeleton = new DtmfInputSkeleton(id, input);
            final RemoteSession stub = (RemoteSession) 
                    UnicastRemoteObject.exportObject(skeleton, 0);
            final String name = 
                    ((DtmfInputSkeleton) skeleton).getSkeletonName();
            try {
                context.rebind(name, stub);
            } catch (NamingException e) {
                throw new RemoteException(e.getMessage(), e);
            }
            LOGGER.info("bound '" + name + "' to '" 
                    + stub.getClass().getCanonicalName() + "'");

            return new DtmfInputStub(id);
        } catch (NoresourceError error) {
            throw new RemoteException(error.getMessage(), error);
        } catch (ConnectionDisconnectHangupEvent error) {
            throw new RemoteException(error.getMessage(), error);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    @Override
    public void hangup() throws RemoteException {
        if (session == null) {
            return;
        }

        session.hangup();

        UnicastRemoteObject.unexportObject(this, true);
    }
}
