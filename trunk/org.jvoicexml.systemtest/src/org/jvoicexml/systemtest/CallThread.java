package org.jvoicexml.systemtest;

import java.net.URI;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

class CallThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(CallThread.class);
    
    URI _uri = null;
    JVoiceXml _jvxml = null;
    RemoteClient _client = null;
    
    Session session = null;

    CallThread(JVoiceXml jvxml, URI uri, RemoteClient client) {
        _client = client;
        _jvxml = jvxml;
        _uri = uri;
    }
    
    @Override
    public void run() {
        
        try {
            session = _jvxml.createSession(_client);
        } catch (ErrorEvent e) {
            LOGGER.error("create session error", e);
        }

        try {
            session.call(_uri);
            LOGGER.debug("session.call() returned.");
            session.waitSessionEnd();
            LOGGER.debug("session.waitSessionEnd() returned.");
        } catch (ErrorEvent ee) {
            LOGGER.error("ErrorEvent catched.", ee);
        } catch (Throwable t) {
            LOGGER.error("Throwable catched.", t);
        }
    }
}