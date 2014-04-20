package org.jvoicexml.voicexmlunit.stub;

import java.net.URI;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * Stub to build IVR objects.
 * 
 * @author raphael
 */
public class IVR {
    /**
     * Build an IVR object.
     * 
     * @param configuration path to configuration folder
     * @param uri the resource of the used voicexml document
     * @param timeout how long will be waited till session termination
     * @return
     * @throws org.jvoicexml.event.JVoiceXMLEvent
     * @throws org.jvoicexml.ConfigurationException
     */
    public static org.jvoicexml.voicexmlunit.IVR beginCall(
            final String configuration, final URI uri, final long timeout) 
            throws JVoiceXMLEvent, ConfigurationException {
        return new org.jvoicexml.voicexmlunit.IVR(configuration, uri, timeout);
    }
}
