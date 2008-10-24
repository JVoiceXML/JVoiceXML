package org.jvoicexml.systemtest;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.callmanager.CallManager;
import org.jvoicexml.event.error.NoresourceError;

/**
 * 
 * a Mock CallManager for system test
 * It do nothing
 * @author Zhang Nan
 * @version $Revision: $
 * @since 0.7
 */
public class MockCallManager implements CallManager {

    @Override
    public void start() throws NoresourceError {

    }

    @Override
    public void stop() {
    }

    @Override
    public void setJVoiceXml(JVoiceXml jvxml) {
    }

}
