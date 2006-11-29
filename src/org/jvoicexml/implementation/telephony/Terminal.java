/*
 * File:    $RCSfile: Terminal.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.telephony;

/**
 * A Terminal represents a telephony endpoint managed by a JVoiceXML
 * provider - i.e. It is the line that a person calls to interact with a
 * VoiceXML application.
 *
 * @author Davide Bruzzone
 * @since 0.3
 */
public interface Terminal {
    /**
     * Returns the terminal's name. A provider's terminals must each
     * have a unique name (but different providers' terminals can have
     * the same name).
     *
     * @return The terminal's name
     */
    String getName();

    /**
     * Sets the terminal's name.  A provider's terminals must each
     * have a unique name (but different providers' terminals can have
     * the same name).
     *
     * @param name The terminal's name
     */
    void setName(String name);

    /**
     * Answers an incoming call.
     */
    void answer();

    /**
     * Hangs up a call that's currently in progress.
     */
    void hangup();

    /**
     * Plays the specified file.
     *
     * @param filename The name of the file to play
     */
    void playFile(String filename);

    /**
     * Records incoming audio (i.e. Spoken or DTMF commands) in the
     * specified file.
     *
     * @param filename The name of the file in which incoming audio
     *                 is recorded
     */
    void record(String filename);
}
