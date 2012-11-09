/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/ImplementationPlatform.java $
 * Version: $LastChangedRevision: 2931 $
 * Date:    $Date: 2012-02-06 01:57:38 -0600 (lun, 06 feb 2012) $
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

package org.jvoicexml;

import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * The <em>implementation platform</em> is controlled by the VoiceXML
 * interpreter context and by the VoiceXML interpreter.
 *
 * <p>
 * The implementation platform generates events in response to user actions
 * (e.g. spoken or character input received, disconnect) and system events (e.g.
 * timer expiration). Some of these events are acted upon the VoiceXML
 * interpreter itself, as specified by the VoiceXML document, while others are
 * acted upon by the VoiceXML interpreter context.
 * </p>
 *
 * <p>
 * This is a facade to access the resources that are capable to handle
 * those user actions.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2931 $
 *
 * @since 0.5.5
 */
public interface ImplementationPlatform extends PromptAccumulator {
    /**
     * Retrieves the audio output device.
     *
     * @return Audio output device to use, never <code>null</code>.
     * @exception NoresourceError
     *            Output device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    SystemOutput getSystemOutput()
        throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Delays until all prompts are played. This is needed e.g. for recording
     * to ensure that we do not record and play prompts in parallel.
     */
    void waitOutputQueueEmpty();

    /**
     * Delays until all prompts are played that do not allow for barge-in.
     * @since 0.7
     */
    void waitNonBargeInPlayed();

    /**
     * Checks, if there is an acquired user input device.
     * @return <code>true</code> if there is an acquired user input device. 
     * @since 0.7.3
     */
    boolean hasUserInput();

    /**
     * Retrieves the user input device.
     *
     * @return User input device to use, never <code>null</code>.
     * @exception NoresourceError
     *            Input device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    UserInput getUserInput()
        throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Retrieves the DTMF input device.
     *
     * @return DTMF input device to use.
     * @exception NoresourceError
     *            Input device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    CharacterInput getCharacterInput()
        throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Retrieves the calling device.
     *
     * @return Calling device to use, never <code>null</code>.
     * @exception NoresourceError
     *            Calling device is not available.
     * @exception ConnectionDisconnectHangupEvent
     *            the user hung up
     */
    CallControl getCallControl()
        throws NoresourceError, ConnectionDisconnectHangupEvent;

    /**
     * Closes all open resources.
     */
    void close();

    /**
     * Sets the event observer to communicate events back to the interpreter.
     * @param observer The event observer.
     *
     * @since 0.5
     */
    void setEventHandler(final EventObserver observer);

    /**
     * Sets the current session.
     * @param session the current session.
     */
    void setSession(final Session session);
}
