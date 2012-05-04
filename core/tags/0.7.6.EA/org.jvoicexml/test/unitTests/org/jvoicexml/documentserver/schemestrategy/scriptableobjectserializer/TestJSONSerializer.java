/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.schemestrategy.scriptableobjectserializer;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.http.NameValuePair;
import org.junit.Test;
import org.jvoicexml.documentserver.schemestrategy.ScriptableObjectSerializer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.mozilla.javascript.ScriptableObject;

/**
 * Test cases for {@link JSONSerializer}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class TestJSONSerializer {

    /**
     * Test method for {@link org.jvoicexml.documentserver.schemestrategy.scriptableobjectserializer.JSONSerializer#serialize(org.mozilla.javascript.ScriptableObject)}.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testSerialize() throws JVoiceXMLEvent {
        final ScriptingEngine scripting = new ScriptingEngine(null);
        scripting.eval("var A = new Object();");
        scripting.eval("A.B = 'test';");
        scripting.eval("A.C = new Object();");
        scripting.eval("A.C.D = 42.0;");
        scripting.eval("A.C.E = null;");
        final ScriptableObjectSerializer serializer = new JSONSerializer();
        final ScriptableObject object =
            (ScriptableObject) scripting.getVariable("A");
       final Collection<NameValuePair> pairs =
           serializer.serialize("A", object);
       Assert.assertEquals(1, pairs.size());
       final Iterator<NameValuePair> iterator = pairs.iterator();
       final NameValuePair pair = iterator.next();
       Assert.assertEquals("A", pair.getName());
       Assert.assertEquals("{\"B\":\"test\",\"C\":{\"D\":42,\"E\":null}}",
               pair.getValue());
    }
}
