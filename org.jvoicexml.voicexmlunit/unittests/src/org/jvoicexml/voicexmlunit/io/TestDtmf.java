/**
 * 
 */
package org.jvoicexml.voicexmlunit.io;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.junit.After;
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
