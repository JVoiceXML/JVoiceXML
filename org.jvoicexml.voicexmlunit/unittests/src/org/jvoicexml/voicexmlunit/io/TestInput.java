package org.jvoicexml.voicexmlunit.io;

import static org.junit.Assert.*;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.xml.ssml.SsmlDocument;

public class TestInput implements TestAssertion {
    
    private Input in;
    boolean failed;

    @Before
    public void setUp() throws Exception {
        in = new Input("abc");
        failed = false;
    }

    @Override
    @Test
    public void testReceive() throws ParserConfigurationException {
        try {
            in.receive(new SsmlDocument());
        } catch (AssertionFailedError e) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Override
    @Test
    public void testSend() {
        in.send(new Recording(null, null)); // mock the server
    }

}
