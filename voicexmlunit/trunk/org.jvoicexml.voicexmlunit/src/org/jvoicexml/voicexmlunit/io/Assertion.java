package org.jvoicexml.voicexmlunit.io;

/**
 * Assertion serves as the abstract for Output and Input statements
 * 
 * @author thesis
 *
 */
public interface Assertion {
	
	/**
	 * @return the expected string
	 */
	public abstract String toString();

	/**
	 * Receive an output
	 * @param actual the output to receive
	 */
	public abstract void receive(String actual);

	/**
	 * Send an input
	 * @param record the transaction used to input
	 */
	public abstract void send(Recording record);

}