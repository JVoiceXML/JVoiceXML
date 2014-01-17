package org.jvoicexml.voicexmlunit.stub;

import java.net.URI;
import org.jvoicexml.voicexmlunit.processor.Connection;

/**
 * Stub to build IVR objects.
 * 
 * @author raphael
 */
public class IVR {
    /**
     * Build an IVR object.
     * 
     * @param uri the resource of the used voicexml document
     * @return
     */
    public static org.jvoicexml.voicexmlunit.IVR beginCall(final URI uri) {
        final Connection call = new Connection(uri);
        final org.jvoicexml.voicexmlunit.IVR ivr = new org.jvoicexml.voicexmlunit.IVR(call);
        call.setListener(ivr);
        return ivr;
    }
}
