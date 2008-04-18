package org.jvoicexml.implementation.jsapi20;

import javax.speech.SpeechEventExecutor;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SynchronousSpeechEventExecutor implements SpeechEventExecutor {

    public SynchronousSpeechEventExecutor() {
    }

    /**
     * execute
     *
     * @param runnable Runnable
     * @throws IllegalStateException
     * @throws NullPointerException
     * @todo Implement this javax.speech.SpeechEventExecutor method
     */
    public void execute(Runnable runnable) throws IllegalStateException,
            NullPointerException {
        runnable.run();
    }
}
