package org.jvoicexml.voicexmlunit;


import java.util.LinkedList;
import java.util.ListIterator;

import org.jvoicexml.voicexmlunit.io.Assertion;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Statement;


/**
 * Conversation is a helper for the communication in between
 * the processing of JVoiceXML, Supervisor and TextServer.
 * You can add Output and Inputs objects in the order you think
 * to expect them coming from the VoiceXML document interpreter.
 * Output and Input are of the abstract type Statement with an
 * indivual message text.
 * 
 * @author thesis
 *
 */
public final class Conversation {
	private LinkedList<Statement> history;
	private ListIterator<Statement> iterator;
	
	/**
	 * Constructor
	 */
	public Conversation() {
		history = new LinkedList<Statement>();
		iterator = null;
	}

	/**
	 * Add a new Output with the expected message
	 * @param message Message to expect
	 */
	public void addOutput(String message) {
		Output output = new Output(message);
		history.add(output);
	}

	/**
	 * Add a new Input with the message to be send
	 * @param message Message to send
	 */
	public void addInput(String message) {
		Input input = new Input(message);
		history.add(input);
	}
	
	/**
	 * Begin the conversation
	 * @return First statement of the conversation
	 */
	public Assertion begin() {
		if (history.isEmpty()) {
			iterator = null; // invalidate any existing cursor
			return null;
		}
		else {
			iterator = history.listIterator(0);
			return iterator.next();
		}
	}
	
	/**
	 * Go to the next statement in the conversation
	 * If there are no more elements left, this method invalidates 
	 * the conversation and returns an invalid object.
	 * @return Next statement after the previously current one
	 */
	public Assertion next() {
		if (iterator == null) {
			return begin();
		}
		else if (iterator.hasNext()) {
			return iterator.next();
		}
		else {
			iterator = null;
			return null;
		}
	}

	/**
	 * @return Count of so far collected statements wit addOutput/addInput
	 */
	public int countStatements() {
		return history.size();
	}
}
