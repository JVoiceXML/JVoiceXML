package org.jvoicexml.interpreter.grammar;

/*
 * File:    $RCSfile: TestGrammarProcessorHelper.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2005/11/17 22:47:34 $
 * Author:  $Author: buente $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.grammar.GrammarProcessorHelper;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.VoiceXmlNode;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

/**
 * The <code>TestGrammarProcessorHelper</code> tests the
 * functionality of the GrammarProcessorHelperdoes ...
 * 
 * @author Christoph Buente
 * 
 * @version $Revision: 1.3 $
 * 
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestGrammarProcessorHelper
        extends TestCase {

    private static Logger LOGGER = Logger
            .getLogger(TestGrammarProcessorHelper.class);

    /**
     * Defines the base directory to custom test grammars.
     */
    private static final String BASE = "test/config/custom_grammar/";

    /**
     * the processors. who got to do the job.
     */
    private GrammarProcessorHelper helper;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.helper = new GrammarProcessorHelper();
    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        this.helper = null;
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalInvalid1() {
        setName("Test with src, srcexpr attributes and inline grammar.");
        LOGGER.debug("TEST 1");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/invalid1.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                helper.isExternalGrammar(gr);
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This exception is correct, "
                        + "cause we put in an invalid grammar.");
            }
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalInvalid2() {
        setName("Test with src and srcexpr attribute.");
        LOGGER.debug("TEST 2");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/invalid2.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                helper.isExternalGrammar(gr);
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This exception is correct, "
                        + "cause we put in an invalid grammar.");
            }
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalInvalid3() {
        setName("Test with src attibute and inline grammar.");
        LOGGER.debug("TEST 3");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/invalid3.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                helper.isExternalGrammar(gr);
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This exception is correct, "
                        + "cause we put in an invalid grammar.");
            }
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalInvalid4() {
        setName("Test with srcexpr attibute and inline grammar.");
        LOGGER.debug("TEST 4");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/invalid4.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                helper.isExternalGrammar(gr);
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This exception is correct, "
                        + "cause we put in an invalid grammar.");
            }
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalValid1() {
        setName("Test with src attibute.");
        LOGGER.debug("TEST 5");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/valid1.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                assertEquals(true, helper.isExternalGrammar(gr));
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            fail("This is not correct, the grammar is valid");
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalValid2() {
        setName("Test with srcexpr attibute.");
        LOGGER.debug("TEST 5");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "external/valid2.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                assertEquals(true, helper.isExternalGrammar(gr));
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            fail("This is not correct, the grammar is valid");
        }
    }

    /**
     * Try to process a srgs abnf grammar.
     */
    public void testIsExternalvalid3() {
        setName("Test with inline inline grammar.");
        LOGGER.debug("TEST 3");
        final StringBuffer buffer = new StringBuffer();
        File testFile = new File(BASE + "inline/valid1.grxml");
        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document = new VoiceXmlDocument(
                    new InputSource(new FileReader(testFile)));
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* now test isExternalMethod */
                assertEquals(false, helper.isExternalGrammar(gr));
            }
        } catch (Exception e) {
            fail("Error opening grammar file.");
        } catch (BadFetchError e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("This exception is correct, "
                        + "cause we put in an invalid grammar.");
            }
        }
    }
}
