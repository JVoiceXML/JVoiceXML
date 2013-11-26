/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.demo;

import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.io.Output;

public class TestHelloDemo {

    private Call call;
    private Supervisor supervisor;
    private Conversation conversation;
    /** URI of the application to call. */
    private URI uri;

    @Before
    public void setUp() throws Exception {
        uri = new File("etc/helloworld.vxml").toURI();
        call = new Call();
        supervisor = new Supervisor();
        conversation = supervisor.init(call);
    }

    @Test
    public void testSuccess() {
        assumeHello();
        assumeGoodbye();
        logConversation();
        supervisor.process(uri);
    }

    @Test(timeout = 9999, expected = AssertionError.class)
    public void testMissingHello() {
        assumeGoodbye();
        assertFailure();
    }

    @Test(timeout = 9999, expected = AssertionError.class)
    public void testMissingGoodbye() {
        assumeHello();
        assertFailure();
    }

    @Test(timeout = 9999, expected = AssertionError.class)
    public void testEmpty() {
        supervisor.connected(null); // enforce processing of an empty list
        assertFailure();
    }

    private void assertFailure() {
        logConversation();

        boolean failed = false;
        supervisor.process(uri);
    }

    private void logConversation() {
        Logger.getLogger(TestHelloDemo.class).info(
                "Expected conversation: " + conversation.toString());
    }

    private void assumeHello() {
        addOutput("Hello World!");
    }

    private void assumeGoodbye() {
        addOutput("Goodbye!");
    }

    private void addOutput(final String message) {
        conversation.add(new Output(message));
    }
}
