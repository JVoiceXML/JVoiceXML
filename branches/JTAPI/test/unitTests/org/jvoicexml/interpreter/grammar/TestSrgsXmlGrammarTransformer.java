/*
 * File:    $RCSfile: TestSrgsXmlGrammarTransformer.java,v $
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
package org.jvoicexml.interpreter.grammar;

import javax.speech.recognition.RuleGrammar;

import junit.framework.TestCase;

import org.jvoicexml.UserInput;
import org.jvoicexml.interpreter.grammar.transformer.SrgsXmlGrammarTransformer;

/**
 * The <code>TestSrgsXmlGrammarTransformer</code> tests the
 * functionality of the corresponding class.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestSrgsXmlGrammarTransformer
        extends TestCase {
    /**
     * The class, which will be tested.
     */
    private GrammarTransformer transformer;

    /**
     * The class, which will be compared to the transformed grammar
     * for equality.
     */
    private RuleGrammar grammar;

    /**
     * The Recognizer from which to get the empty rule Grammar.
     */
    private UserInput input;

    /**
     * Defines the base directory to the test grammars.
     */
    private static final String BASE = "test/config/irp_vxml21/";

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        /* create a very new transformer */
        transformer = new SrgsXmlGrammarTransformer();

    }

    /**
     * {@inheritDoc}
     */
    protected void tearDown() {
        if (input != null) {
            input.close();
            input = null;
        }
    }

    /**
     * Tests an empty rule grammar.
     */
    public void testEmptyRuleGrammar() {
        assertNotNull(this.grammar);
    }
}
