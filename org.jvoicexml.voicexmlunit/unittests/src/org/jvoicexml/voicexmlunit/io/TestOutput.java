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

import java.lang.AssertionError;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ComparisonFailure;
import org.junit.Test;

import org.jvoicexml.xml.ssml.SsmlDocument;

import org.xml.sax.SAXException;

/**
 * @author thesis
 *
 */
public class TestOutput implements AbstractTestAssertion {

    private Output out;
    private boolean failed;

    /* (non-Javadoc)
     * @see org.jvoicexml.voicexmlunit.io.TestAssertion#setUp()
     */
    @Before
    public void setUp() throws Exception {
        out = new Output("123");
        failed = false;
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.voicexmlunit.io.TestAssertion#testReceive()
     */
    @Override
    @Test
    public void testReceive() throws ParserConfigurationException, SAXException, IOException {
        out.receive(out.toString());
    }

    @Test(expected=AssertionError.class)
    public void testReceiveEmpty() throws ParserConfigurationException, SAXException, IOException {
        out.receive(new SsmlDocument()); // must fail
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.voicexmlunit.io.TestAssertion#testSend()
     */
    @Override
    @Test(expected=AssertionError.class)
    public void testSend() {
        out.send(new Recording(null, null)); // mock the server
    }
}
