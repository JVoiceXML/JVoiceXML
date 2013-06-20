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
        try {
            out.receive(new SsmlDocument()); // must fail
        } catch (AssertionFailedError e) {
            failed = true;
        }
        assertTrue(failed);
    }

    /* (non-Javadoc)
     * @see org.jvoicexml.voicexmlunit.io.TestAssertion#testSend()
     */
    @Override
    @Test
    public void testSend() {
        try {
            out.send(new Recording(null, null)); // mock the server
        } catch (AssertionFailedError e) {
            failed = true;
        }
        assertTrue(failed);
    }

}
