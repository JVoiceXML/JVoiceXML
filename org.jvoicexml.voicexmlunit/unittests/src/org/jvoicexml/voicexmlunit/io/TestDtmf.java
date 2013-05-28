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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.xml.sax.SAXException;

/**
 * @author thesis
 *
 */
public class TestDtmf implements TestAssertion {

    private static final char[] DIGITS = "1234567890".toCharArray();
    
    private char digit;
    private Dtmf dtmf;
    private boolean failed;
    
    @Before
    public void setUp() throws Exception {
        Integer i = (int) (Math.random() * 10);
        digit = DIGITS[i];
        dtmf = new Dtmf(digit);
        failed = false;
    }

    @Override
    @Test
    public void testReceive() throws ParserConfigurationException,
            SAXException, IOException {
        try {
            dtmf.receive(new SsmlDocument()); // must fail
        } catch (AssertionFailedError e) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Override
    @Test
    public void testSend() {
        dtmf.send(new Recording(null, null));        
    }
}
