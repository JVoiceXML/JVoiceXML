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

import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlExtensionNotificationDataExtractorTest {
    /** The EMMA namespace. */
    private static final String EMMA_NAMESPACE = "http://www.w3.org/2003/04/emma";

    @Test
    public void testSimple() throws Exception {
        final Mmi mmi = new Mmi();
        final ExtensionNotification notification = new ExtensionNotification();
        mmi.setExtensionNotification(notification);
        notification.setContext(UUID.randomUUID().toString());
        notification.setRequestId("42");
        notification.setSource("fromhere");
        notification.setTarget("tothere");
        final AnyComplexType any = new AnyComplexType();
        notification.setData(any);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.newDocument();
        final Element emma = document.createElementNS(EMMA_NAMESPACE,
                "emma:emma");
        emma.setAttribute("version", "1.0");
        document.appendChild(emma);
        final Element interpretation = document.createElementNS(EMMA_NAMESPACE,
                "emma:interpretation");
        interpretation.setAttribute("id", "dummy");
        interpretation
                .setAttributeNS(EMMA_NAMESPACE, "emma:medium", "acoustic");
        any.addContent(emma);
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:mode", "mmi");
        float confidence = 0.4f;
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:confidence",
                Float.toString(confidence));
        final String tokens = "this is a test";
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens", tokens);
        emma.appendChild(interpretation);
        final XmlExtensionNotificationDataExtractor extractor = new XmlExtensionNotificationDataExtractor();
        final ExtensionNotification ext = mmi.getExtensionNotification();
        final RecognitionResult result = extractor.getRecognitionResult(mmi,
                ext);
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
    }

    @Test
    public void testSimpleSemanticIntperpretation() throws Exception, SemanticError {
        final Mmi mmi = new Mmi();
        final ExtensionNotification notification = new ExtensionNotification();
        mmi.setExtensionNotification(notification);
        notification.setContext(UUID.randomUUID().toString());
        notification.setRequestId("42");
        notification.setSource("fromhere");
        notification.setTarget("tothere");
        final AnyComplexType any = new AnyComplexType();
        notification.setData(any);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.newDocument();
        final Element emma = document.createElementNS(EMMA_NAMESPACE,
                "emma:emma");
        emma.setAttribute("version", "1.0");
        document.appendChild(emma);
        final Element interpretation = document.createElementNS(EMMA_NAMESPACE,
                "emma:interpretation");
        interpretation.setAttribute("id", "dummy");
        interpretation
                .setAttributeNS(EMMA_NAMESPACE, "emma:medium", "acoustic");
        any.addContent(emma);
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:mode", "mmi");
        float confidence = 0.4f;
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:confidence",
                Float.toString(confidence));
        final String tokens = "this is a test";
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens", tokens);
        emma.appendChild(interpretation);
        final Element literal = document.createElementNS(EMMA_NAMESPACE,
                "emma:literal");
        interpretation.appendChild(literal);
        final Text text = document.createTextNode("test");
        literal.appendChild(text);
        final XmlExtensionNotificationDataExtractor extractor = new XmlExtensionNotificationDataExtractor();
        final ExtensionNotification ext = mmi.getExtensionNotification();
        final RecognitionResult result = extractor.getRecognitionResult(mmi,
                ext);
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
        final DataModel model = Mockito.mock(DataModel.class);
        Assert.assertEquals("test", result.getSemanticInterpretation(model));
    }

    @Test
    public void testCompundSemanticIntperpretation() throws Exception, SemanticError {
        final Mmi mmi = new Mmi();
        final ExtensionNotification notification = new ExtensionNotification();
        mmi.setExtensionNotification(notification);
        notification.setContext(UUID.randomUUID().toString());
        notification.setRequestId("42");
        notification.setSource("fromhere");
        notification.setTarget("tothere");
        final AnyComplexType any = new AnyComplexType();
        notification.setData(any);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.newDocument();
        final Element emma = document.createElementNS(EMMA_NAMESPACE,
                "emma:emma");
        emma.setAttribute("version", "1.0");
        document.appendChild(emma);
        final Element interpretation = document.createElementNS(EMMA_NAMESPACE,
                "emma:interpretation");
        interpretation.setAttribute("id", "dummy");
        interpretation
                .setAttributeNS(EMMA_NAMESPACE, "emma:medium", "acoustic");
        any.addContent(emma);
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:mode", "mmi");
        float confidence = 0.4f;
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:confidence",
                Float.toString(confidence));
        final String tokens = "this is a test";
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens", tokens);
        emma.appendChild(interpretation);
        final Element order = document.createElement("order");
        interpretation.appendChild(order);
        final Element topping = document.createElement("topping");
        order.appendChild(topping);
        final Text orderText = document.createTextNode("Salami");
        topping.appendChild(orderText);
        final Element size = document.createElement("size");
        order.appendChild(size);
        final Text sizeText = document.createTextNode("medium");
        size.appendChild(sizeText);
        final XmlExtensionNotificationDataExtractor extractor = new XmlExtensionNotificationDataExtractor();
        final ExtensionNotification ext = mmi.getExtensionNotification();
        final RecognitionResult result = extractor.getRecognitionResult(mmi,
                ext);
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
        final DataModel model = Mockito.mock(DataModel.class);
        System.out.println(result.getSemanticInterpretation(model));
//        final String json = ScriptingEngine.toJSON((ScriptableObject) result
//                .getSemanticInterpretation());
//        Assert.assertEquals("{\"topping\":\"Salami\",\"size\":\"medium\"}",
//                json);
    }

}
