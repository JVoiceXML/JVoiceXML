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
public class TestOutput implements TestAssertion {
    
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
