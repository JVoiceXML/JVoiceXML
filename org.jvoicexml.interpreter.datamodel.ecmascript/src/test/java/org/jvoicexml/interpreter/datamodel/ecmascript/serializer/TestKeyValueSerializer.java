/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.datamodel.ecmascript.serializer;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.interpreter.datamodel.ecmascript.EcmaScriptDataModel;
import org.jvoicexml.interpreter.scope.Scope;

/**
 * Test cases for {@link KeyValueSerializer}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class TestKeyValueSerializer {

    /**
     * Test method for
     * {@link KeyValueSerializer#serialize(DataModel, String, Object)}. .
     * 
     * @exception JVoiceXMLEvent
     *                test failed
     */
    @Test
    public void testSerialize() throws JVoiceXMLEvent {
        final DataModel model = new EcmaScriptDataModel();
        model.createScope(Scope.SESSION);
        model.evaluateExpression("var A = new Object();", Object.class);
        model.evaluateExpression("A.B = 'test';", Object.class);
        model.evaluateExpression("A.C = new Object();", Object.class);
        model.evaluateExpression("A.C.D = 42.0;", Object.class);
        model.evaluateExpression("A.C.E = null;", Object.class);
        final DataModelObjectSerializer serializer = new KeyValueSerializer();
        final Object object = model.readVariable("A", Object.class);
        final Collection<KeyValuePair> pairs = serializer.serialize(model, "A",
                object);
        Assert.assertEquals(2, pairs.size());
        final Iterator<KeyValuePair> iterator = pairs.iterator();
        final KeyValuePair pair1 = iterator.next();
        Assert.assertEquals("A.B", pair1.getKey());
        Assert.assertEquals("test", pair1.getValue());
        final KeyValuePair pair2 = iterator.next();
        Assert.assertEquals("A.C.D", pair2.getKey());
        Assert.assertEquals("42", pair2.getValue());
    }
}
