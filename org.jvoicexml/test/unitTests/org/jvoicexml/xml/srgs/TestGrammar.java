/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test cases for {@link Grammar}.
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.7
 */
public final class TestGrammar {

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRootRule()}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetRootRule() throws Exception {
        SrgsXmlDocument document = new SrgsXmlDocument();
        Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");

        Rule rootRule = grammar.getRootRule();
        Assert.assertEquals(rule, rootRule);
    }

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Grammar#getRule(String))}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testGetRule() throws Exception {
        SrgsXmlDocument document = new SrgsXmlDocument();
        Grammar grammar = document.getGrammar();
        grammar.setRoot("test1");
        Rule rule1 = grammar.appendChild(Rule.class);
        rule1.setId("test1");
        Rule rule2 = grammar.appendChild(Rule.class);
        rule2.setId("test2");

        Rule rule1Node = grammar.getRule("test1");
        Assert.assertEquals(rule1, rule1Node);
        Rule rule2Node = grammar.getRule("test2");
        Assert.assertEquals(rule2, rule2Node);
    }
}
