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

/**
 * result of test.
 *
 * @author lancer
 *
 */
public interface Result {

    /**
     * Key of PASS.
     */
    String PASS = "pass";

    /**
     * Key of Fail.
     */
    String FAIL = "fail";

    /**
     * Key of SKIP.
     */
    String SKIP = "skip";

    /**
     * Key of SKIP.
     */
    String NEUTRAL = "neutral";

    /**
     * fail reason type1.
     */
    String TIMEOUT_WHEN_CONNECT = "Timeout When Connect";
    /**
     * fail reason type2.
     */
    String TIMEOUT_WHEN_DISCONNECT = "Timeout When Disconnect";
    /**
     * fail reason type3.
     */
    String TIMEOUT_WHEN_WAIT_OUTPUT = "Timeout When Wait Output";

    /**
     * fail reason type4.
     */
    String FAIL_ASSERT_BY_OUTPUT = "Fail Report By Output";

    /**
     * fail reason type5.
     */
    String DISCONNECT_BEFORE_ASSERT = "Disconnect Before Assert";

    /**
     * @return key of result.
     */
    String getReason();

    /**
     * @return key string of test.
     */
    String getAssert();

}



/**
 * executer status listener interface.
 * @author lancer
 *
 */
interface StatusListener {
    /**
     * notify the executer status changed.
     */
    void update();
}
