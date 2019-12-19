/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.grammar.luis;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.GrammarTypeFactory;

/**
 * Test cases for {@link LUISGrammarTypeFactory}
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class LUISGrammarTypeFactoryTest {

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.grammar.luis.LUISGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarType() {
        final GrammarTypeFactory factory = new LUISGrammarTypeFactory();
        final GrammarType type = factory
                .getGrammarType(LUISGrammarType.GRAMMAR_TYPE.toString());
        Assert.assertTrue(LUISGrammarType.GRAMMAR_TYPE.match(type.getType()));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.grammar.luis.LUISGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeNull() {
        final GrammarTypeFactory factory = new LUISGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType(null);
        Assert.assertNull(type);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.grammar.luis.LUISGrammarTypeFactory#getGrammarType(java.lang.String)}.
     */
    @Test
    public void testGetGrammarTypeOtherType() {
        final GrammarTypeFactory factory = new LUISGrammarTypeFactory();
        final GrammarType type = factory.getGrammarType("application/dummy");
        Assert.assertNull(type);
    }
}
