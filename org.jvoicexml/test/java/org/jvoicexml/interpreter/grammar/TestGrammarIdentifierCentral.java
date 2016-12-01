/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.ExternalGrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.identifier.SrgsAbnfGrammarIdentifier;
import org.jvoicexml.interpreter.grammar.identifier.SrgsXmlGrammarIdentifier;
import org.jvoicexml.xml.srgs.GrammarType;


/**
 * The <code>TestGrammarIdentifierCentral</code> tests the
 * functionality of the GrammarIdentifierCentral class.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 */
public final class TestGrammarIdentifierCentral {

    /** The test object. */
    private GrammarIdentifierCentral central;

    /**
     * Set up the test environment.
     * @exception Exception setup failed
     */
    @Before
    public void setUp()
            throws Exception {
        central = new GrammarIdentifierCentral();
        central.addIdentifier(new SrgsAbnfGrammarIdentifier());
        central.addIdentifier(new SrgsXmlGrammarIdentifier());
    }

    /**
     * Convenience method to create a grammar document from a string.
     * @param content content of the document.
     * @return grammar document.
     */
    private GrammarDocument getGrammarFromString(final String content) {
        return new ExternalGrammarDocument(null, content.getBytes(),
                System.getProperty("file.encoding"), true);
    }

    /**
     * Tests the functionality to identify a grammar, which is within
     * a VoiceXML document.
     * @exception UnsupportedFormatError test failed
     */
    @Test
    public void testIdentifyInlineGrammar() throws UnsupportedFormatError {
        final String grammar = "<grammar type=\"application/srgs+xml\" "
            + "root=\"r2\" version=\"1.0\"></grammar>";

        final GrammarDocument doc = getGrammarFromString(grammar);

        final GrammarType type = central.identifyGrammar(doc,
                GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }

    /**
     * Tests the functionality to load and identify an externally
     * linked grammar.
     * @exception UnsupportedFormatError test failed
     */
    @Test
    public void testIdentifyExternalGrammar() throws UnsupportedFormatError {
        final String grammar = "<grammar type=\"application/srgs+xml\" "
            + "root=\"r2\" src=\"grammar.grxml\" version=\"1.0\"></grammar>";
        final GrammarDocument doc = getGrammarFromString(grammar);

        final GrammarType type = central.identifyGrammar(doc,
                GrammarType.SRGS_XML);
        Assert.assertEquals(GrammarType.SRGS_XML, type);
    }
}
