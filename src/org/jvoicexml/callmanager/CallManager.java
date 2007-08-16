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

import java.net.URI;

import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Manager for telephony integration.
 *
 * <p>
 * The call manager has several tasks
 * <ol>
 * <li>
 * maintain a list of terminal as an interface to the telephony environment
 * </li>
 * <li>
 * manage a mapping of terminals to an URI of the starting document of an
 * application.
 * </li>
 * <li>
 * initiate calls in JVoiceXML and call the configured URI for the terminal.
 * </li>
 * </ol>
 * </p>
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public interface CallManager {
    /** Configuration key. */
    String CONFIG_KEY = "callmanager";

    /**
     * Sets a reference to JVoiceXml.
     * @param jvxml reference to JVoiceXml.
     */
    void setJVoiceXml(final JVoiceXml jvxml);

    /**
     * Starts the call manager.
     * @exception NoresourceError
     *      Error starting the call manager.
     */
    void start() throws NoresourceError;

    /**
     * Stops the call manager.
     */
    void stop();

    /**
     * Adds the terminal with the given URI to the list of known terminals.
     *
     * @param terminal
     *            identifier for the terminal
     * @param application
     *            URI of the application to add.
     * @return <code>true</code> if the terminal was added.
     */
    boolean addTerminal(final String terminal, final URI application);
}
