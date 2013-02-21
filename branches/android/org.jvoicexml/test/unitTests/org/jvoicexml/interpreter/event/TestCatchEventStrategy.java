/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/event/TestCatchEventStrategy.java $
 * Version: $LastChangedRevision: 2612 $
 * Date:    $Date: 2011-02-28 11:58:33 -0600 (lun, 28 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.config.DummyConfiguration;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link CatchEventStrategy}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2612 $
 * @since 0.7.3
 */
public final class TestCatchEventStrategy {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /**
     * Sets up the test environment.
     * @throws java.lang.Exception
     *         setup failed.
     */
    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, jvxml, null);
        final Configuration configuration = new DummyConfiguration();
        context = new VoiceXmlInterpreterContext(session, configuration);
        interpreter = new VoiceXmlInterpreter(context);
        interpreter.init(configuration);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.AbstractEventStrategy#isActive()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIsActive() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.setName("field");
        final Filled filled = field.appendChild(Filled.class);
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter, dialog);
        final FieldFormItem item = new FieldFormItem(context, field);
        final CatchEventStrategy strategy =
            new CatchEventStrategy(context, interpreter, fia, item, filled,
                    RecognitionEvent.EVENT_TYPE);
        Assert.assertTrue(strategy.isActive());
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.AbstractEventStrategy#isActive()}.
     * @exception JVoiceXMLEvent
     *            test failed
     * @exception Exception
     *            test failed
     */
    @Test
    public void testIsActiveCond() throws JVoiceXMLEvent, Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field1 = form.appendChild(Field.class);
        field1.setName("field");
        final Noinput noinput = field1.appendChild(Noinput.class);
        noinput.setCond("false");
        final Dialog dialog = new ExecutablePlainForm();
        dialog.setNode(form);
        final FormInterpretationAlgorithm fia =
            new FormInterpretationAlgorithm(context, interpreter, dialog);
        final FieldFormItem item = new FieldFormItem(context, field1);
        final CatchEventStrategy strategy =
            new CatchEventStrategy(context, interpreter, fia, item, noinput,
                    RecognitionEvent.EVENT_TYPE);
        Assert.assertFalse(strategy.isActive());
    }
}
