/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar.identifier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.test.interpreter.grammar.GrammarUtil;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>TestSrgsXmlGrammarIdentifier</code> tests the
 * functionality of the corresponding class.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $LastChangedRevision$
 */
public final class TestSrgsXmlGrammarIdentifier {
    /**
     * The grammar identifier.
     */
    private SrgsXmlGrammarIdentifier identifier;

    /**
     * Defines the base directory to VXML 2.1 IRP the test grammars.
     */
    private static final String BASE21 = "unittests/config/irp_vxml21/";

    /**
     * Defines the base directory to VXML 2.0 IRP the test grammars.
     */
    private static final String BASE20 = "unittests/config/irp_vxml20/";

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         set up failed
     */
    @Before
    public void setUp()
            throws Exception {
        identifier = new SrgsXmlGrammarIdentifier();
    }

    /**
     * Tear down the test environment.
     */
    @After
    public void tearDown() {
        identifier = null;
    }


    /**
     * Test 1 of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test1() throws Exception {
        GrammarDocument doc =
            GrammarUtil.getGrammarFromFile(BASE21 + "1/1_grammar.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 2a of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test2a() throws Exception {
        GrammarDocument doc =
            GrammarUtil.getGrammarFromFile(BASE21 + "2/2_grammar_a.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 2b of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test2b() throws Exception {
        GrammarDocument doc =
                GrammarUtil.getGrammarFromFile(BASE21 + "2/2_grammar_b.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 3 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test3() throws Exception {
        GrammarDocument doc =
                GrammarUtil.getGrammarFromFile(BASE21 + "3/3_grammar_a.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 5 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test5() throws Exception {
        GrammarDocument doc =
            GrammarUtil.getGrammarFromFile(BASE21 + "5/first.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 7 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test7() throws Exception {
        GrammarDocument doc =
            GrammarUtil.getGrammarFromFile(BASE21 + "7/7.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test 338 of the "VXML 2.0 Implementation Report Plan". This
     * report plan provides more than 1000 tests to check the SRGS
     * compliance.
     * @throws Exception test failed.
     */
    @Test
    public void test338() throws Exception {
        GrammarDocument doc =
                GrammarUtil.getGrammarFromFile(BASE20 + "338/338Grammar.grxml");

        final GrammarType type = identifier.identify(doc);
        Assert.assertNull(type);
    }

    /**
     * Tests a valid SRGS XML header.
     *
     */
    @Test
    public void testValidHeader() {
        final GrammarDocument doc = GrammarUtil.getGrammarFromString(
                "<?xml version=\"1.0\" ?>"
                + "<grammar version=\"1.0\" "
                + "xmlns=\"http://www.w3.org/2001/06/grammar\" "
                + "></grammar>");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Tests a valid SRGS XML header.
     */
    @Test
    public void testValidVoicemodeHeader() {
        final GrammarDocument doc = GrammarUtil.getGrammarFromString(
                "<?xml version=\"1.0\" ?>"
                + "<grammar version=\"1.0\" "
                + "xmlns=\"http://www.w3.org/2001/06/grammar\" "
                + "mode=\"voice\" xml:lang=\"en-US\"></grammar>");

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Test a valid SRGS XML header.
     */
    @Test
    public void testValidDoctypeHeader() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\"?>");
        builder.append("<!DOCTYPE grammar PUBLIC ");
        builder.append("\"-//W3C//DTD GRAMMAR 1.0//EN\" ");
        builder.append("\"http://www.w3.org/TR/speech-grammar/grammar.dtd\">");
        builder.append("<grammar version=\"1.0\" ");
        builder.append("xmlns=\"http://www.w3.org/2001/06/grammar\" ");
        builder.append("mode=\"voice\" xml:lang=\"de-DE\"/>");

        final GrammarDocument doc =
            GrammarUtil.getGrammarFromString(builder.toString());

        final GrammarType type = identifier.identify(doc);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Tests a invalid SRGS XML header.
     */
    @Test
    public void testInvalidHeader() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" ?>");
        builder.append("<grammar/>");

        final GrammarDocument doc =
            GrammarUtil.getGrammarFromString(builder.toString());

        final GrammarType type = identifier.identify(doc);
        Assert.assertNull(type);
    }

    /**
     * Tests a invalid SRGS XML header.
     */
    @Test
    public void testInvalidVoiceModeHeader() {
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" ?>");
        builder.append("<grammar version=\"1.0\" ");
        builder.append(" xmlns=\"http://www.w3.org/2001/06/grammar\" ");
        builder.append(" mode=\"voice\"></grammar>");

        final GrammarDocument doc =
            GrammarUtil.getGrammarFromString(builder.toString());

        final GrammarType type = identifier.identify(doc);
        Assert.assertNull(type);
    }
}
