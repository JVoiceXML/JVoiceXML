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

import java.io.Serializable;
import java.net.URI;
import java.rmi.RemoteException;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jvoicexml.Application;
import org.jvoicexml.DtmfInput;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
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
public final class SessionStub
        extends AbstractStub<RemoteSession>
        implements Session, Serializable {
    /** The serial version UID  */
    private static final long serialVersionUID = -3205385620722771514L;
    /** The session ID. */
    private SessionIdentifier sessionIdentifier;

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
    public SessionStub(final SessionIdentifier id) {
        sessionIdentifier = id;
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return Session.class.getSimpleName() + "." + sessionIdentifier.getId();
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
    @Override
    public Application call(final URI uri)
            throws ErrorEvent {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            return session.call(uri);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();

            final ErrorEvent event = getErrorEvent(re);
            if (event == null) {
                re.printStackTrace();
                return null;
            } else {
                throw event;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hangup() {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            if (session == null) {
                return;
            }
            session.hangup();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Application getApplication() {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            return session.getApplication();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DtmfInput getDtmfInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            return session.getDtmfInput();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            final ErrorEvent event = getErrorEvent(re);
            if ((event == null) || !(event instanceof NoresourceError)) {
                re.printStackTrace();
                return null;
            } else {
                final NoresourceError noresource = (NoresourceError) event;

                throw noresource;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitSessionEnd()
            throws ErrorEvent {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            session.waitSessionEnd();
        } catch (java.rmi.RemoteException | NamingException re) {
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
    @Override
    public boolean hasEnded() {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            return session.hasEnded();
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();
            re.printStackTrace();
            return true;
            
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SessionIdentifier getSessionId() {
        return sessionIdentifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ErrorEvent getLastError() {
        try {
            final RemoteSession session = getSkeleton(sessionIdentifier.getId());
            return session.getLastError();
        } catch (RemoteException | NamingException e) {
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
