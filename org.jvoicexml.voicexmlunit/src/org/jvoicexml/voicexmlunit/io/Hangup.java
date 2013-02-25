package org.jvoicexml.voicexmlunit.io;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * @author thesis
 * Simulation of an hangup event while there's still an assertion to process.
 * This is a wrapper for the assertion and the hangup is compared to it.
 */
public final class Hangup extends Output {
    
    private Assertion assertion;
    
    /**
     * @param statement the final statement
     */
    public Hangup(final Assertion assertion) {
        super("## disconnected ##");
        this.assertion = assertion;
    }
    
    /**
     * @return the statement converted into Ssml
     * @throws ParserConfigurationException
     */
    public SsmlDocument toSsml() 
            throws ParserConfigurationException {
        SsmlDocument doc = new SsmlDocument();
        if (assertion != null) {
            doc.getSpeak().setTextContent(assertion.toString());
        }
        return doc;
    }
}
