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

import junit.framework.TestCase;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
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
 *
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestGrammarIdentifierCentral
        extends TestCase {

    /** The test object. */
    private GrammarIdentifierCentral central;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
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
        return new JVoiceXmlGrammarDocument(null, content.getBytes(),
                System.getProperty("file.encoding"), true);
    }

    /**
     * Tests the functionality to identify a grammar, which is within
     * a VoiceXML document.
     */
    public void testIdentifyInlineGrammar() {
        final String grammar = "<grammar type=\"application/srgs+xml\" "
            + "root=\"r2\" version=\"1.0\"></grammar>";

        final GrammarDocument doc = getGrammarFromString(grammar);

        try {
            final GrammarType type = central.identifyGrammar(doc,
                    GrammarType.SRGS_XML);
            assertEquals(GrammarType.SRGS_XML, type);
        } catch (UnsupportedFormatError e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests the functionality to load and identify an externally
     * linked grammar.
     */
    public void testIdentifyExternalGrammar() {
        final String grammar = "<grammar type=\"application/srgs+xml\" "
            + "root=\"r2\" src=\"grammar.grxml\" version=\"1.0\"></grammar>";
        final GrammarDocument doc = getGrammarFromString(grammar);

        try {
            final GrammarType type = central.identifyGrammar(doc,
                    GrammarType.SRGS_XML);
            assertEquals(GrammarType.SRGS_XML, type);
        } catch (UnsupportedFormatError e) {
            fail(e.getMessage());
        }
    }
}
