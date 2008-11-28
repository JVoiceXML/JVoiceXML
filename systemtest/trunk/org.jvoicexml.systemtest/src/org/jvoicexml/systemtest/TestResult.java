/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
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
        buff.append(lastName(t.getClass().getName()));
        buff.append(" : ");
        buff.append(t.getMessage());
        if(where != null ){
            buff.append(" when " + where);
        }
        reason = buff.toString();
    }

    final String lastName(final String className) {
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