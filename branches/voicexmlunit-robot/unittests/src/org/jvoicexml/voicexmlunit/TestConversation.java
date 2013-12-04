/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
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

package org.jvoicexml.voicexmlunit;


import java.util.ConcurrentModificationException;

import org.junit.*;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.io.*;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.processor.Assert;

public class TestConversation {

    private Conversation conversation;

    @Before
    public void setUp() throws Exception {
        conversation = new Conversation();
        Assert.assertStatements(0, conversation);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConversationAddOutput() {
        final String expected = "1";
        addOutput(expected);
        Assert.assertStatements(1, conversation);
        Assert.assertEquals(expected, conversation.begin().toString());
    }

    @Test
    public void testConversationAddInput() {
        // first element
        addOutput("2");
        Assert.assertNotNull(conversation.next());
        Assert.assertNotNull(conversation.begin());

        // second element, after cursor creation
        final String expected = "3";
        addInput(expected);
        Assert.assertStatements(2, conversation);
        Assert.assertNull(conversation.next()); // invalid cursor
        Assert.assertNotNull(conversation.begin()); // validate again
        final Assertion next = conversation.next();
        Assert.assertEquals(next.getClass(), Input.class);
        Assert.assertEquals(expected, next.toString());
    }

    @Test
    public void testConversationNext() {
        addOutput("begin");
        addOutput("next");

        Assert.assertEquals(conversation.next().toString(), "begin");
        Assert.assertEquals(conversation.begin().toString(), "begin");
        Assert.assertEquals(conversation.next().toString(), "next");
        Assert.assertNull(conversation.next());
    }

    private void addOutput(final String message) {
        conversation.add(new Output(message));
    }

    private void addInput(final String message) {
        conversation.add(new Input(message));
    }
}
