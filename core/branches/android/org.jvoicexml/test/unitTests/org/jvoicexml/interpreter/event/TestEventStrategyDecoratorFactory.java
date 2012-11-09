/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/event/TestEventStrategyDecoratorFactory.java $
 * Version: $LastChangedRevision: 2397 $
 * Date:    $Date: 2010-11-24 02:14:18 -0600 (mié, 24 nov 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.interpreter.EventStrategy;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.interpreter.formitem.InitialFormItem;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.interpreter.formitem.RecordFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Record;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link EventStrategyDecoratorFactory}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2397 $
 * @since 0.7
 */
public final class TestEventStrategyDecoratorFactory {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /**
     * Prepares the testing environment.
     */
    @Before
    public void setUp() {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        context = new VoiceXmlInterpreterContext(session, null);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.EventStrategyDecoratorFactory#getDecorator(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.InputItem)}.
     * @exception Exception test failed.
     */
    @Test
    public void testGetDecorator() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        final ObjectTag object = form.appendChild(ObjectTag.class);
        final Record record = form.appendChild(Record.class);
        final Initial initial = form.appendChild(Initial.class);

        final FieldFormItem fieldItem = new FieldFormItem(context, field);
        final EventStrategyDecoratorFactory factory =
            new EventStrategyDecoratorFactory();
        final EventStrategy strategy1 =
            factory.getDecorator(context, null, null, fieldItem);
        Assert.assertNotNull(strategy1);
        Assert.assertEquals(InputItemRecognitionEventStrategy.class,
                strategy1.getClass());

        final RecordFormItem recordItem = new RecordFormItem(context, record);
        final EventStrategy strategy2 =
            factory.getDecorator(context, null, null, recordItem);
        Assert.assertNotNull(strategy2);
        Assert.assertEquals(RecordingEventStrategy.class,
                strategy2.getClass());

        final ObjectFormItem objectItem = new ObjectFormItem(context, object);
        final EventStrategy strategy3 =
            factory.getDecorator(context, null, null, objectItem);
        Assert.assertNotNull(strategy3);
        Assert.assertEquals(ObjectTagEventStrategy.class,
                strategy3.getClass());

        final InitialFormItem initialItem = new InitialFormItem(context,
                initial);
        final EventStrategy strategy4 =
            factory.getDecorator(context, null, null, initialItem);
        Assert.assertNotNull(strategy4);
        Assert.assertEquals(FormLevelRecognitionEventStrategy.class,
                strategy4.getClass());
        
        final EventStrategy strategy5 =
            factory.getDecorator(context, null, null, null);
        Assert.assertNull("expected a null strategy for a null input item",
                strategy5);
    }
}
