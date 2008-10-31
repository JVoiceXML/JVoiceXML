package org.jvoicexml.systemtest.script;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jvoicexml.systemtest.ActionContext;

@XmlRootElement(name = "input")
public class AnswerAction extends Action {

    private static final String MARK = "'";

    @XmlAttribute(name = "answer")
    String speak;

    @Override
    public void execute(ActionContext context) {
        context.answer(speak);
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
