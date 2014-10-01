package org.jvoicexml.callmanager.mmi;

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
