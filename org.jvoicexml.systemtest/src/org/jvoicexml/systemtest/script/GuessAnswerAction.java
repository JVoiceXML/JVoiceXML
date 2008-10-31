package org.jvoicexml.systemtest.script;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.systemtest.ActionContext;

public class GuessAnswerAction extends Action {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(GuessAnswerAction.class.getName());

    private static final String MARK = "'";

    @Override
    public void execute(ActionContext context) throws ErrorEvent,
            TimeoutException, IOException {
        LOGGER.debug("execute()");
        while (true) {
            String answer = getAnswer(context.nextEvent());
            if (answer != null) {
                LOGGER.debug("guess answer is : " + answer);
                context.removeCurrentEvent();
                context.answer(answer);
            } else {
                LOGGER.debug("not guess suitable answer, exit.");
                break;
            }
        }
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
