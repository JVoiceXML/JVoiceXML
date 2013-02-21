/*
 * File:    $RCSfile: SessionSkeleton.java,v $
 * Version: $Revision: 1.8 $
 * Date:    $Date: 2006/06/22 12:31:09 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.Session;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CharacterInput;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Skeleton for the <code>Session</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.8 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 * @see org.jvoicexml.Session
 */
final class SessionSkeleton
        extends UnicastRemoteObject implements RemoteSession, Skeleton {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SessionSkeleton.class);

    /** The serial version UID. */
    static final long serialVersionUID = 5077447568049520892L;

    /** The encapsulataed <code>ApplicationRegistry</code>. */
    private Session session;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public SessionSkeleton()
            throws RemoteException {
    }

    /**
     * Constructs a new object with the given session.
     * @param sess The session.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public SessionSkeleton(final Session sess)
            throws RemoteException {
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
    public void call()
            throws RemoteException {
        if (session == null) {
            return;
        }

        try {
            session.call();
        } catch (ErrorEvent event) {
            LOGGER.error(event.getMessage(), event);

            throw new RemoteException(event.getMessage(), event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void hangup()
            throws RemoteException {
        if (session == null) {
            return;
        }

        session.hangup();
    }

    /**
     * {@inheritDoc}
     */
    public CharacterInput getCharacterInput()
            throws RemoteException {
        if (session == null) {
            return null;
        }

        try {
            final String id = session.getSessionID();
            final CharacterInput input = session.getCharacterInput();
            final Skeleton skeleton = new CharacterInputSkeleton(id, input);
            final CharacterInput characterInput = new CharacterInputStub(id);
            final Stub stub = (Stub) characterInput;

            final Context context;
            try {
                context = new InitialContext();
            } catch (javax.naming.NamingException ne) {
                throw new RemoteException(
                        "unable tor retrieve the initial context",
                        ne);
            }

            JndiSupport.bind(context, skeleton, stub);

            return characterInput;
        } catch (NoresourceError error) {
            throw new RemoteException(error.getMessage(), error);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void waitSessionEnd()
            throws RemoteException {
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
    public void close()
            throws RemoteException {
        if (session == null) {
            return;
        }

        session.close();

        UnicastRemoteObject.unexportObject(this, true);
    }

}
