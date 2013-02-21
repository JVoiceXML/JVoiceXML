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
 * Test case for {@link org.jvoicexml.xml.srgs.Item}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */

public final class TestItem {
    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Item#getMinRepeat()}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testGetMinRepeat() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf politeOneOf = rule.appendChild(OneOf.class);
        final Item item = politeOneOf.appendChild(Item.class);
        Assert.assertEquals(1, item.getMinRepeat());
        item.setOptional();
        Assert.assertEquals(0, item.getMinRepeat());
        final int repeat = 6;
        item.setRepeat(repeat);
        Assert.assertEquals(repeat, item.getMinRepeat());
        final int min = 4;
        final int max = 7;
        item.setRepeat(min, max);
        Assert.assertEquals(min, item.getMinRepeat());
        final int min2 = 4;
        final int max2 = -1;
        item.setRepeat(min2, max2);
        Assert.assertEquals(min2, item.getMinRepeat());
    }

    /**
     * Test method for {@link org.jvoicexml.xml.srgs.Item#getMaxRepeat()}.
     * @exception Exception
     *            test failed.
     */
    @Test
    public void testGetMaxRepeat() throws Exception {
        final SrgsXmlDocument document = new SrgsXmlDocument();
        final Grammar grammar = document.getGrammar();
        grammar.setRoot("test");
        final Rule rule = grammar.appendChild(Rule.class);
        rule.setId("test");
        final OneOf politeOneOf = rule.appendChild(OneOf.class);
        final Item item = politeOneOf.appendChild(Item.class);
        Assert.assertEquals(1, item.getMaxRepeat());
        item.setOptional();
        Assert.assertEquals(1, item.getMaxRepeat());
        final int repeat = 6;
        item.setRepeat(repeat);
        Assert.assertEquals(repeat, item.getMaxRepeat());
        final int min = 4;
        final int max = 7;
        item.setRepeat(min, max);
        Assert.assertEquals(max, item.getMaxRepeat());
        final int min2 = 4;
        final int max2 = -1;
        item.setRepeat(min2, max2);
        Assert.assertEquals(max2, item.getMaxRepeat());
    }
}
