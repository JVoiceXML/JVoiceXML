package org.jvoicexml.systemtest;


import org.apache.log4j.Logger;

/**
 * It monitor TestServer received system out speak messages, and generate
 * suitable answer. if test pass or fail, the answer is PASS or FAIL. Not cover
 * every thing yet, in developing.
 * 
 * @author lancer
 */
public class AnswerGenerator {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(AnswerGenerator.class);
    private static final String MARK = "'";


 
    String parseWord(String message, String startMark, String endMark) {
        int first = message.indexOf(startMark);
        if(first < 0){
            return null;
        }
        int last = message.indexOf(endMark, first + endMark.length());
        if(last < 0){
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