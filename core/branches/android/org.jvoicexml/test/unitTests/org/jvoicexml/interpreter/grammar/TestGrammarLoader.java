/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/grammar/TestGrammarLoader.java $
 * Version: $LastChangedRevision: 2672 $
 * Date:    $Date: 2011-05-20 02:30:50 -0500 (vie, 20 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link GrammarLoader}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2672 $
 * @since 0.7.5
 */
public final class TestGrammarLoader {
    /** The test object. */
    private GrammarLoader loader;

    /** The VoiceXML interpreter contxt. */
    private VoiceXmlInterpreterContext context;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        loader = new GrammarLoader();
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
    }

    /**
     * Tear down the test environment.
     * @throws java.lang.Exception
     *         tear down failed.
     */
    @After
    public void tearDown() throws Exception {
        loader = null;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.grammar.GrammarLoader#loadGrammarDocument(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.FetchAttributes, org.jvoicexml.xml.srgs.Grammar)}.
     * @exception Exception
     *            test failed
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testLoadGrammarDocument() throws Exception, JVoiceXMLEvent {
        final String cr = System.getProperty("line.separator");
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setType(GrammarType.JSGF);
        final StringBuilder str = new StringBuilder();
        str.append("#JSGF V1.0;" + cr);
        str.append("grammar jvoicexml;" + cr);
        str.append("public <boolean> = yes{true}|no{false};");
        grammar.addCData(str.toString());
        final GrammarDocument grammarDocument =
            loader.loadGrammarDocument(context, null, grammar);
        Assert.assertEquals(grammar.toString(), grammarDocument.getDocument());
    }

}
