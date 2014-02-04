package org.jvoicexml.voicexmlunit.stub;

import java.net.URI;
import org.jvoicexml.voicexmlunit.processor.Connection;
import org.jvoicexml.voicexmlunit.processor.Voice;

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
        final Voice voice = new Voice(uri);
        final Connection connection = new Connection(voice);
        return new org.jvoicexml.voicexmlunit.IVR(connection);
    }
}
