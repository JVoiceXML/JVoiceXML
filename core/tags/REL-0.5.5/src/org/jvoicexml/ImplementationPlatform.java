/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml;

import org.jvoicexml.event.EventObserver;
import org.jvoicexml.event.error.NoresourceError;

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
 * @see org.jvoicexml.interpreter.VoiceXmlInterpreter
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @since 0.5.5
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface ImplementationPlatform {

    /**
     *Retrieves the audio output device.
     *
     * @return Audio output device to use.
     * @exception NoresourceError
     *            Output device is not available.
     */
    SystemOutput getSystemOutput()
        throws NoresourceError;

    /**
     * Retrieves the user input device.
     *
     * @return User input device to use.
     * @exception NoresourceError
     *            Input device is not available.
     */
    UserInput getUserInput()
        throws NoresourceError;

    /**
     * Retrieves the DTMF input device.
     *
     * @return DTMF input device to use.
     * @exception NoresourceError
     *            Input device is not available.
     */
    CharacterInput getCharacterInput()
        throws NoresourceError;

    /**
     * Retrieves the calling device.
     *
     * @return Calling device to use.
     * @exception NoresourceError
     *            Calling device is not available.
     */
    CallControl getCallControl()
        throws NoresourceError;

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
}
