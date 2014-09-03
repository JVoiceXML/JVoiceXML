package org.jvoicexml.callmanager.mmi;

import java.util.List;

import org.junit.Test;
import org.jvoicexml.LastResult;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class EmmaExtensionNotificationDataConverterTest {

    @Test
    public void testConvertApplicationLastResult() throws Exception {
        final XmlExtensionNotificationDataConverter converter = new XmlExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final LastResult result = new LastResult("yes", .75f, "voice", null);
        list.add(result);
        converter.convertApplicationLastResult(list);
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
        final LastResult result = new LastResult("yes", .75f, "voice", out);
        list.add(result);
        converter.convertApplicationLastResult(list);
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
        converter.convertApplicationLastResult(list);
    }

}
