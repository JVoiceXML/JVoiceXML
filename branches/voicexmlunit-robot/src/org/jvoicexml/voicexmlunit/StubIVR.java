package org.jvoicexml.voicexmlunit;

import java.net.URI;
import org.jvoicexml.voicexmlunit.processor.Connection;

/**
 * Stub to build IVR objects.
 * 
 * @author raphael
 */
public class StubIVR {
    public static IVR beginCall(final URI uri) {
        final Connection call = new Connection(uri);
        final IVR ivr = new IVR(call);
        call.setListener(ivr);
        return ivr;
    }
}
