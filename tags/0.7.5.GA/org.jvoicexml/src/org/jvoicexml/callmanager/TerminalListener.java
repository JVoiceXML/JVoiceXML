/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;

/**
 * A listener to terminal events.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */

public interface TerminalListener {
    /**
     * A connection has been established with the given terminal.
     * @param terminal the connected terminal.
     * @param parameters additional call parameters
     */
    void terminalConnected(final Terminal terminal,
            final CallParameters parameters);

    /**
     * A connection has been disconnected with the given terminal.
     * @param terminal the disconnected terminal.
     */
    void terminalDisconnected(final Terminal terminal);

    /**
     * An error has occurred while interacting with the terminal.
     * @param terminal the terminal
     * @param message a detailed message about the error
     * @param cause the error cause.
     * @since 0.7.5
     */
    void terminalError(Terminal terminal, String message, Throwable cause);
}
