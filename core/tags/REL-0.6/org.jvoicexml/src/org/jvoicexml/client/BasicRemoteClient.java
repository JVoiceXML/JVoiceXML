/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/client/rtp/RtpRemoteClient.java $
 * Version: $LastChangedRevision: 409 $
 * Date:    $Date: 2007-08-16 18:07:44 +0200 (Do, 16 Aug 2007) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.client;

import org.jvoicexml.RemoteClient;

/**
 * Basic Remote client configuration.
 *
 * <p>
 * This implementation is designed to transfer the minimum of the needed
 * information from the client to the JVoiceXml server. It may be
 * extended by custom implementations to transfer other client settings
 * that is needed by custom implementation platforms.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 409 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
@SuppressWarnings("serial")
public class BasicRemoteClient implements RemoteClient {
    /** Unique identifier for the call control. */
    private final String callControl;

    /** Unique identifier for the system output. */
    private final String systemOutput;

    /** Unique identifier for the user input. */
    private final String userInput;

    /**
     * Constructs a new object.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     */
    public BasicRemoteClient(final String call, final String output,
            final String input) {
        callControl = call;
        systemOutput = output;
        userInput = input;
    }

    /**
     * {@inheritDoc}
     */
    public final String getCallControl() {
        return callControl;
    }

    /**
     * {@inheritDoc}
     */
    public final String getSystemOutput() {
        return systemOutput;
    }

    /**
     * {@inheritDoc}
     */
    public final String getUserInput() {
        return userInput;
    }
}
