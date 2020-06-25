/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Test;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.zanzibar.sip.SipServer;

import junit.framework.Assert;

/**
 * Test cases for {@link SipCallManager}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class SipCallManagerTest {

    /**
     * Test method for {@link SipCallManager#start()}.
     * @throws NoresourceError test failed
     * @throws IOException test failed
     * @throws Exception test failed
     */
    @Test
    public void testStart() throws NoresourceError, IOException, Exception {
        final SipCallManager manager = new SipCallManager();
        final SipServer server = new SipServer();
        manager.setSipServer(server);
        server.setDialogService(manager);
        server.setMySipAddress("sip:127.0.0.1:4245");
        server.setPort(4245);
        server.setCairoSipAddress("sip:cairo@speechforge.org");
        server.setCairoSipHostName("127.0.0.1");
        server.setCairoSipPort(5050);
        server.setStackName("Mrcpv2SessionManager");
        server.setTransport("UDP");
        Assert.assertFalse(manager.isStarted());
        manager.start();
        Assert.assertTrue(manager.isStarted());
        manager.stop();
        Assert.assertFalse(manager.isStarted());
    }
}
