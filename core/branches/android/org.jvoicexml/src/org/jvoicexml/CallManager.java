/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/CallManager.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;

import org.jvoicexml.event.error.NoresourceError;

/**
 * Manager for telephony integration to allow for <em>real</em> client-server
 * scenarios.
 * <p>
 * The {@link CallManager} is started asynchronously via the {@link #start()}
 * method when JVoiceXML starts. The {@link CallManager} starts as a server,
 * waiting for incoming connections, e.g. from a PBX. Once a call arrives
 * it creates a {@link org.jvoicexml.Session} using the {@link JVoiceXml}
 * reference that is delivered via the {@link #setJVoiceXml(JVoiceXml)} method.
 * </p>
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
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
     * @exception IOException
     *      unable to start a terminal
     */
    void start() throws NoresourceError, IOException;

    /**
     * Stops the call manager and all terminals.
     */
    void stop();
}
