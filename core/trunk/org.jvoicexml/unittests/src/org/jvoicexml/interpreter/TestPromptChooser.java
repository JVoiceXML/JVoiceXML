/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.interpreter;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link PromptChooser}. 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.6
 */
public class TestPromptChooser {
    /** The VoiceXmlInterpreterContext to use. */
    private VoiceXmlInterpreterContext context;
    /** The document containing the field. */
    private VoiceXmlDocument document;
    /** The field for that field form item to test. */
    private Field field;
    
    /**
     * Set up the test environment.
     * @throws Exception
     *         set up failed
     * @throws JVoiceXMLEvent 
     *         set up failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang("en");
        final Form form = vxml.appendChild(Form.class);
        field = form.appendChild(Field.class);
        field.setName("testfield");
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final ImplementationPlatform platform =
                new DummyImplementationPlatform();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.PromptChooser#collect()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testCollect() throws JVoiceXMLEvent {
        final Prompt prompt1 = field.appendChild(Prompt.class);
        prompt1.addText("prompt 1");
        final Prompt prompt2 = field.appendChild(Prompt.class);
        prompt2.addText("prompt 2");
        final PromptCountable countable = new FieldFormItem(context, field); 
        final PromptChooser chooser = new PromptChooser(countable, context);
        final Collection<Prompt> prompts = chooser.collect();
        Assert.assertEquals(2, prompts.size());
        final Iterator<Prompt> iterator = prompts.iterator();
        Assert.assertEquals(prompt1, iterator.next());
        Assert.assertEquals(prompt2, iterator.next());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.PromptChooser#collect()}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testCollectCond() throws JVoiceXMLEvent {
        final Prompt prompt1 = field.appendChild(Prompt.class);
        prompt1.addText("prompt 1");
        prompt1.setCond("1 == 2");
        final Prompt prompt2 = field.appendChild(Prompt.class);
        prompt2.addText("prompt 2");
        prompt2.setCond("2 == 2");
        final PromptCountable countable = new FieldFormItem(context, field); 
        final PromptChooser chooser = new PromptChooser(countable, context);
        final Collection<Prompt> prompts = chooser.collect();
        Assert.assertEquals(1, prompts.size());
        final Iterator<Prompt> iterator = prompts.iterator();
        Assert.assertEquals(prompt2, iterator.next());
    }
}
