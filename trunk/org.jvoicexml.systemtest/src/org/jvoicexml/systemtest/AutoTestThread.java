package org.jvoicexml.systemtest;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.systemtest.testcase.IRTestCase;

/**
 * AutoTestThread as the name
 * @author lancer
 *
 */
class AutoTestThread extends Thread {
	/** Logger for this class. */
	private static final Logger LOGGER = Logger.getLogger(AutoTestThread.class);

	TextServer textServer;
	List<IRTestCase> testcaseList;
	JVoiceXml jvxml = null;

	CallThread callThread = null;
	int textServerPort = 0;

	public AutoTestThread(JVoiceXml interpreter, int port, List<IRTestCase> tests) {
		jvxml = interpreter;
		testcaseList = tests;
		textServerPort = port;
		textServer = new TextServer(textServerPort);
		textServer.start();
	}

	@Override
	public void run() {
		
		for (int i = 0; i < testcaseList.size(); i++) {
			boolean result = false;

			AnswerGenerator answerGenerator = new AnswerGenerator();
			textServer.addTextListener(answerGenerator);
	
			IRTestCase testcase = testcaseList.get(i);

			if (testcase.hasDeps()) {
				LOGGER.debug("id " + testcase.getId()
						+ " case has more page, not implements, skip.");
				continue;
			}

			LOGGER.info("\n\n");
			LOGGER.info("###########################################");
			LOGGER.info("start testcase : " + testcase.toString());

			waitForMoment();
			try {
				URI testURI = testcase.getStartURI();
				LOGGER.info("start uri : " + testURI.toString());
				callThread = new CallThread(jvxml, testURI, textServer
						.getRemoteClient());
				callThread.start();
				result = responseInterpreterPrompt(answerGenerator);
				callThread.join();

			} catch (Exception e) {
				e.printStackTrace();
			}
			// need remove, but not this method
			//textServer.removeTextListener(answerGenerator);
			
			if(result){
				LOGGER.info("The test result is : ---- PASSED ----");
			} else {
				LOGGER.info("The test result is : ---- FAILED ----");
			}
			LOGGER.info("testcase " + testcase.getId() + " finished");
		}

		LOGGER.info("no more test uri, exit.");
		textServer.stopServer();
	}

	/**
	 * fetch answer from AnswerGenerator, and send it back to text server.
	 * 
	 * @return true if test pass , else return false.
	 */
	boolean responseInterpreterPrompt(AnswerGenerator answerGenerator) {
		while (true) {
			String answer = answerGenerator.next();

			if (answer.equalsIgnoreCase("pass")) {
				return true;
			} else if (answer.equalsIgnoreCase("fail")) {
				return false;
			} else {
				// not answer interpreter immediate
				waitForMoment();

				LOGGER.debug("send message : " + answer);
				try {
					// callThread.session.getCharacterInput().addCharacter('1');
					textServer.sendInput(answer);
				} catch (Throwable e) {
					e.printStackTrace();
					return false;
				}
			}
		}
	}

	private void waitForMoment() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	}
}
