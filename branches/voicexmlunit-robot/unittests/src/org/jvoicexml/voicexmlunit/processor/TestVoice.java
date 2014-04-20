/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/JVoiceXmlStub.java $
 * Version: $LastChangedRevision: 2430 $
 * Date:    $Date: 2010-12-21 09:21:06 +0100 (Di, 21 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.processor;

import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.BasicConfigurator;
import org.junit.*;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.voicexmlunit.backend.Tweaker;
import org.jvoicexml.voicexmlunit.backend.Voice;

/**
 * Test cases for {@link Voice}.
 * @author Raphael Groner
 *
 */
public class TestVoice {

    private Voice voice;
    private URI uri;

    /**
     * Set up the test environment.
     * @throws org.jvoicexml.event.JVoiceXMLEvent
     * @throws org.jvoicexml.ConfigurationException
     * @throws java.net.URISyntaxException
     */
    @Before
    public void setUp() 
            throws JVoiceXMLEvent, ConfigurationException, URISyntaxException {
        final Tweaker tweak = new Tweaker("../org.jvoicexml/config");
        voice = new Voice(tweak);
        uri = new URI("unittests/etc/mock.vxml");
    }

    /**
     * Close the test environment.
     */
    @After
    public void tearDown() {
        
    }

    /**
     * Test method for {@link Voice#getSession()}.
     * Ensures a complete session run without any error.
     */
    @Test(timeout=9999)
    public void shouldHandleSessionSuccess() throws ErrorEvent {
        Assert.assertNull(voice.getSession());

        Session session = voice.dial(uri);
        Assert.assertNotNull(session);
        Assert.assertEquals(voice.getSession(), session);
        
        session.waitSessionEnd();
        voice.shutdown();
        Assert.assertNull(voice.getSession());
    }
}