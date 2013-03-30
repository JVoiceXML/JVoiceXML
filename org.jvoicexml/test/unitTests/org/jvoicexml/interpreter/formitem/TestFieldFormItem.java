/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/formitem/TestFieldFormItem.java $
 * Version: $LastChangedRevision: 2618 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link FieldFormItem}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2618 $
 * @since 0.7.1
 */
public final class TestFieldFormItem {

    /**
     * Test case for {@link FieldFormItem#getGrammars()}.
     * @throws Exception
     *         test failed.
     * @since 0.7.1
     */
    @Test
    public void testGetGrammars() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang("en");
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("lo_fat_meal");
        field.setType("boolean");
        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Do you want a low fat meal on this flight?");

        final FieldFormItem item = new FieldFormItem(null, field);
        final Collection<Grammar> grammars = item.getGrammars();
        Assert.assertEquals(2, grammars.size());
        final Iterator<Grammar> iterator = grammars.iterator();
        final Grammar dtmfGrammar = iterator.next();
        Assert.assertEquals("builtin:dtmf/boolean", dtmfGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), dtmfGrammar.getXmlLang());
        final Grammar voiceGrammar = iterator.next();
        Assert.assertEquals("builtin:voice/boolean", voiceGrammar.getSrc());
        Assert.assertEquals(vxml.getXmlLang(), voiceGrammar.getXmlLang());
    }

    /**
     * Test case for {@link FieldFormItem#getSlot()}.
     * @exception Exception
     *            test failed
     * @since 0.7.2
     */
    @Test
    public void testGetSlot() throws Exception {
        final VoiceXmlDocument document1 = new VoiceXmlDocument();
        final Vxml vxml1 = document1.getVxml();
        final Form form1 = vxml1.appendChild(Form.class);
        final Field field1 = form1.appendChild(Field.class);
        field1.setName("lo_fat_meal");
        final FieldFormItem item1 = new FieldFormItem(null, field1);
        Assert.assertEquals(field1.getName(), item1.getSlot());

        final VoiceXmlDocument document2 = new VoiceXmlDocument();
        final Vxml vxml2 = document2.getVxml();
        final Form form2 = vxml2.appendChild(Form.class);
        final Field field2 = form2.appendChild(Field.class);
        field2.setName("lo_fat_meal");
        field2.setSlot("meal");
        final FieldFormItem item2 = new FieldFormItem(null, field2);
        Assert.assertEquals(field2.getSlot(), item2.getSlot());
    }
}
