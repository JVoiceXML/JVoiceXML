/**
 * 
 */
package org.jvoicexml.voicexmlunit;

import org.jvoicexml.client.text.TextServer;

/**
 * Runner can be used to go through a test run, without
 * having to bother about still open resources afterwards.
 * 
 * @author thesis
 *
 */
public final class Runner implements Runnable {

	private TextServer server;
	private String path;
	private Supervisor supervisor;
	private int serverPort;
	
	public Runner(String path, Supervisor supervisor) {
		server = new TextServer(randomizePortForServer());
		this.path = path;
		this.supervisor = supervisor;
	}
	
	private int randomizePortForServer() {
		// port number must be greater than 1024
		serverPort = (int) ((Math.random()*5000)+1024);
		return serverPort;
	}
	
	/**
	 * @return the server
	 */
	public TextServer getServer() {
		return server;
	}

	/**
	 * @return the port used to create the TextServer object
	 */
	public int getServerPort() {
		return serverPort;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		server.start();	
		if (path != null) {
			supervisor.process(path);
		}
		server.stopServer();
	}
}
