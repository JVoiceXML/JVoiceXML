package org.jvoicexml.voicexmlunit;


import java.net.InetSocketAddress;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.jvoicexml.client.text.TextListener;

import org.jvoicexml.voicexmlunit.io.Assertion;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;
import org.jvoicexml.voicexmlunit.io.Recording;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;


/**
 * Supervisor can help you to write unit tests for VoiceXML documents.
 * Use case scenario:
 * 1. Create an instance of Call with your wished VoiceXML resource.
 * 2. Initialize a new conversation with your Call object.
 * 3. Process the given VoiceXML file. 
 * 
 * @author thesis
 *
 */
public final class Supervisor implements TextListener {
	
	private Call call = null;
	private Conversation conversation = null;
	private Assertion statement = null;

	/**
	 * Initialize a new server conversation
	 * @param call the call object
	 * @return Conversation to be used and initialized by the caller
	 */
	public Conversation init(Call call) {
		this.call = call;
		if (call != null) { // null means a mock object
			call.setListener(this);
		}
		conversation = new Conversation();
		return conversation;
	}

	/**
	 * Process a VoiceXML file and generate test log
	 */
	public void process() {
		if (call == null) {
			return;
		}
		
		statement = conversation.begin();
		call.run();
			
		AssertionFailedError error = call.getFailure();
		if (error != null) {
			error.printStackTrace();
			Assert.fail(error.toString());
		}
	}

	/**
	 * Assert the expected count of conversation statements
	 * @param expectedCount How many statements should we have?
	 */
	public void assertStatements(int expectedCount) throws AssertionFailedError {
		Assert.assertEquals("Statements",expectedCount,conversation.countStatements());
	}

	/**
	 * Assert that the current statement is an Output instance with the given message
	 * @param message Message to expect in the call
	 */
	public void assertOutput(String message) throws AssertionFailedError {
		if (statement == null) {
			Assert.assertEquals(Output.class.getSimpleName(),message,"## nothing ##");
		} else {
			statement.receive(message);
			statement = conversation.next();
		}
	}
	
	/**
	 * Assert that the current statement is an Input instance and the actual message can be send.´
	 */
	public void assertInput() throws AssertionFailedError {
		if (statement == null) {
			Assert.fail(Input.class.getSimpleName()+" expected");
		} else {
			Recording record;
			if (call == null) {
				record = new Recording(null,null); // mock
			} else {
				record = call.record();
			}
			statement.send(record);
			statement = conversation.next();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#started()
	 */
	public void started() {
		call.startDialog();
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#connected(java.net.InetSocketAddress)
	 */
	public void connected(final InetSocketAddress remote) {
		statement = conversation.begin();
	}
	
	/**
	 * Some text has received, test it with expectation.
	 * This is a method from an older TextListener interface and is
	 * kept for compatibility.
	 * @param text the received text
	 */
	public void outputText(final String text) {
		try {
			assertOutput(text);
		}
		catch (AssertionFailedError e) {
			call.fail(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#outputSsml(org.jvoicexml.xml.ssml.SsmlDocument)
	 */
	public void outputSsml(final SsmlDocument document) {
		//TODO better handling of the XML structure inside (xpath?)
		if (document != null) {
			Speak speak = document.getSpeak();
			if (speak != null) {
				final String text = speak.getTextContent();
				outputText(text);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#expectingInput()
	 */
	public void expectingInput() {
		try {
			assertInput();
		}
		catch (AssertionFailedError e) {
			call.fail(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#inputClosed()
	 */
	public void inputClosed() {
		
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.client.text.TextListener#disconnected()
	 */
	public void disconnected() {
		try {
			if (statement != null) {
				assertOutput("## disconnected ##"); // fails always, intentionally
			}
		}
		catch (AssertionFailedError e) {
			if (call == null) {
				Assert.fail(e.getMessage());
			}
			else {
				call.fail(e);
			}
		}
	}
}