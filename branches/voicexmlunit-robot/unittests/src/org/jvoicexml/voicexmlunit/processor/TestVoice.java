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

import java.io.File;
import org.junit.*;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test cases for {@link Voice}.
 * @author Raphael Groner
 *
 */
public class TestVoice {

    private Voice voice;

    /**
     * Set up the test environment.
     */
    @Before
    public void setUp() throws JVoiceXMLEvent {
        voice = new Voice(9999);
        final File file = new File("unittests/etc/mock.vxml");
        voice.getDialog(file.toURI());
    }

    /**
     * Close the test environment.
     */
    @After
    public void tearDown() {
        voice.shutdown();
    }

    /**
     * Test method for {@link Voice#getSession()}.
     * Ensures a complete session run without any error.
     */
    @Test(timeout=9999)
    public void testSessionSuccess() throws ErrorEvent {
        Assert.assertNull(voice.getSession());

        Session session = voice.createSession(null);
        Assert.assertNotNull(session);
        Assert.assertEquals(voice.getSession(), session);
        
        session.waitSessionEnd();
        voice.shutdown();
        Assert.assertNull(voice.getSession());
    }
}