/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/jndi/JVoiceXmlSkeleton.java $
 * Version: $LastChangedRevision: 1874 $
 * Date:    $LastChangedDate: 2009-10-20 09:07:58 +0200 (Di, 20 Okt 2009) $
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

package org.jvoicexml.jndi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.client.jndi.RemoteJVoiceXml;
import org.jvoicexml.client.jndi.SessionStub;
import org.jvoicexml.client.jndi.Stub;
import org.jvoicexml.event.ErrorEvent;

/**
 * Skeleton for <code>JVoiceXml</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1874 $
 * @since 0.4
 * @see org.jvoicexml.JVoiceXml
 */
@SuppressWarnings("serial")
class JVoiceXmlSkeleton
        extends UnicastRemoteObject implements RemoteJVoiceXml, Skeleton {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlSkeleton.class);
    /** The encapsulated <code>JVoiceXml</code> object. */
    private JVoiceXml jvxml;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public JVoiceXmlSkeleton()
            throws RemoteException {
    }

    /**
     * Constructs a new object with the given main entry point..
     * @param jvoicexml Main entry point for all clients.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public JVoiceXmlSkeleton(final JVoiceXml jvoicexml)
            throws RemoteException {
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
    public Session createSession(final ConnectionInformation client)
            throws RemoteException {
        if (jvxml == null) {
            return null;
        }

        final Session session;
        try {
            session = jvxml.createSession(client);
        } catch (ErrorEvent ee) {
            LOGGER.error("unable to create session", ee);

            throw new RemoteException("unable to create session", ee);
        }

        final Skeleton sessionSkeleton = new SessionSkeleton(session);
        final Stub sessionStub = new SessionStub(session.getSessionID());

        final Context context;
        try {
            context = new InitialContext();
        } catch (javax.naming.NamingException ne) {
            throw new RemoteException("unable tor retrieve the initial context",
                                      ne);
        }

        JVoiceXmlJndiSupport.bind(context, sessionSkeleton, sessionStub);

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
