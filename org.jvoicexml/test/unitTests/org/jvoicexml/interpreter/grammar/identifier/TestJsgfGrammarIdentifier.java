/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/grammar/identifier/TestJsgfGrammarIdentifier.java $
 * Version: $LastChangedRevision: 2890 $
 * Date:    $Date: 2012-01-13 02:38:51 -0600 (vie, 13 ene 2012) $
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

package org.jvoicexml.interpreter.grammar.identifier;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.test.interpreter.grammar.GrammarUtil;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link JsgfGrammarIdentifier}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2890 $
 * @since 0.7.5
 */
public final class TestJsgfGrammarIdentifier {
    /** The test object. */
    private GrammarIdentifier identifier;

    /**
     * Set up the test environment.
     * @throws java.lang.Exception
     *         set up failed
     */
    @Before
    public void setUp() throws Exception {
        identifier = new JsgfGrammarIdentifier();
    }

    /**
     * Tear down the test environment.
     * @throws java.lang.Exception
     *         tear down failed.
     */
    @After
    public void tearDown() throws Exception {
        identifier = null;
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.grammar.identifier.JsgfGrammarIdentifier#identify(org.jvoicexml.GrammarDocument)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIdentify() throws Exception {
        final GrammarDocument document =
            GrammarUtil.getGrammarFromResource(
                "/org/jvoicexml/interpreter/grammar/identifier/jvoicexml.gram");
        final GrammarType type = identifier.identify(document);
        Assert.assertEquals(GrammarType.JSGF, type);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.grammar.identifier.JsgfGrammarIdentifier#identify(org.jvoicexml.GrammarDocument)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIdentifyGrammarNode() throws Exception {
        final String cr = System.getProperty("line.separator");
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setType(GrammarType.JSGF);
        final StringBuilder str = new StringBuilder();
        str.append("#JSGF V1.0;");
        str.append(cr);
        str.append("grammar jvoicexml;");
        str.append(cr);
        str.append("public <boolean> = yes{true}|no{false};");
        grammar.addCData(str.toString());
        final GrammarDocument grammarDocument =
            new JVoiceXmlGrammarDocument(null, grammar);
        final GrammarType type = identifier.identify(grammarDocument);
        Assert.assertEquals(GrammarType.JSGF, type);
    }
}
