/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/Terminal.java $
 * Version: $LastChangedRevision: 2355 $
 * Date:    $Date: 2010-10-07 13:28:03 -0500 (jue, 07 oct 2010) $
 * Author:  $LastChangedBy: schnelle $
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

/**
 * Terminals are object that are waiting for incoming connections. Once
 * a connection to a terminal is established the interpreter is called
 * using the configured URI.
 * <p>
 * The term <em>terminal</em> is chosen as a tribute to JTAPI.
 * </p>
 * <p>
 * A terminal is responsible to accept incoming connections and to close the
 * connection once the user hangs up or if application terminates. Other
 * functionality, like streaming of audio to the phone should be handled by
 * the corresponding implementation of a
 * {@link org.jvoicexml.implementation.Telephony} implementation (if present) or
 * by the {@link org.jvoicexml.implementation.SpokenInput} or
 * {@link org.jvoicexml.implementation.SynthesizedOutput} implementations.
 * It may happen that the {@link org.jvoicexml.implementation.Telephony}
 * needs resources from the terminal to fulfill its job. In that case it
 * is advisable to store a reference to the terminal in a custom implementation
 * of a {@link org.jvoicexml.ConnectionInformation}. The
 * {@link org.jvoicexml.ConnectionInformation} will be passed as an argument to the
 * {@link org.jvoicexml.implementation.ExternalResource#connect(org.jvoicexml.ConnectionInformation)}
 * calls, once the resource is needed.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2355 $
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
     * This method is called if the {@link org.jvoicexml.CallManager} starts up.
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
