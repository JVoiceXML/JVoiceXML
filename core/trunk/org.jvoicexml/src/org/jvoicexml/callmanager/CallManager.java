/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Manager for telephony integration.
 *
 * <p>
 * The call manager has several tasks
 * <ol>
 * <li>
 * manage a mapping of terminals to an URI of the starting document of an
 * application through the {@link ConfiguredApplication}
 * </li>
 * <li>
 * maintain a list of {@link Terminal}s as an interface to the telephony
 * environment
 * </li>
 * <li>
 * initiate calls in JVoiceXML and call the configured URI for the terminal.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * The {@link CallManager} is started asynchronously via the {@link #start()}
 * method when JVoiceXML starts. The {@link CallManager} starts as a server,
 * waiting for incoming connections, e.g. from a PBX. Once a call arrives
 * it creates a {@link org.jvoicexml.Session} using the {@link JVoiceXml}
 * reference that is delivered via the {@link #setJVoiceXml(JVoiceXml)} method.
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.6
 */
public interface CallManager {
    /**
     * Sets a reference to JVoiceXml.
     * @param jvxml reference to JVoiceXml.
     */
    void setJVoiceXml(final JVoiceXml jvxml);

    /**
     * Starts the call manager asynchronously.
     * <p>
     * This means that all terminals are initialized and started. Upon a
     * successful run, all terminal are waiting for incoming connections.
     * </p>
     * @exception NoresourceError
     *      Error starting the call manager.
     */
    void start() throws NoresourceError;

    /**
     * Stops the call manager and all terminals.
     */
    void stop();
}
