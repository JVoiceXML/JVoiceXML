package org.jvoicexml.voicexmlunit;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;


public class TestVoice implements TextListener {
    
    private static final long MAX_WAIT = 1000;
    private URI dialog;
    private Voice voice;
    private boolean active;

    @Before
    public void setUp() throws IOException {
        dialog = new File("unittests/rc/mock.vxml").toURI();
        voice = new Voice();
        voice.setPolicy("unittests/etc/jvoicexml.policy");
        voice.loadConfiguration("unittests/etc/jndi.properties");
        active = false;
    }
    
    @Test
    public void testLookup() throws IOException {
        // NOTICE: JVoiceXML has to run for test success!!
        Assert.assertNotNull(voice.getJVoiceXml());
        Assert.assertNotNull(voice.getContext());
    }
    
    @Test
    public void testSession() throws ErrorEvent, IOException, InterruptedException {
        Assert.assertNull(voice.getSession());
        
        final TextServer server = new TextServer(4711);
        server.addTextListener(this);
        server.start();
        synchronized (dialog) {
            dialog.wait(MAX_WAIT);
        }
        try {
            final ConnectionInformation connectionInformation = server.getConnectionInformation();;
            voice.connect(connectionInformation, dialog);
        } finally {
            server.stopServer();
        }
        Assert.assertNull(voice.getSession());
        Assert.assertTrue("active",active);
    }

    @Override
    public void started() {
        synchronized (dialog) {
            dialog.notifyAll();
        }
    }

    @Override
    public void connected(InetSocketAddress remote) {
        try {
            Thread.sleep(500); // delay
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void outputSsml(SsmlDocument document) {

    }

    @Override
    public void expectingInput() {

    }

    @Override
    public void inputClosed() {
        active = (voice.getSession() != null);
    }

    @Override
    public void disconnected() {

    }
}
