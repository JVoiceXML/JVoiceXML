/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test case for the {@link BufferedCharacterInput}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestBufferedCharacterInput implements SpokenInputListener {
    /** Maximum number of msec to wait for a result. */
    private static final int MAX_WAIT = 500;

    /** The test object. */
    private BufferedCharacterInput input;

    /** Synchronisation. */
    private final Object lock = new Object();

    /** The last received event. */
    private transient RecognitionResult result;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         Test failed.
     */
    @Before
    public void setUp() throws Exception {
        input = new BufferedCharacterInput();
        input.addListener(this);
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.BufferedCharacterInput#addCharacter(char)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testAddCharacter() throws JVoiceXMLEvent, Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setMode(ModeType.DTMF);
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf oneOf = rule.appendChild(OneOf.class);
        final Item item1 = oneOf.appendChild(Item.class);
        item1.addText("1");
        final Item item2 = oneOf.appendChild(Item.class);
        item2.addText("2");
        final Item item3 = oneOf.appendChild(Item.class);
        item3.addText("3");
        System.out.println(document);
        final SrgsXmlGrammarImplementation impl =
            new SrgsXmlGrammarImplementation(document);
        final Collection<GrammarImplementation<?>> grammars =
            new java.util.ArrayList<GrammarImplementation<?>>();
        grammars.add(impl);
        input.activateGrammars(grammars);

        input.startRecognition();
        final char dtmf = '2';
        input.addCharacter(dtmf);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        input.stopRecognition();
        Assert.assertTrue("result should be accpted", result.isAccepted());
        Assert.assertEquals(Character.toString(dtmf), result.getUtterance());

        input.startRecognition();
        final char invalidDtmf = '4';
        input.addCharacter(invalidDtmf);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        input.stopRecognition();
        Assert.assertTrue("result should be rejected", result.isRejected());
        Assert.assertEquals(Character.toString(invalidDtmf),
                result.getUtterance());
    }

    /**
     * Test method for {@link org.jvoicexml.implementation.BufferedCharacterInput#addCharacter(char)}.
     * @exception JVoiceXMLEvent
     *            Test failed.
     * @exception Exception
     *            Test failed.
     */
    @Test
    public void testAddCharacterNoGrammar() throws JVoiceXMLEvent, Exception {
        input.startRecognition();
        final char dtmf = '2';
        input.addCharacter(dtmf);
        synchronized (lock) {
            lock.wait(MAX_WAIT);
        }
        input.stopRecognition();
        Assert.assertTrue("result should be rejected", result.isRejected());
        Assert.assertEquals(Character.toString(dtmf), result.getUtterance());
    }

    /**
     * {@inheritDoc}
     */
    public void inputStatusChanged(final SpokenInputEvent event) {
        final int type = event.getEvent();
        System.out.println(event);
        if ((type != SpokenInputEvent.RESULT_ACCEPTED)
                && (type != SpokenInputEvent.RESULT_REJECTED)) {
            return;
        }
        result = (RecognitionResult) event.getParam();
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
