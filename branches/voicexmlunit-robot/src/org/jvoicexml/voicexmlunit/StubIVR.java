package org.jvoicexml.voicexmlunit;

import java.net.URI;
import org.jvoicexml.voicexmlunit.processor.Call;

/**
 * Stub to build IVR objects.
 * 
 * @author raphael
 */
public class StubIVR {
    public static IVR beginCall(final URI uri) {
        final Call call = new Call(uri);
        final IVR ivr = new IVR(call);
        call.setListener(ivr);
        return ivr;
    }
}
