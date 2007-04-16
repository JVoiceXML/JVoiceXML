package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import org.jvoicexml.callmanager.CallControlListener;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @version 1.0
 */
public class Test implements CallControlListener {

    JtapiCallControl _call;

    /**
     *
     * @param call CallControlImpl
     * @param app String
     */

    public Test(JtapiCallControl call, String app) {

        _call = call;

        _call.addListener(this);

        //final Session session = jvxml.createSession(null, application);

    }

    /**
     *
     */
    public void answered() {

        try { //play
            _call.play((new URI("intro")));
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }

        try {
            prompt();
            _call.stopPlay();
        } catch (IOException ex2) {
        }

        try { //bargeIn
            _call.play((new URI("intro")));
            _call.record((new URI("david")));
        } catch (URISyntaxException ex1) {
            ex1.printStackTrace();
        }
        try {
            System.err.println("\n please press enter to stop to record");
            prompt();
        } catch (IOException ex2) {
        }
        _call.stopRecord();

    }
    /**
     *
     */
    public void hangedup() {
        System.err.println("\n hangedup ");
    }
    /**
     *
     */
    public void playStarted() {
        System.err.println("\n playStarted ");
    }
    /**
     *
     */
    public void playStopped() {
        System.err.println("\n playStopped ");

    }
    /**
     *
     */
    public void recordStarted() {
        System.err.println("\n recordStarted ");

    }
    /**
     *
     */
    public void recordStopped() {
        System.err.println("\n recordStopped ");

    }

    /**
     * Prompt for user input.
     */
    protected static void prompt() throws IOException {
        InputStream in = System.in;

        System.out.println("Hit return to continue...");
        // wait
        in.read();
        in.skip(in.available());
    }

}
