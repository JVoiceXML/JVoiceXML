/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Test cases for {@link PromptChooser}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public final class TestPromptChooser {

    public static final String TRUE_CONDITION = "2 == 2";
    public static final String FALSE_CONDITION = "1 == 2";

    /**
     * Prompts with no condition set have 'true' condition value
     */
    public static final String DEFAULT_CONDITION = "true";

    /** The VoiceXmlInterpreterContext to use. */
    private VoiceXmlInterpreterContext context;
    /** The document containing the field. */
    private VoiceXmlDocument document;
    /** The field for that field form item to test. */
    private Field field;

    /**
     * Set up the test environment.
     * 
     * @throws Exception
     *             set up failed
     * @throws JVoiceXMLEvent
     *             set up failed
     */
    @Before
    public void setUp() throws Exception, JVoiceXMLEvent {
        document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        vxml.setXmlLang("en");
        final Form form = vxml.appendChild(Form.class);
        field = form.appendChild(Field.class);
        field.setName("testfield");
        final JVoiceXmlCore jvxml = Mockito.mock(JVoiceXmlCore.class);
        final Configuration configuration = Mockito.mock(Configuration.class);
        when(jvxml.getConfiguration()).thenReturn(configuration);
        final ImplementationPlatform platform = Mockito
                .mock(ImplementationPlatform.class);
        final Profile profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);
        final SessionIdentifier id = new UuidSessionIdentifier();
        final JVoiceXmlSession session = Mockito.spy(new JVoiceXmlSession(
                platform, jvxml, null, profile, id));
        context = Mockito.spy(new VoiceXmlInterpreterContext(session,
                configuration));
        when(session.getVoiceXmlInterpreterContext()).thenReturn(
                context);

        final DataModel model = Mockito.mock(DataModel.class);
        when(model.evaluateExpression(eq(DEFAULT_CONDITION), any())).thenReturn(Boolean.TRUE);
        when(model.evaluateExpression(eq(TRUE_CONDITION), any())).thenReturn(Boolean.TRUE);
        when(model.evaluateExpression(eq(FALSE_CONDITION), any())).thenReturn(Boolean.FALSE);
        Mockito.doReturn(model).when(context).getDataModel();
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.PromptChooser#collect()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testCollectPromptsWithoutCondition() throws JVoiceXMLEvent {
        final Prompt prompt1 = field.appendChild(Prompt.class);
        prompt1.addText("prompt 1");
        final Prompt prompt2 = field.appendChild(Prompt.class);
        prompt2.addText("prompt 2");
        final PromptCountable countable = new FieldFormItem(context, field);
        final PromptChooser chooser = new PromptChooser(countable, context);
        final Collection<VoiceXmlNode> prompts = chooser.collect();
        Assert.assertEquals(2, prompts.size());
        final Iterator<VoiceXmlNode> iterator = prompts.iterator();
        Assert.assertEquals(prompt1, iterator.next());
        Assert.assertEquals(prompt2, iterator.next());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.PromptChooser#collect()}
     * .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testCollectPromptsWithCondition() throws JVoiceXMLEvent {
        final Prompt prompt1 = field.appendChild(Prompt.class);
        prompt1.addText("prompt 1");
        prompt1.setCond(FALSE_CONDITION);

        final Prompt prompt2 = field.appendChild(Prompt.class);
        prompt2.addText("prompt 2");
        prompt2.setCond(TRUE_CONDITION);

        final PromptCountable countable = new FieldFormItem(context, field);
        final PromptChooser chooser = new PromptChooser(countable, context);
        final Collection<VoiceXmlNode> prompts = chooser.collect();
        Assert.assertEquals(1, prompts.size());
        final Iterator<VoiceXmlNode> iterator = prompts.iterator();
        Assert.assertEquals(prompt2, iterator.next());
    }
}
