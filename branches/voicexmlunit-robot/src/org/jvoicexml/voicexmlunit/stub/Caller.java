package org.jvoicexml.voicexmlunit.stub;

import java.net.URI;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Stub to build Caller objects.
 * 
 * @author raphael
 */
public class Caller {
    private static long TIMEOUT = 2500;
    
    /**
     * Build a new Caller object.
     * 
     * @param uri the resource of the used voicexml document
     * @return
     */
    public static org.jvoicexml.voicexmlunit.Caller beginCall(final URI uri) 
            throws JVoiceXMLEvent {
        return new org.jvoicexml.voicexmlunit.Caller(
                IVR.beginCall(uri, TIMEOUT));
    }
}
