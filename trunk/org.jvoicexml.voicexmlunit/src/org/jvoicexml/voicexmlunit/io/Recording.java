/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.io;

import java.io.IOException;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Recording transacts and abstracts the model for the Supervisor and the
 * individual Statement it wants to send.
 * 
 * @author thesis
 * 
 */
public class Recording {
    private TextServer server;
    private Session session;

    /**
     * Constructs a Recording
     * 
     * @param server
     *            the server to send something
     */
    public Recording(TextServer server, Session session) {
        super();
        this.server = server;
        this.session = session;
    }

    /**
     * Input a text to the underlying server
     * 
     * @param text
     * @throws IOException
     */
    public void input(String text) throws IOException {
        if (server != null) {
            server.sendInput(text);
        }
    }

    public void input(char dtmf) throws NoresourceError,
            ConnectionDisconnectHangupEvent {
        if (session != null) {
            CharacterInput input = session.getCharacterInput();
            input.addCharacter(dtmf);
        }
    }
}
