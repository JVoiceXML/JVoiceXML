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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jvoicexml.voicexmlunit.Conversation;
import org.jvoicexml.voicexmlunit.Supervisor;
import org.jvoicexml.voicexmlunit.processor.Assert;

public class TestConversation {

    private Supervisor supervisor;

    @Before
    public void setUp() throws Exception {
        supervisor = new Supervisor();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConversationAdd() {
        // 1. one Output
        Conversation conversation = createConversationForSimpleTest();
        String expected = "Test1";
        conversation.addOutput(expected);
        // Assert.assertStatements(1, conversation);
        Assert.assertEquals(expected, conversation.begin().toString());

        // 2. one Output again
        conversation = createConversationForSimpleTest();
        expected = "Test2";
        conversation.addOutput(expected);
        // Assert.assertStatements(1, conversation);
        Assert.assertEquals(expected, conversation.begin().toString());

        // 3. both Output and Input
        expected = "Test3";
        conversation.addInput(expected);
        // Assert.assertStatements(2, conversation);
        // Assert.assertEquals(expected, conversation.next().toString()); //
        // TODO

        // 4. conversation gets empty again
        conversation = createConversationForSimpleTest();
        // Assert.assertStatements(0, conversation);
        Assert.assertNull(conversation.next());
    }

    @Test
    public void testConversationNext() {
        Conversation conversation = createConversationForSimpleTest();
        conversation.addOutput("begin");
        conversation.addOutput("next");

        Assert.assertEquals(conversation.next().toString(), "begin"); // tricky,
                                                                      // eh?
        Assert.assertEquals(conversation.begin().toString(), "begin");
        Assert.assertEquals(conversation.next().toString(), "next");
        Assert.assertNull(conversation.next());
    }

    private Conversation createConversationForSimpleTest() {
        Conversation conversation = supervisor.init(null);
        Assert.assertStatements(0, conversation);
        return conversation;
    }
}
