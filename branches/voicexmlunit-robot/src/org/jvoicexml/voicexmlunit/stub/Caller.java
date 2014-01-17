package org.jvoicexml.voicexmlunit.stub;

import java.net.URI;

/**
 * Stub to build Caller objects.
 * 
 * @author raphael
 */
public class Caller {
    /**
     * Build a new Caller object.
     * 
     * @param uri the resource of the used voicexml document
     * @return
     */
    public static org.jvoicexml.voicexmlunit.Caller beginCall(final URI uri) {
        return new org.jvoicexml.voicexmlunit.Caller(IVR.beginCall(uri));
    }
}
