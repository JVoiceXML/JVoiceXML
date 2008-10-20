package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * It monitor TestServer received system out speak messages, 
 * and generate suitable answer.
 * if test pass or fail, the answer is PASS or FAIL.
 * Not cover every thing yet, in developing.
 * 
 * @author lancer
 *
 */
public class AnswerGenerator implements TextListener {
	/** Logger for this class. */
	private static final Logger LOGGER = Logger
			.getLogger(AnswerGenerator.class);

	private BlockingQueue<String> outputMessages = new LinkedBlockingQueue<String>();



	/** 
	 * 
	 * @return suitable answer.
	 */
	public String next() {
		LOGGER.debug("next() begin");
		String message = null;
		try {
			message = outputMessages.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (message == null) {
			return null;
		}
		LOGGER.debug("got message : " + message);
		if (message.indexOf("Press") > 0) {
			int index = message.indexOf("'");
			message = message.substring(index + 1, index + 2);
		}
		if (message.indexOf("pass") > 0) {
			message = "PASS";
		}
		if (message.indexOf("fail") > 0) {
			message = "FAIL";
		}
		return message;
	}

	/**
	 * 
	 * @return true if have more answers.
	 */
	public boolean hasMore() {
		synchronized (outputMessages) {
			return outputMessages.isEmpty() ? false : true;
		}
	}
	
	@Override
	public void outputSsml(SsmlDocument arg0) {
		LOGGER.info("Received SsmlDocument : " + arg0.toString());

		try {
			outputMessages.put(arg0.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void outputText(String arg0) {
		LOGGER.info("Received Text : " + arg0);
		
		try {
			outputMessages.put(arg0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void connected(InetSocketAddress remote) {
		LOGGER.debug("connected to " + remote.toString());
	}

	@Override
	public void disconnected() {
		LOGGER.debug("disconnected");
	}

}
