package org.jvoicexml.voicexmlunit;

import java.net.URI;

/**
 * Stub to build Caller objects.
 * 
 * @author raphael
 */
public class StubCaller {
    public static Caller beginCall(final URI uri) {
        return new Caller(StubIVR.beginCall(uri));
    }
}
