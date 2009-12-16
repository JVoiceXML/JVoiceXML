/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.net.URI;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * A session begins when the user starts to interact with a VoiceXML interpreter
 * context, continues as documents are loaded and processed, and ends when
 * requested by the user, a document, or the interpreter context.
 *
 * <p>
 * A <code>Session</code> can be obtained via the
 * {@linkplain JVoiceXml#createSession(RemoteClient)} method.
 * </p>
 *
 * <p>
 * A client usually performs the following method calls to interact with
 * the interpreter:
 * <ol>
 * <li>{@link #call(URI) call}</li>
 * <li>{@link #hangup() hangup}</li>
 * </ol>
 * </p>
 *
 * <p>
 * The call method returns immediately. In case of an error the client
 * does not get notified. In order to monitor the call and retrieve error
 * messages, clients should use the {@link #waitSessionEnd() waitSessionEnd}
 * method. This may be done synchronously right after the
 * <code>call</code> method call or asynchronously in a thread.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.JVoiceXml#createSession(RemoteClient)
 *
 */
public interface Session {
    /**
     * Adds the session listener.
     * @param listener the session listener to add.
     * @since 0.7.3
     */
    void addSessionListener(final SessionListener listener);

    /**
     * Removes the session listener.
     * @param listener the session listener to remove.
     * @since 0.7.3
     */
    void removeSessionListener(final SessionListener listener);

    /**
     * Retrieves the universal unique identifier for this session.
     * @return Universal unique identifier for this session.
     * @since 0.4
     */
    String getSessionID();

    /**
     * Handles a call request.
     *
     * <p>
     * Starts processing of the given application and returns immediately.
     * </p>
     * <p>
     * Since this method returns immediately, it offers no means to monitor the
     * call processing and catch exceptions. Therefore clients are requested
     * to use the {@link #waitSessionEnd()} method to monitor the session.
     * Another way can be via the {@link org.jvoicexml.implementation.Telephony}
     * interface and calling {@link #getLastError()}. However, the latter
     * option relies on the concrete implementation.
     * </p>
     * @param uri URI of the first document to load.
     *
     * @exception ErrorEvent
     *            Error initiating the call.
     */
    void call(final URI uri)
            throws ErrorEvent;


    /**
     * Closes this session. After a session is closed, it can not be
     * reopened e.g., to call another application.
     *
     * @since 0.4
     */
    void hangup();

    /**
     * Retrieves the DTMF input device.
     * @return DTMF input device.
     * @exception NoresourceError
     *            Input device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     * @since 0.5
     */
    CharacterInput getCharacterInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Delays until the session ends.
     * @exception ErrorEvent
     *            Error waiting for the end of the call.
     * @since 0.4
     */
    void waitSessionEnd()
            throws ErrorEvent;

    /**
     * Retrieves an error, if any, that happened during call processing.
     * @return an error that happened during call processing, <code>null</code>
     *         if there was no error.
     * @exception ErrorEvent
     *            Error reading the last error.
     * @since 0.7
     */
    ErrorEvent getLastError()
        throws ErrorEvent;
}
