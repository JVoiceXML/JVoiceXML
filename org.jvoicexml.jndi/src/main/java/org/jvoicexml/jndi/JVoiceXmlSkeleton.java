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

package org.jvoicexml.jndi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifer;
import org.jvoicexml.client.jndi.RemoteJVoiceXml;
import org.jvoicexml.client.jndi.SessionStub;
import org.jvoicexml.client.jndi.Stub;
import org.jvoicexml.event.ErrorEvent;

/**
 * Skeleton for <code>JVoiceXml</code>.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.4
 * @see org.jvoicexml.JVoiceXml
 */
class JVoiceXmlSkeleton
        extends UnicastRemoteObject implements RemoteJVoiceXml, Skeleton {
    /** The serial version UID. */
    private static final long serialVersionUID = 3777294862730171402L;

    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(JVoiceXmlSkeleton.class);

    /** The encapsulated <code>JVoiceXml</code> object. */
    private JVoiceXml jvxml;

    /** The JNDI context. */
    private final Context context;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    JVoiceXmlSkeleton()
            throws RemoteException {
        context = null;
    }

    /**
     * Constructs a new object with the given main entry point.
     * @param ctx the current JNDI context.
     * @param jvoicexml Main entry point for all clients.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    JVoiceXmlSkeleton(final Context ctx, final JVoiceXml jvoicexml)
            throws RemoteException {
        context = ctx;
        jvxml = jvoicexml;
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName() {
        return RemoteJVoiceXml.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        if (jvxml == null) {
            return null;
        }

        return jvxml.getVersion();
    }

    /**
     * {@inheritDoc}
     */
    public Session createSession(final ConnectionInformation info)
            throws RemoteException {
        final SessionIdentifier id = new UuidSessionIdentifer();
        return createSession(info, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(
            final ConnectionInformation info,
            final SessionIdentifier id) throws RemoteException {
        if (jvxml == null) {
            return null;
        }
        final Session session;
        try {
            session = jvxml.createSession(info, id);
        } catch (ErrorEvent e) {
            LOGGER.error("unable to create session", e);
            throw new RemoteException("unable to create session", e);
        }

        final Skeleton sessionSkeleton = new SessionSkeleton(context, session);
        final Stub sessionStub = new SessionStub(session.getSessionId());

        JVoiceXmlJndiSupport.bind(context, sessionSkeleton);

        return (Session) sessionStub;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown()
            throws RemoteException {
        if (jvxml == null) {
            return;
        }

        jvxml.shutdown();
    }
}
