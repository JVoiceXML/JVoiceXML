package org.jvoicexml.interpreter.grammar;

import junit.framework.TestCase;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarIdentifierCentral;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;

/*
 * File:    $RCSfile: TestGrammarIdentifierCentral.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

/**
 * The <code>TestGrammarIdentifierCentral</code> tests the
 * functionality of the GrammarIdentifierCentral class.
 *
 * @author Christoph Buente
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public class TestGrammarIdentifierCentral
        extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp()
            throws Exception {
     }

    /**
     * Tests the functionality to identify a grammar, which is within
     * a vxml document.
     *
     */
    public final void testIdentifyInlineGrammar() {
        String grammar = "<grammar type=\"application/srgs+xml\" "
                         + "root=\"r2\" version=\"1.0\"></grammar>";
        try {
            final String type = new GrammarIdentifierCentral().identifyGrammar("");
            assertEquals("application/srgs+xml", type);
        } catch (UnsupportedFormatError e) {
            fail();
        }
    }

    /**
     * Tests the fuctionality to load and identify an externally
     * linked grammar.
     *
     */
    public final void testIdentifyExternalGrammar() {
        String grammar = "<grammar type=\"application/srgs+xml\" "
                         +
                "root=\"r2\" src=\"grammar.grxml\" version=\"1.0\"></grammar>";
        try {
            final String type = new GrammarIdentifierCentral().identifyGrammar("");
            assertEquals("application/srgs+xml", type);
        } catch (UnsupportedFormatError e) {
            fail();
        }
    }

}
