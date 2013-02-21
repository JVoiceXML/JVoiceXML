/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/CancelRequest.java $
 * Version: $LastChangedRevision: 3485 $
 * Date:    $Date: 2013-01-23 12:45:54 +0100 (Wed, 23 Jan 2013) $
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

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

import junit.framework.Assert;

public class Dtmf extends Statement {
    private char dtmf;

    public Dtmf(char dtmf) {
        super("DTMF '" + dtmf + "'");
        this.dtmf = dtmf;
    }

    public void receive(SsmlDocument actual) {
        Assert.fail("Receive " + getClass().getSimpleName() + ": " + actual);
    }

    public void send(Recording record) {
        if (record == null) {
            return;
        }

        try {
            record.input(dtmf);
        } catch (NoresourceError | ConnectionDisconnectHangupEvent e) {
            e.printStackTrace();
            Assert.fail("Send: " + toString());
        }
    }
}
