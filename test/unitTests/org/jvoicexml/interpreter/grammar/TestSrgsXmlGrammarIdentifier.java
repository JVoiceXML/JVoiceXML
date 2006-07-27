/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;

import junit.framework.TestCase;

/**
 * The <code>TestSrgsXmlGrammarIdentifier</code> tests the
 * functionality of the corresponding class.
 *
 * @author Christoph Buente
 *
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestSrgsXmlGrammarIdentifier
        extends TestCase {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger
                                         .getLogger(
            TestSrgsXmlGrammarIdentifier.class);

    /**
     * The grammar identifier.
     */
    private SrgsXmlGrammarIdentifier identifier;

    /**
     * Defines the base directory to VXML 2.1 IRP the test grammars.
     */
    private static final String BASE21 = "test/config/irp_vxml21/";

    /**
     * Defines the base directory to VXML 2.0 IRP the test grammars.
     */
    private static final String BASE20 = "test/config/irp_vxml20/";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        identifier = new SrgsXmlGrammarIdentifier();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() {
        identifier = null;
    }

    /**
     * Test 1 of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     */
    public final void test1() {
        LOGGER.debug("TEST 1");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "1/1_grammar.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test 2a of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     */
    public final void test2a() {
        LOGGER.debug("TEST 2a");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "2/2_grammar_a.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test 2b of the "Implementation Report Plan". This report plan
     * provides 148 tests to check the SRGS compliance.
     */
    public final void test2b() {
        LOGGER.debug("TEST 2b");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "2/2_grammar_b.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test 3 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     */
    public final void test3() {
        LOGGER.debug("TEST 3");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "3/3_grammar_a.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Test 5 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     */
    public final void test5() {
        LOGGER.debug("TEST 5");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "5/first.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test 7 of the "VXML 2.1 Implementation Report Plan". This
     * report plan provides 148 tests to check the SRGS compliance.
     */
    public final void test7() {
        LOGGER.debug("TEST 7");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE21 + "7/7.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertEquals("application/srgs+xml", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test 338 of the "VXML 2.0 Implementation Report Plan". This
     * report plan provides more than 1000 tests to check the SRGS
     * compliance.
     */
    public final void test338() {
        LOGGER.debug("TEST 338");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE20 + "338/338Grammar.grxml");
        try {
            FileReader fileReader = new FileReader(testFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while (bufferedReader.ready()) {
                buffer.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            fileReader.close();
            final String type = identifier.identify(new String(buffer));
            LOGGER.debug("identified type is " + type);
            assertNull("grammar contains no version.", type);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests a valid SRGS XML header.
     *
     */
    public final void testValidHeader() {
        LOGGER.debug("testing header with mode attr.");
        final String type = identifier.identify("<?xml version=\"1.0\" ?>"
                                                + "<grammar version=\"1.0\" "
                                                +
                "xmlns=\"http://www.w3.org/2001/06/grammar\" "
                                                + "></grammar>");
        LOGGER.debug("identified type is " + type);
        assertEquals("application/srgs+xml", type);
    }

    /**
     * Tests a valid SRGS XML header.
     */
    public final void testValidVoicemodeHeader() {
        LOGGER.debug("testing header with mode attr.");
        final String type = identifier.identify("<?xml version=\"1.0\" ?>"
                                                + "<grammar version=\"1.0\" "
                                                +
                "xmlns=\"http://www.w3.org/2001/06/grammar\" "
                                                +
                "mode=\"voice\" xml:lang=\"en-US\"></grammar>");
        LOGGER.debug("identified type is " + type);
        assertEquals("application/srgs+xml", type);
    }

    /**
     * Test a valid SRGS XML header.
     */
    public final void testValidDoctypeHeader() {
        LOGGER.debug("testing header with doctype");
        final StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\"?>");
        builder.append("<!DOCTYPE grammar PUBLIC ");
        builder.append("\"-//W3C//DTD GRAMMAR 1.0//EN\" ");
        builder.append("\"http://www.w3.org/TR/speech-grammar/grammar.dtd\">");
        builder.append("<grammar version=\"1.0\" ");
        builder.append("xmlns=\"http://www.w3.org/2001/06/grammar\" ");
        builder.append("mode=\"voice\" xml:lang=\"de-DE\"/>");

        final String type = identifier.identify(builder.toString());
        LOGGER.debug("identified type is " + type);
        assertEquals("application/srgs+xml", type);

    }

    /**
     * Tests a invalid SRGS XML header.
     */
    public final void testInvalidHeader() {
        LOGGER.debug("testing minimal invalid header");
        final String type = identifier.identify("<?xml version=\"1.0\" ?>"
                                                + "<grammar/>");
        LOGGER.debug("identified type is " + type);
        assertNull(type);
    }

    /**
     * Tests a invalid SRGS XML header.
     */
    public final void testInvalidVoiceModeHeader() {
        LOGGER.debug("testing invalid header with mode attr.");
        final String type = identifier.identify("<?xml version=\"1.0\" ?>"
                                                + "<grammar version=\"1.0\" "
                                                +
                " xmlns=\"http://www.w3.org/2001/06/grammar\" "
                                                + " mode=\"voice\"></grammar>");
        LOGGER.debug("identified type is " + type);
        assertNull(type);
    }
}
