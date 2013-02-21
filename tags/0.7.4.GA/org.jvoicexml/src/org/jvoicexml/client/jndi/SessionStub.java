/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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

package org.jvoicexml.client.jndi;

import java.io.Serializable;
import java.net.URI;
import java.rmi.RemoteException;

import javax.naming.Context;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.Session;
import org.jvoicexml.SessionListener;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Stub for the <code>Session</code>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.4
 * @see org.jvoicexml.Session
 */
@SuppressWarnings("serial")
public final class SessionStub
        extends AbstractStub<RemoteSession>
        implements Session, Serializable {
    /** The session ID. */
    private String sessionID;

    /**
     * Constructs a new object.
     */
    public SessionStub() {
    }

    /**
     * Constructs a new object.
     * @param context The context to use.
     * @since 0.6
     */
    public SessionStub(final Context context) {
        super(context);
    }

    /**
     * Constructs a new object.
     * @param id The session id.
     */
    public SessionStub(final String id) {
        sessionID = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return Session.class.getSimpleName() + "." + sessionID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteSession> getRemoteClass() {
        return RemoteSession.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return Session.class;
    }

    /**
     * {@inheritDoc}
     */
    public void call(final URI uri)
            throws ErrorEvent {
        final RemoteSession session = getSkeleton(sessionID);

        try {
            session.call(uri);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            final ErrorEvent event = getErrorEvent(re);
            if (event == null) {
                re.printStackTrace();
            } else {
                throw event;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void hangup() {
        final RemoteSession session = getSkeleton(sessionID);
        if (session == null) {
            return;
        }
        try {
            session.hangup();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacterInput getCharacterInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        final RemoteSession session = getSkeleton(sessionID);

        CharacterInput input;

        try {
            input = session.getCharacterInput();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            final ErrorEvent event = getErrorEvent(re);
            if ((event == null) || !(event instanceof NoresourceError)) {
                re.printStackTrace();

                input = null;
            } else {
                final NoresourceError noresource = (NoresourceError) event;

                throw noresource;
            }
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void waitSessionEnd()
            throws ErrorEvent {
        final RemoteSession session = getSkeleton(sessionID);

        try {
            session.waitSessionEnd();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            final ErrorEvent event = getErrorEvent(re);
            if (event == null) {
                re.printStackTrace();
            } else {
                throw event;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * {@inheritDoc}
     */
    public ErrorEvent getLastError() {
        final RemoteSession session = getSkeleton(sessionID);
        try {
            return session.getLastError();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSessionListener(final SessionListener listener) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSessionListener(final SessionListener listener) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
