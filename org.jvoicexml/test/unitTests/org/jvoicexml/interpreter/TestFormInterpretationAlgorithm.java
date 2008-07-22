/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
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

package org.jvoicexml.interpreter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementationplatform.DummyImplementationPlatform;
import org.jvoicexml.test.implementationplatform.DummyUserInput;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link FormInterpretationAlgorithm}.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

public final class TestFormInterpretationAlgorithm {
    /** The VoiceXml interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The implementation platform. */
    private ImplementationPlatform platform;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        platform = new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session = new JVoiceXmlSession(platform, jvxml);
        context = new VoiceXmlInterpreterContext(session);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm#processGrammar(org.jvoicexml.xml.srgs.Grammar)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            Test failed.
     */
    @Test
    public void testProcessGrammar() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Grammar grammar = form.appendChild(Grammar.class);
        grammar.setVersion("1.0");
        grammar.setType(GrammarType.SRGS_XML);
        final Rule rule = grammar.appendChild(Rule.class);
        final OneOf oneof = rule.appendChild(OneOf.class);
        final Item item1 = oneof.appendChild(Item.class);
        item1.addText("visa");
        final Item item2 = oneof.appendChild(Item.class);
        item2.addText("mastercard");
        final Item item3 = oneof.appendChild(Item.class);
        item3.addText("american express");

        final Dialog executableForm = new ExecutablePlainForm(form);
        FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, null, executableForm);
        fia.processGrammar(grammar);
        final GrammarRegistry registry = context.getGrammarRegistry();
        final GrammarDocument grammarDoc =
            new JVoiceXmlGrammarDocument(grammar.toString());
        grammarDoc.setMediaType(grammar.getType());
        Assert.assertTrue(registry.contains(grammarDoc));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.FormInterpretationAlgorithm#visitFieldFormItem(org.jvoicexml.interpreter.formitem.AbstractInputItem)}.
     * @throws Exception
     *         Test failed.
     * @throws JVoiceXMLEvent
     *         Test failed.
     */
    @Test
    public void testVisitFieldFormItem() throws Exception, JVoiceXMLEvent {
        final VoiceXmlDocument doc = new VoiceXmlDocument();
        final Vxml vxml = doc.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);

        final Dialog executableForm = new ExecutablePlainForm(form);
        FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, null, executableForm);
        final InputItem item = new FieldFormItem(context, field);
        fia.visitFieldFormItem(item);
        final DummyUserInput input =
            (DummyUserInput) platform.getBorrowedUserInput();
        Assert.assertNotNull(input);
        Assert.assertTrue(input.isRecognitionStarted());
    }

}
