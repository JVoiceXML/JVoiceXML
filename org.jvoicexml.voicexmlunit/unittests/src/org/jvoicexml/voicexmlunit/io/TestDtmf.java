/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.xml.sax.SAXException;

/**
 * Unit tests for {@link Dtmf}.
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public class TestDtmf implements AbstractTestAssertion {

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
    @Test(expected=AssertionError.class)
    public void testReceive() throws ParserConfigurationException,
            SAXException, IOException {
        dtmf.receive(new SsmlDocument()); // must fail
    }

    @Override
    @Test
    public void testSend() {
        dtmf.send(new Recording(null, null));
    }
}
