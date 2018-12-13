/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.systemtest;

import org.jvoicexml.voicexmlunit.Call;


/**
 * Actual test script to be executed per test case.
 * @author lancer
 * @author Dirk Schnelle-Walka
 */
public interface Script {
    /** Default timeout for hearing utterances or waiting for expected input. */
    long DEFAULT_TIMEOUT = 10000;

    /**
     * Sets the id of this test case.
     * @param id id of this test case.
     * @since 0.7.6
     */
    void setTestId(final String id);

    /**
     * Performs the actual test.
     * @param call the actual call
     * @exception AssertionError
     *            error processing the script
     */
    void perform(final Call call) throws AssertionError;
}
