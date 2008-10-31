package org.jvoicexml.systemtest;

import java.util.ArrayList;
import java.util.List;

/**
 * result of IR test
 * 
 * @author lancer
 */
public class TestResult {

    public final static String PASS = "pass";

    public final static String FAIL = "fail";

    public final static String SKIP = "skip";

    private String reason = null;

    private String result = null;

    private List<String> logs = new ArrayList<String>();

    public TestResult(String result, String reason) {
        this.result = result;
        this.reason = reason;
    }

    public TestResult(String output) {
        String lowercase = output.toLowerCase();
        if (lowercase.indexOf("pass") >= 0) {
            result = PASS;
            reason = "-";
        }
        if (lowercase.indexOf("fail") >= 0) {
            result = FAIL;
            reason = "-";
        }

    }

    TestResult(Throwable t, String where) {
        result = FAIL;
        StringBuffer buff = new StringBuffer();
        buff.append("at " + where);
        buff.append("[");
        buff.append(lastName(t.getClass().getName()));
        buff.append(":");
        buff.append(t.getMessage());
        buff.append("]");
        reason = buff.toString();
    }

    String lastName(String className) {
        int index = className.lastIndexOf(".");
        if (index < 0) {
            return className;
        } else {
            return className.substring(index + 1);
        }
    }

    @Override
    public String toString() {
        return result + " : " + reason;
    }

    public String getReason() {
        return reason;
    }

    public void addLogMessage(String string) {
        logs.add(string);
    }

    public List<String> getLogMessages() {
        return logs;
    }

    public String getAssert() {
        return result;
    }
}