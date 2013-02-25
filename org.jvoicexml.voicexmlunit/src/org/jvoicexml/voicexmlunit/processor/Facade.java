package org.jvoicexml.voicexmlunit.processor;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.xml.ssml.SsmlDocument;

public interface Facade {
    
    void assertOutput(final SsmlDocument message);
    
    void assertInput();
    
    void assertHangup();

}