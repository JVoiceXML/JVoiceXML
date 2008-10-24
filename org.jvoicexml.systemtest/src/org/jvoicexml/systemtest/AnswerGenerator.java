package org.jvoicexml.systemtest;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jvoicexml.client.text.TextListener;
import org.jvoicexml.client.text.TextServer;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * It monitor TestServer received system out speak messages, and generate
 * suitable answer. if test pass or fail, the answer is PASS or FAIL. Not cover
 * every thing yet, in developing.
 * 
 * @author lancer
 */
public class AnswerGenerator implements TextListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(AnswerGenerator.class);
    public final static long MAX_WAIT_TIME = 3000L;
    public final static long DELAY_ANSWER_TIME = 1000L;

    private Thread waitedThread = null;

    private BlockingQueue<String> outputMessages = new LinkedBlockingQueue<String>();

    private TextServer textServer;

    AnswerGenerator(TextServer textServer) {
        this.textServer = textServer;
    }

    /**
     * fetch answer from AnswerGenerator, and send it back to text server.
     * 
     * @param autoTestThread
     *            TODO
     * @return true if test pass , else return false.
     */
    boolean audioResponse() throws InterruptedException {

        while (true) {
            String message = waitResult();
            LOGGER.info("got message : " + message);

            if (isPassMessage(message)) {
                return true;
            }
            if (isFailMessage(message)) {
                return false;
            }

            String answer = null;
            if (message.indexOf("Say") > 0) {
                answer = parseWord(message, "'", "'");
            }
            if (message.indexOf("Press") > 0) {
                answer = parseWord(message, "'", "'");
            }
            if (answer == null) {
                LOGGER.warn("not get answer from output");
                continue;
            }
            LOGGER.debug("send message : " + answer);
            // not answer interpreter immediate
            waitForMoment();

            if (textServer != null) {
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

    private boolean isPassMessage(final String message) {
        String lowercase = message.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isFailMessage(final String message) {
        String lowercase = message.toLowerCase();
        if (lowercase.indexOf("fail") >= 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return suitable answer.
     */
    public String waitResult() throws InterruptedException {
        LOGGER.debug("next() begin");
        String message = null;
        waitedThread = Thread.currentThread();
        Timer timer = new Timer();
        timer.start();
        try {
            message = outputMessages.take();
            timer.stopTimer();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        }
        return message;
    }

    String parseWord(String message, String start, String end) {
        int first = message.indexOf(start);
        int last = message.indexOf(end, first + 1);
        return message.substring(first + 1, last).toLowerCase();
    }

    /**
     * @return true if have more answers.
     */
    public boolean hasMore() {
        synchronized (outputMessages) {
            return outputMessages.isEmpty() ? false : true;
        }
    }

    @Override
    public void outputSsml(SsmlDocument arg0) {
        LOGGER.debug("Received SsmlDocument : " + arg0.toString());

        try {
            outputMessages.put(arg0.toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void outputText(String arg0) {
        LOGGER.debug("Received Text : " + arg0);

        try {
            outputMessages.put(arg0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connected(InetSocketAddress remote) {
        if (remote != null) {
            LOGGER.debug("connected to " + remote.toString());
        }
        synchronized (outputMessages) {
            outputMessages.clear();
        }
    }

    @Override
    public void disconnected() {
        LOGGER.debug("disconnected");
    }

    void timeout() {
        LOGGER.info("max wait time was reach. cancel current test case");
        waitedThread.interrupt();
    }

    void waitForMoment() {
        try {
            Thread.sleep(DELAY_ANSWER_TIME);
        } catch (InterruptedException e) {
        }
    }

    class Timer extends Thread {

        boolean stop = false;

        @Override
        public void run() {
            LOGGER.debug("timer started.");
            try {
                Thread.sleep(MAX_WAIT_TIME);
            } catch (InterruptedException e) {
            }
            LOGGER.debug("timer wake up.");
            if (!stop) {
                timeout();
            }
        }

        public void stopTimer() {
            LOGGER.debug("timer stopped.");
            stop = true;
        }
    }
}
