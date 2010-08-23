package org.jvoicexml.systemtest.script;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Answer;

public class GuessAnswerAction extends Action {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(GuessAnswerAction.class.getName());

    private static final String MARK = "'";

    /**
     * {@inheritDoc}
     */
    @Override
    public Answer execute(String event) {
        LOGGER.debug("execute()");

            String answer = getAnswer(event);
            if (answer != null) {
                LOGGER.debug("guess answer is : " + answer);
                return new Answer(answer);
            } else {
                LOGGER.debug("not guess suitable answer, exit.");
                return null;
            }

    }
    
    @Override
    public boolean finished (){
        return false;
    }

    String parseWord(String message, String startMark, String endMark) {
        int first = message.indexOf(startMark);
        if (first < 0) {
            return null;
        }
        int last = message.indexOf(endMark, first + endMark.length());
        if (last < 0) {
            return null;
        }
        return message.substring(first + 1, last).toLowerCase();
    }

    public String getAnswer(final String message) {
        String answer = null;
        if (message.indexOf("Say") >= 0) {
            answer = parseWord(message, MARK, MARK);
        } else if (message.indexOf("Press") >= 0) {
            answer = parseWord(message, MARK, MARK);
        }
        return answer;
    }
}
