package org.jvoicexml.callmanager.mmi;

import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class EmmaSemanticInterpretationExtractorTest {
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
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens",
                tokens);
        emma.appendChild(interpretation);
        final JAXBContext context = JAXBContext.newInstance(Mmi.class);
        Marshaller marshaller = context.createMarshaller();
        final EmmaSemanticInterpretationExtractor extractor = new EmmaSemanticInterpretationExtractor();
        marshaller.marshal(mmi, extractor);
        final RecognitionResult result = extractor.getRecognitonResult();
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
    }

    @Test
    public void testSimpleSemanticIntperpretation() throws Exception {
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
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens",
                tokens);
        emma.appendChild(interpretation);
        final Element literal = document.createElementNS(EMMA_NAMESPACE,
                "emma:literal");
        interpretation.appendChild(literal);
        final Text text = document.createTextNode("test");
        literal.appendChild(text);
        final JAXBContext context = JAXBContext.newInstance(Mmi.class);
        Marshaller marshaller = context.createMarshaller();
        final EmmaSemanticInterpretationExtractor extractor = new EmmaSemanticInterpretationExtractor();
        marshaller.marshal(mmi, extractor);
        final RecognitionResult result = extractor.getRecognitonResult();
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
        Assert.assertEquals("test", result.getSemanticInterpretation());
    }

    @Test
    public void testCompundSemanticIntperpretation() throws Exception {
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
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens",
                tokens);
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
        final JAXBContext context = JAXBContext.newInstance(Mmi.class);
        Marshaller marshaller = context.createMarshaller();
        final EmmaSemanticInterpretationExtractor extractor = new EmmaSemanticInterpretationExtractor();
        marshaller.marshal(mmi, extractor);
        final RecognitionResult result = extractor.getRecognitonResult();
        Assert.assertEquals(tokens, result.getUtterance());
        Assert.assertEquals(confidence, result.getConfidence(), .001);
        final String json = ScriptingEngine.toJSON((ScriptableObject) result.getSemanticInterpretation());
        Assert.assertEquals("{\"topping\":\"Salami\",\"size\":\"medium\"}", json);
    }

}
