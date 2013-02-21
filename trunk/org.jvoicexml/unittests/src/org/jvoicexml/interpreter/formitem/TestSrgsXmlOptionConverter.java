/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Option;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public final class TestSrgsXmlOptionConverter {

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter#createVoiceGrammar(java.util.Collection, java.util.Locale)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateVoiceGrammar() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Option option1 = field.appendChild(Option.class);
        option1.addText("one");
        final Option option2 = field.appendChild(Option.class);
        option2.addText("two");
        final Option option3 = field.appendChild(Option.class);
        option3.addText("three");
        final Collection<Option> options = field.getChildNodes(Option.class);
        final OptionConverter converter = new SrgsXmlOptionConverter();
        final Grammar grammar = converter.createVoiceGrammar(options,
                Locale.getDefault());
        final Rule rule = grammar.getRootRule();
        Assert.assertNotNull(rule);
        final OneOf oneof = rule.getChildNodes(OneOf.class).iterator().next();
        Assert.assertNotNull(oneof);
        final Collection<Item> items = oneof.getChildNodes(Item.class);
        Assert.assertEquals(3, items.size());
        final Iterator<Item> iterator = items.iterator();
        final Item item1 = iterator.next();
        Assert.assertEquals(option1.getTextContent(), item1.getTextContent());
        final Item item2 = iterator.next();
        Assert.assertEquals(option2.getTextContent(), item2.getTextContent());
        final Item item3 = iterator.next();
        Assert.assertEquals(option3.getTextContent(), item3.getTextContent());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter#createVoiceGrammar(java.util.Collection, java.util.Locale)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateVoiceGrammarNoOptions() throws Exception {
        final Collection<Option> options = new java.util.ArrayList<Option>();
        final OptionConverter converter = new SrgsXmlOptionConverter();
        final Grammar grammar = converter.createVoiceGrammar(options,
                Locale.getDefault());
        Assert.assertNull(grammar);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter#createDtmfGrammar(java.util.Collection, java.util.Locale)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateDtmfGrammar() throws Exception  {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Option option1 = field.appendChild(Option.class);
        option1.addText("one");
        option1.setDtmf("1");
        final Option option2 = field.appendChild(Option.class);
        option2.addText("two");
        final Option option3 = field.appendChild(Option.class);
        option3.addText("three");
        option3.setDtmf("3");
        final Collection<Option> options = field.getChildNodes(Option.class);
        final OptionConverter converter = new SrgsXmlOptionConverter();
        final Grammar grammar = converter.createDtmfGrammar(options);
        final Rule rule = grammar.getRootRule();
        Assert.assertNotNull(rule);
        final OneOf oneof = rule.getChildNodes(OneOf.class).iterator().next();
        Assert.assertNotNull(oneof);
        final Collection<Item> items = oneof.getChildNodes(Item.class);
        Assert.assertEquals(2, items.size());
        final Iterator<Item> iterator = items.iterator();
        final Item item1 = iterator.next();
        Assert.assertEquals(option1.getDtmf(), item1.getTextContent());
        final Item item3 = iterator.next();
        Assert.assertEquals(option3.getDtmf(), item3.getTextContent());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter#createDtmfGrammar(java.util.Collection, java.util.Locale)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateDtmfGrammarNoDtmf() throws Exception  {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final Option option1 = field.appendChild(Option.class);
        option1.addText("one");
        final Option option2 = field.appendChild(Option.class);
        option2.addText("two");
        final Option option3 = field.appendChild(Option.class);
        option3.addText("three");
        final Collection<Option> options = field.getChildNodes(Option.class);
        final OptionConverter converter = new SrgsXmlOptionConverter();
        final Grammar grammar = converter.createDtmfGrammar(options);
        Assert.assertNull(grammar);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.SrgsXmlOptionConverter#createVoiceGrammar(java.util.Collection, java.util.Locale)}.
     * @exception Exception
     *            test failed
     */
    @Test
    public void testCreateDtmfGrammarNoOptions() throws Exception {
        final Collection<Option> options = new java.util.ArrayList<Option>();
        final OptionConverter converter = new SrgsXmlOptionConverter();
        final Grammar grammar = converter.createDtmfGrammar(options);
        Assert.assertNull(grammar);
    }
}
