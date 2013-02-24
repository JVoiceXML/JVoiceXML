package org.jvoicexml.voicexmlunit.io;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public interface TestAssertion {

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.io.Output#receive(org.jvoicexml.xml.ssml.SsmlDocument)}.
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     */
    @Test
    public abstract void testReceive() throws ParserConfigurationException,
            SAXException, IOException;

    /**
     * Test method for {@link org.jvoicexml.voicexmlunit.io.Output#send(org.jvoicexml.voicexmlunit.io.Recording)}.
     */
    @Test
    public abstract void testSend();

}