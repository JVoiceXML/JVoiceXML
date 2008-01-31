/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.implementation.CharacterInput;

/**
 * A session begins when the user starts to interact with a VoiceXML interpreter
 * context, continues as documents are loaded and processed, and ends when
 * requested by the user, a document, or the interpreter context.
 *
 * <p>
 * A <code>Session</code> can be obtained via the
 * <code>JVoiceXml.createSession()</code> method.
 * </p>
 *
 * <p>
 * A client usually performs the following method calls to interact with
 * the interpreter:
 * <ol>
 * <li>{@link #call(URI) call}</li>
 * <li>{@link #hangup() hangup}</li>
 * <li>{@link #close() close}</li>
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
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @see org.jvoicexml.JVoiceXml#createSession(RemoteClient)
 *
 */
public interface Session {
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
     *
     * @param uri URI of the first document to load.
     *
     * @exception ErrorEvent
     *            Error initiating the call.
     */
    void call(final URI uri)
            throws ErrorEvent;


    /**
     * Handles a hangup request.
     *
     * @exception ErrorEvent
     *            Error terminating the call.
     * @since 0.4
     */
    void hangup()
        throws ErrorEvent;

    /**
     * Retrieves the DTMF input device.
     * @return DTMF input device.
     * @exception NoresourceError
     *            Input device is not available.
     *
     * @since 0.5
     */
    CharacterInput getCharacterInput()
            throws NoresourceError;

    /**
     * Delays until the session ends.
     * @exception ErrorEvent
     *            Error processing the call.
     * @since 0.4
     */
    void waitSessionEnd()
            throws ErrorEvent;

    /**
     * Closes this session. After a session is closed, it can not be
     * reopened e.g., to call another application.
     *
     * <p>
     * If no hangup call was initiated, the session is aborted.
     * </p>
     *
     * <p>
     * The current implementation frees all resources when this method is
     * called. This means in turn that the resources are not freed if the
     * client crashes without calling {@link #close()} thus requiring a restart
     * of the interpreter. This has to be fixed.
     * </p>
     */
    void close();
}
