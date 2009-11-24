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

import java.io.IOException;

import org.jvoicexml.CallManager;

/**
 * Terminals are object that are waiting for incoming connections. Once
 * a connection to a terminal is established the interpreter is called
 * using the configured URI.
 * <p>
 * The term <em>terminal</em> is chosen as a tribute to JTAPI.
 * </p>
 * @author DS01191
 * @version $Revision$
 * @since 0.7
 */

public interface Terminal {
    /**
     * Retrieves the name of a terminal.
     * @return name of the terminal
     */
    String getName();

    /**
     * Starts waiting for incoming connections. This method is expected to run
     * asynchronously.
     * <p>
     * This method is called if the {@link CallManager} starts up.
     * </p>
     * @throws IOException
     *         error waiting for connections.
     */
    void waitForConnections() throws IOException;

    /**
     * Stops waiting for incoming connections.
     */
    void stopWaiting();
}
