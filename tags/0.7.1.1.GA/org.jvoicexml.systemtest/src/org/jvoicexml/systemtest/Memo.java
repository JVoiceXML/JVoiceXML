/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.systemtest;

import java.util.ArrayList;
import java.util.List;

/**
 * the TestResult implements with recordable about communications.
 * @author lancer
 *
 */
class Memo implements Result {

    /**
     * communications.
     */
    private List<String> commMsgs = new ArrayList<String>();

    /**
     * default result.
     */
    private String result = NEUTRAL;

    /**
     * default reason.
     */
    private String reason = "-";

    /**
     * set fail result. if result had assert, set reason only.
     * @param arg0 assert string.
     */
    public void setFail(final String arg0) {
        if (result == NEUTRAL) {
            result = FAIL;
        }
        reason = arg0;
    }

    /**
     * append a message of communication .
     * @param connMsg the message.
     */
    public void appendCommMsg(final String connMsg) {
        commMsgs.add(connMsg);
        String lowcase = connMsg.toLowerCase().trim();
        if (PASS.equals(lowcase)) {
            result = PASS;
        } else if (FAIL.equals(lowcase)) {
            result = FAIL;
            reason = FAIL_ASSERT_BY_OUTPUT;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAssert() {
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("comm msg:\n");
        for (String msg : commMsgs) {
            str.append(msg + "\n");
        }
        str.append("----" + result + "\n");
        return str.toString();
    }
}
