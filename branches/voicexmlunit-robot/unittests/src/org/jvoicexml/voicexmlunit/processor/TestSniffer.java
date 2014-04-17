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
import java.net.URI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Test cases for {@link Sniffer}.
 * @author Raphael Groner
 *
 */
public class TestSniffer {
    
    URI mock;
    Sniffer sniffer;
    boolean started;
    boolean connected;
    boolean activated;


    /**
     * Set up the test environment
     * @throws org.jvoicexml.event.JVoiceXMLEvent
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        mock = new URI("unittests/etc/mock.vxml");
        sniffer = new Sniffer();

        started = false;
        connected = false;
        activated = false;
    }

    @Test
    public void test() {
        Assert.fail("tbd");
    }
}
