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

package org.jvoicexml.interpreter.event;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;
import org.jvoicexml.interpreter.formitem.FieldFormItem;
import org.jvoicexml.xml.vxml.Catch;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Help;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test cases for {@link JVoiceXmlEventHandler}.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 */
public final class TestJVoiceXmlEventHandler {

    /**
     * Test method for {@link org.jvoicexml.interpreter.event.JVoiceXmlEventHandler#collect(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.formitem.InputItem)}.
     * @exception Exception test failed.
     */
    @Test
    public void testCollect() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Field field = form.appendChild(Field.class);
        field.appendChild(Filled.class);
        field.appendChild(Noinput.class);
        field.appendChild(Help.class);
        final Catch catchNode = field.appendChild(Catch.class);
        catchNode.setEvent("test");

        final FieldFormItem item = new FieldFormItem(null, field);
        final JVoiceXmlEventHandler handler = new JVoiceXmlEventHandler(null);
        handler.collect(null, null, null, item);

        final Collection<AbstractEventStrategy> strategies =
            handler.getStrategies();
        Assert.assertEquals(4, strategies.size());
        Assert.assertTrue("expected to find type test",
                containsType(strategies, "test"));
        Assert.assertTrue("expected to find type noinput",
                containsType(strategies, "noinput"));
        Assert.assertTrue("expected to find type help",
                containsType(strategies, "help"));
        Assert.assertTrue("expected to find type "
                + RecognitionEvent.EVENT_TYPE,
                containsType(strategies, RecognitionEvent.EVENT_TYPE));
    }

    /**
     * Checks if the given type has a corresponding entry in the list of
     * strategies.
     * @param strategies the strategies to check
     * @param type the type to look for
     * @return <code>true</code> if the type is contained in the list.
     */
    private boolean containsType(
            final Collection<AbstractEventStrategy> strategies,
            final String type) {
        for (AbstractEventStrategy strategy : strategies) {
            final String currentType = strategy.getEventType();
            if (type.equals(currentType)) {
                return true;
            }
        }
        return false;
    }
}
