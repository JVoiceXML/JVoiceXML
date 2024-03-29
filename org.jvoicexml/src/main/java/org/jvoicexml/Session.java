/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * requested by the user, a document, or the interpreter context. Note, that a
 * session is only valid for a single call and <b>cannot be reused for
 * subsequent calls</b>.
 *
 * <p>
 * A <code>Session</code> can be obtained via the
 * {@linkplain JVoiceXml#createSession(ConnectionInformation, SessionIdentifier)}
 * method.
 * </p>
 *
 * <p>
 * A client usually performs the following method calls to interact with the
 * interpreter:
 * </p>
 * <ol>
 * <li>{@link #call(URI)}</li>
 * <li>{@link #waitSessionEnd()}</li>
 * <li>{@link #hangup()}</li>
 * </ol>
 *
 * <p>
 * Note, that a call to {@link #call(URI)} <b>must</b> be followed by a call to
 * {@link #hangup()} in order to free resources such as ASR and TTS.
 * </p>
 * 
 * <p>
 * The call method returns immediately. In case of an error the client does not
 * get notified. In order to monitor the call and retrieve error messages,
 * clients should use the {@link #waitSessionEnd() waitSessionEnd} method. This
 * may be done synchronously right after the <code>call</code> method call or
 * asynchronously in a thread.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @see org.jvoicexml.JVoiceXml#createSession(ConnectionInformation,
 *      SessionIdentifier)
 *
 */
public interface Session {
    /**
     * Adds the session listener.
     * 
     * @param listener
     *            the session listener to add.
     * @since 0.7.3
     */
    void addSessionListener(SessionListener listener);

    /**
     * Removes the session listener.
     * 
     * @param listener
     *            the session listener to remove.
     * @since 0.7.3
     */
    void removeSessionListener(SessionListener listener);

    /**
     * Retrieves the universal unique identifier for this session.
     * 
     * @return Universal unique identifier for this session.
     * @since 0.4
     */
    SessionIdentifier getSessionId();

    /**
     * Handles a call request.
     *
     * <p>
     * Starts processing of the given application and returns immediately.
     * </p>
     * <p>
     * Since this method returns immediately, it offers no means to monitor the
     * call processing and catch exceptions. Therefore clients are requested to
     * use the {@link #waitSessionEnd()} method to monitor the session. Another
     * way to achieve that is via the
     * {@link org.jvoicexml.implementation.Telephony} interface and calling
     * {@link #getLastError()}. However, the latter option relies on the
     * concrete implementation.
     * </p>
     * <p>
     * Ensure that you call {@link #hangup()} after this call to ensure that
     * resources like ASR and TTS are released.
     * </p>
     * 
     * @param uri
     *            canonical URI of the first document to load. Relative URIs are
     *            not supported
     * @return called application
     * @exception ErrorEvent
     *                Error initiating the call.
     */
    Application call(URI uri) throws ErrorEvent;

    /**
     * Closes this session. After a session is closed, it can not be reopened
     * e.g., to call another application.
     *
     * @since 0.4
     */
    void hangup();

    /**
     * Retrieves the application that is currently being processed.
     * 
     * @return the current application
     * @since 0.7.7
     */
    Application getApplication();

    /**
     * Retrieves the DTMF input device.
     * 
     * @return DTMF input device.
     * @exception NoresourceError
     *                Input device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *                the user hung up
     * @since 0.5
     */
    DtmfInput getDtmfInput()
            throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Checks if this session has ended.
     * 
     * @return <code>true</code> if the session has ended.
     * @since 0.7.5
     */
    boolean hasEnded();

    /**
     * Delays until the session ends.
     * 
     * @exception ErrorEvent
     *                Error waiting for the end of the call.
     * @since 0.4
     */
    void waitSessionEnd() throws ErrorEvent;

    /**
     * Retrieves an error, if any, that happened during call processing.
     * 
     * @return an error that happened during call processing, <code>null</code>
     *         if there was no error.
     * @exception ErrorEvent
     *                Error reading the last error.
     * @since 0.7
     */
    ErrorEvent getLastError() throws ErrorEvent;
}
