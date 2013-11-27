package org.jvoicexml.voicexmlunit;

import java.net.InetSocketAddress;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Monitor that enables delaying until JVoiceXML expects an input.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class InputMonitor implements TextListener {
    /** Synchronization. */
    private final Object monitor;

    /**
     * Constructs a new object.
     */
    public InputMonitor() {
        monitor = new Object();
    }

    /**
     * Waits until JVoiceXml is expecting input.
     * @throws InterruptedException
     *         waiting interrupted
     */
    public void waitUntilExpectingInput() throws InterruptedException {
        synchronized (monitor) {
            monitor.wait();
        }
    }

    @Override
    public void started() {
    }

    @Override
    public void connected(final InetSocketAddress remote) {
    }

    @Override
    public void outputSsml(final SsmlDocument document) {
    }

    @Override
    public void expectingInput() {
        synchronized (monitor) {
            monitor.notifyAll();
        }
    }

    @Override
    public void inputClosed() {
    }

    @Override
    public void disconnected() {
    }

}
