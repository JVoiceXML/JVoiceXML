/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/documentserver/schemestrategy/builtin/TestDigitsGrammarCreator.java $
 * Version: $LastChangedRevision: 2899 $
 * Date:    $Date: 2012-01-17 06:34:22 -0600 (mar, 17 ene 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayInputStream;
import java.net.URI;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.ModeType;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;

/**
 * Test cases for {@link DigitsGrammarCreator}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2899 $
 * @since 0.7.1
 */
public final class TestDigitsGrammarCreator {

    /**
     * Test method for {@link DigitsGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammar() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitsGrammarCreator();

        final URI dtmfUri = new URI("builtin:dtmf/digits");
        final byte[] bytes1 = creator.createGrammar(dtmfUri);
        final ByteArrayInputStream in1 = new ByteArrayInputStream(bytes1);
        final InputSource source1 = new InputSource(in1);
        final SrgsXmlDocument document1 = new SrgsXmlDocument(source1);
        final Grammar dtmfGrammar = document1.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(document1);
        Assert.assertEquals("1-", dtmfItem.getRepeat());

        final URI voiceUri = new URI("builtin:voice/digit");
        final byte[] bytes2 = creator.createGrammar(voiceUri);
        final ByteArrayInputStream in2 = new ByteArrayInputStream(bytes2);
        final InputSource source2 = new InputSource(in2);
        final SrgsXmlDocument document2 = new SrgsXmlDocument(source2);
        final Grammar voiceGrammar = document2.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(document2);
        Assert.assertEquals("1-", voiceItem.getRepeat());
    }

    /**
     * Test method for {@link DigitsGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammarParamters() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitsGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?minlength=2;maxlength=4");
        final byte[] bytes1 = creator.createGrammar(dtmfUri);
        final ByteArrayInputStream in1 = new ByteArrayInputStream(bytes1);
        final InputSource source1 = new InputSource(in1);
        final SrgsXmlDocument document1 = new SrgsXmlDocument(source1);
        final Grammar dtmfGrammar = document1.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(document1);
        Assert.assertEquals("2-4", dtmfItem.getRepeat());

        final URI voiceUri = new URI(
                "builtin:voice/digit?minlength=2;maxlength=4");
        final byte[] bytes2 = creator.createGrammar(voiceUri);
        final ByteArrayInputStream in2 = new ByteArrayInputStream(bytes2);
        final InputSource source2 = new InputSource(in2);
        final SrgsXmlDocument document2 = new SrgsXmlDocument(source2);
        final Grammar voiceGrammar = document2.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(document2);
        Assert.assertEquals("2-4", voiceItem.getRepeat());
    }

    /**
     * Test method for {@link DigitsGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected exception
     */
    @Test(expected = BadFetchError.class)
    public void testCreateGrammarIllegalParamters()
        throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitsGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digit?minlength=4;maxlength=2");
        creator.createGrammar(dtmfUri);
    }

    /**
     * Test method for {@link DigitsGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            test failed
     */
    @Test
    public void testCreateGrammarLength() throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitsGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digits?length=4");
        final byte[] bytes1 = creator.createGrammar(dtmfUri);
        final ByteArrayInputStream in1 = new ByteArrayInputStream(bytes1);
        final InputSource source1 = new InputSource(in1);
        final SrgsXmlDocument document1 = new SrgsXmlDocument(source1);
        final Grammar dtmfGrammar = document1.getGrammar();
        Assert.assertEquals(ModeType.DTMF, dtmfGrammar.getMode());
        final Item dtmfItem = getRootItem(document1);
        Assert.assertEquals("4", dtmfItem.getRepeat());

        final URI voiceUri = new URI(
                "builtin:voice/digits?length=4");
        final byte[] bytes2 = creator.createGrammar(voiceUri);
        final ByteArrayInputStream in2 = new ByteArrayInputStream(bytes2);
        final InputSource source2 = new InputSource(in2);
        final SrgsXmlDocument document2 = new SrgsXmlDocument(source2);
        final Grammar voiceGrammar = document2.getGrammar();
        Assert.assertEquals(ModeType.VOICE, voiceGrammar.getMode());
        final Item voiceItem = getRootItem(document2);
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
     * Test method for {@link DigitsGrammarCreator#createGrammar(java.net.URI)}.
     * @exception Exception
     *            test failed
     * @exception BadFetchError
     *            expected error
     */
    @Test(expected = BadFetchError.class)
    public void testCreateGrammarIllegalParamterCombination()
        throws Exception, BadFetchError {
        final GrammarCreator creator = new DigitsGrammarCreator();

        final URI dtmfUri = new URI(
                "builtin:dtmf/digits?minlength=2;maxlength=4&length=3");
        creator.createGrammar(dtmfUri);
    }
}
