/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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
package org.jvoicexml.callmanager.mrcpv2;

import java.io.IOException;

import org.jvoicexml.callmanager.Terminal;

/**
 * A SIP terminal manages a SIP phone that runs on the server side of JVoiceXML.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.3
 */
public final class SipTerminal implements Terminal {
    /** Name of this terminal. */
    private final String name;

    /**
     * Constructs a new object.
     * @param terminalName name of this terminal.
     */
    public SipTerminal(final String terminalName) {
        name = terminalName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitForConnections() throws IOException {
        // TODO Initialize the SIP stack for this terminal
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopWaiting() {
        // TODO shutdown the SIP stack for this terminal
        
    }


}
