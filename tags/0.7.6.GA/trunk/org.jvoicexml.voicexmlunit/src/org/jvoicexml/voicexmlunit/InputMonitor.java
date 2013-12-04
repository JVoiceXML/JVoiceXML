package org.jvoicexml.voicexmlunit;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeoutException;

import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Monitor that enables delaying until JVoiceXML expects an input.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class InputMonitor implements TextListener {
    /** Synchronization. */
    private final Object monitor;
    /** Set to true, if input is expected. */
    private boolean expectingInput;
    /** Caught event while waiting for an input. */
    private JVoiceXMLEvent event;

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

    /**
     * Waits until JVoiceXml is expecting input.
     * @param timeout the timeout to wait at max in msec, waits forever, if
     *          timeout is zero
     * @throws InterruptedException
     *         waiting interrupted
     * @throws TimeoutException
     *         waiting time exceeded
     * @throws JVoiceXMLEvent
     *         error while waiting
     */
    public void waitUntilExpectingInput(final long timeout)
            throws InterruptedException, TimeoutException, JVoiceXMLEvent {
        synchronized (monitor) {
            monitor.wait(timeout);
            if (event != null) {
                throw event;
            }
            if (!expectingInput) {
                throw new TimeoutException("timeout of '" + timeout
                        + "' msec exceeded while waiting for expected input");
            }
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
            expectingInput = false;
            monitor.notifyAll();
        }
    }

    @Override
    public void inputClosed() {
    }

    @Override
    public void disconnected() {
        synchronized (monitor) {
            event = new ConnectionDisconnectHangupEvent();
            monitor.notifyAll();
        }
    }

}
