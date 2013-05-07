/**
 * 
 */
package org.jvoicexml.voicexmlunit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import junit.framework.AssertionFailedError;

import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.event.ErrorEvent;

import org.jvoicexml.voicexmlunit.io.Recording;

/**
 * Call simulates a real telephony call. This is done with creation of a new
 * JVoiceXML session and a TextServer that can be used to notice all events. You
 * have to call startDialog() in the started() event handler of your
 * TextListener, otherwise the Server may fail. Your TextListener instance is
 * registered with setListener() method. Lookup of JVoiceXml is done by help
 * from Voice, you may use getVoice() to do some initialization before. In case
 * of an assertion failure, you can stop the Server, therefore use the fail()
 * and getFailure() methods.
 * 
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 * 
 */
public final class Call implements Runnable {
    private URI dialog;
    private TextServer server;
    private Voice voice;
    private AssertionFailedError error;

    public static int SERVER_PORT = 6000; // port number must be greater than
                                          // 1024
    public static int SERVER_PORT_RANDOMIZE_COUNT = 100; // 0 means a fixed port
                                                         // number
    public static long SERVER_WAIT = 5000;

    /** Synchronization lock. */
    private final Object lock;

    /**
     * Constructs a new call
     * 
     * @param uri
     *            URI of the dialog to call call
     */
    public Call(final URI uri) {
        super();
        dialog = uri;
        final int port = randomizePortForServer();
        server = new TextServer(port);
        lock = new Object();
    }

    /**
     * Constructs a new call
     * 
     * @param path
     *            the path to a local file
     */
    public Call(final String path) {
        this((new File(path)).toURI());
    }

    private int randomizePortForServer() {
        return (int) ((Math.random() * SERVER_PORT_RANDOMIZE_COUNT) + SERVER_PORT);
    }

    public void setListener(final TextListener listener) {
        server.addTextListener(listener);
    }

    /**
     * Get the Voice object This method tries best to get a valid object.
     * 
     * @return the actual Voice object
     */
    public Voice getVoice() {
        if (voice == null) {
            setVoice(new Voice());
        }
        return voice;
    }

    /**
     * Set a custom Voice object
     * 
     * @param voice
     *            the new Voice object
     */
    public void setVoice(final Voice voice) {
        this.voice = voice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (dialog == null) {
            return;
        }

        error = null;
        server.start();
        try {
            /* wait for the server */
            synchronized (dialog) {
                lock.wait(SERVER_WAIT);
            }
            runDialog();
        } catch (InterruptedException | IOException | ErrorEvent e) {
             fail(new AssertionFailedError(e.getMessage()));
        } finally {
            server.stopServer();
        }
    }

    /**
     * Start the dialog
     */
    public void startDialog() {
        synchronized (dialog) {
            lock.notifyAll();
        }
    }

    /**
     * Runs the configured dialog.
     * @throws ErrorEvent
     * @throws IOException
     */
    private void runDialog() throws ErrorEvent, IOException {
        final Voice voice = getVoice();
        final ConnectionInformation info = server.getConnectionInformation();
        voice.connect(info, dialog);
    }

    /**
     * Starts a transaction to send input
     * 
     * @return transaction to use for the input
     */
    public Recording record() {
        final Voice voice = getVoice();
        final Session session = voice.getSession();
        return new Recording(server, session);
    }

    /**
     * Sets the call into failure state and terminates the call process.
     * 
     * @param error
     *            the error that has caused the failure
     */
    public void fail(final AssertionFailedError error) {
        if (this.error == null) { // only the first error
            server.interrupt();
            final Voice voice = getVoice();
            final Session session = voice.getSession();
            if (session != null) {
                session.hangup();
            }
            this.error = error;
        }
    }

    public AssertionFailedError getFailure() {
        return error;
    }
}
