/**
 * 
 */
package org.jvoicexml.voicexmlunit;


import java.io.File;
import java.net.URI;
import java.net.UnknownHostException;

import junit.framework.AssertionFailedError;

import org.jvoicexml.Session;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;

import org.jvoicexml.event.ErrorEvent;

import org.jvoicexml.voicexmlunit.io.Recording;


/**
 * Call simulates a real telephony call.
 * This is done with creation of a new JVoiceXML session
 * and a TextServer that can be used to notice all events.
 * You have to call startDialog() in the started() event 
 * handler of your TextListener, otherwise the Server 
 * may fail. Your TextListener instance is registered with
 * setListener() method.
 * Lookup of JVoiceXml is done by help from Voice, you may 
 * use getVoice() to do some initialization before.
 * In case of an assertion failure, you can stop the Server,
 * therefore use the fail() and getFailure() methods.
 * 
 * @author thesis
 *
 */
public final class Call implements Runnable {
	private URI dialog;
	private TextServer server;
	private Voice voice;
	private AssertionFailedError error;
	
	static public int SERVER_PORT = 6000; // port number must be greater than 1024
	static public int SERVER_PORT_RANDOMIZE_COUNT = 100; // 0 means a fixed port number
	static public long SERVER_WAIT = 5000;
	
	/**
	 * Constructs a new call
	 * @param dialog resource to use for the call
	 */
	public Call(URI dialog) {
		super();
		this.dialog = dialog;
		this.server = new TextServer(randomizePortForServer());
		this.voice = null;
		this.error = null;
	}
	
	/**
	 * Constructs a new call
	 * @param path the path to a local file
	 */
	public Call(String path) {
		this(new File(path).toURI());
	}

	private int randomizePortForServer() {
		return (int) ((Math.random()*SERVER_PORT_RANDOMIZE_COUNT)+SERVER_PORT);
	}

	public void setListener(TextListener listener) {
		server.addTextListener(listener);
	}
	
	/**
	 * Get the Voice object
	 * This method tries best to get a valid object.
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
	 * @param voice the new Voice object
	 */
	public void setVoice(Voice voice) {
		this.voice = voice;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
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
				dialog.wait(SERVER_WAIT);
			}
			runDialog();
		} catch (InterruptedException | UnknownHostException | ErrorEvent e) {
			e.printStackTrace();
		} finally {
			server.stopServer();
		}
	}		

	
	/**
	 * Start the dialog
	 */
	public void startDialog() {
		synchronized (dialog) {
			dialog.notifyAll();
		}
	}

	private void runDialog() throws UnknownHostException, ErrorEvent {
		getVoice().connect(server.getConnectionInformation(),dialog);
	}
	
	/**
	 * Starts a transaction to send input
	 * @return transaction to use for the input
	 */
	public Recording record() {
		return new Recording(server,getVoice().getSession());
	}
	
	/**
	 * Sets the call into failure state and terminates the call process.
	 * @param error the error that has caused the failure
	 */
	public void fail(AssertionFailedError error) {
		if (this.error == null) { // only the first error
			server.interrupt();
			final Session session = getVoice().getSession();
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
