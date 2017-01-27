/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/FieldShadowVarContainer.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.callmanager.mmi.xml;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.jvoicexml.LastResult;
import org.jvoicexml.callmanager.mmi.xml.XmlExtensionNotificationDataConverter;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;

public class XmlExtensionNotificationDataConverterTest {

    @Test
    public void testQueueEmpty() throws Exception {
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final QueueEmptyEvent empty = new QueueEmptyEvent(null, null);
        Element element = (Element) converter
                .convertSynthesizedOutputEvent(empty);
        System.out.println(toString(element));
    }

    @Test
    public void testConvertApplicationLastResult() throws Exception {
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final LastResult result = new LastResult("yes", .75f, "voice", null);
        list.add(result);
        final Element element = (Element) converter.convertApplicationLastResult(list);
        System.out.println(toString(element));
    }

    @Test
    public void testConvertApplicationLastResultSimpleSemanticInterpretation()
            throws Exception {
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();
        context.evaluateString(scope, "var out = 'yes';", "expr", 1, null);
        final Object out = scope.get("out", scope);
        final LastResult result = new LastResult("yeah", .75f, "voice", out);
        list.add(result);
        Element element = (Element) converter.convertApplicationLastResult(list);
        System.out.println(toString(element));
    }

    @Test
    public void testConvertApplicationLastResultComplexSemanticInterpretation()
            throws Exception {
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();
        context.evaluateString(scope, "var out = new Object();", "expr", 1,
                null);
        context.evaluateString(scope, "var out = new Object();", "expr", 1,
                null);
        context.evaluateString(scope, "out.order = new Object();", "expr", 1,
                null);
        context.evaluateString(scope, "out.order.topping = 'Salami';", "expr",
                1, null);
        context.evaluateString(scope, "out.order.size = 'medium';", "expr", 1,
                null);
        context.evaluateString(scope, "out.date = 'now';", "expr", 1, null);
        final Object out = scope.get("out", scope);
        final LastResult result = new LastResult("yes", .75f, "voice", out);
        list.add(result);
        final Element element = (Element) converter
                .convertApplicationLastResult(list);
        System.out.println(toString(element));
    }

    public String toString(Element element) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out);
        final TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        try {
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
                    "yes");
            final Source source = new DOMSource(element);
            transformer.transform(source, result);
            return out.toString();
        } catch (TransformerException e) {
            return super.toString();
        }
    }

}
