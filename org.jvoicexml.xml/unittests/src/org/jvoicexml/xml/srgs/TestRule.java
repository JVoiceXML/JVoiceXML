/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.xml.srgs;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for {@link Rule}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2325 $
 * @since 0.7.5
 */
public class TestRule {


    /**
     * Test of {@link Rule#makePrivate()}.
     * @exception Exception
     *            test failes
     */
    @Test
    public void testMakePrivate() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule = grammar.appendChild(Rule.class);
        rule.makePrivate();
        Assert.assertEquals(null, rule.getScope());
    }

    /**
     * Test of {@link Rule#makePublic()}.
     * @exception Exception
     *            test failes
     */
    @Test
    public void testMakePublic() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule = grammar.appendChild(Rule.class);
        rule.makePublic();
        Assert.assertEquals("public", rule.getScope());
    }

    /**
     * Test of isPublic method, of class Rule.
     * @exception Exception
     *            test failes
     */
    @Test
    public void testIsPublic() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        final Rule rule = grammar.appendChild(Rule.class);
        Assert.assertFalse(rule.isPublic());
        rule.makePublic();
        Assert.assertTrue(rule.isPublic());
        rule.setScope("private");
        Assert.assertFalse(rule.isPublic());
    }
}