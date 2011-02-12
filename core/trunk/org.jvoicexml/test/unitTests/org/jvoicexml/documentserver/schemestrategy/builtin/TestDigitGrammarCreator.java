/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;

/**
 * Test cases for {@link DigitGrammarCreator}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
public final class TestDigitGrammarCreator {

    /**
     * Test method for {@link DigitGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammar() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitGrammarCreator();

        final URI dtmfUri = new URI("builtin:dtmf/digit");
        final SrgsXmlDocument dtmfDocument = creator.createGrammar(dtmfUri);
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(dtmfDocument);
        Assert.assertEquals("1-", dtmfItem.getRepeat());

        final URI voiceUri = new URI("builtin:voice/digit");
        final SrgsXmlDocument voiceDocument = creator.createGrammar(voiceUri);
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(voiceDocument);
        Assert.assertEquals("1-", voiceItem.getRepeat());
    }

    /**
     * Test method for {@link DigitGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammarParamters() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?minlength=2;maxlength=4");
        final SrgsXmlDocument dtmfDocument = creator.createGrammar(dtmfUri);
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(dtmfDocument);
        Assert.assertEquals("2-4", dtmfItem.getRepeat());

        final URI voiceUri = new URI(
                "builtin:voice/digit?minlength=2;maxlength=4");
        final SrgsXmlDocument voiceDocument = creator.createGrammar(voiceUri);
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(voiceDocument);
        Assert.assertEquals("2-4", voiceItem.getRepeat());
    }

    /**
     * Test method for {@link DigitGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected exception
     */
    @Test(expected = BadFetchError.class)
    public void testCreateGrammarIllegalParamters()
        throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?minlength=4;maxlength=2");
        creator.createGrammar(dtmfUri);
    }

    /**
     * Test method for {@link DigitGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammarLength() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?length=4");
        final SrgsXmlDocument dtmfDocument = creator.createGrammar(dtmfUri);
        final Grammar dtmfGrammar = dtmfDocument.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(dtmfDocument);
        Assert.assertEquals("4", dtmfItem.getRepeat());

        final URI voiceUri = new URI(
                "builtin:voice/digit?length=4");
        final SrgsXmlDocument voiceDocument = creator.createGrammar(voiceUri);
        final Grammar voiceGrammar = voiceDocument.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(voiceDocument);
        Assert.assertEquals("4", voiceItem.getRepeat());
    }

    /**
     * Retrieves the item of the root rule.
     * @param document the document
     * @return item of the root rule.
     */
    private Item getRootItem(final SrgsXmlDocument document) {
        final Grammar grammar = document.getGrammar();
        final Rule rule = grammar.getRootRule();
        return (Item) rule.getFirstChild();
    }

    /**
     * Test method for {@link DigitGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected error
     */
    @Test(expected = BadFetchError.class)
    public void testCreateGrammarIllegalParamterCombination()
        throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?minlength=2;maxlength=4&length=3");
        creator.createGrammar(dtmfUri);
    }
}
