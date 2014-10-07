/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi.json;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.LastResult;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Test cases for {@link JsonExtensionNotificationDataConverter}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class JsonExtensionNotificationDataConverterTest {
    @Test
    public void testConvertApplicationLastResult() throws Exception {
        final JsonExtensionNotificationDataConverter converter = new JsonExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final LastResult result = new LastResult("yes", .75f, "voice", null);
        list.add(result);
        final String json = (String) converter
                .convertApplicationLastResult(list);
        Assert.assertEquals("vxml.input = {\"utterance\":\"yes\","
                + "\"confidence\":0.75,\"interpretation\":null,"
                + "\"mode\":\"voice\"}", json);
    }

    @Test
    public void testConvertApplicationLastResultSimpleSemanticInterpretation()
            throws Exception {
        final JsonExtensionNotificationDataConverter converter = new JsonExtensionNotificationDataConverter();
        final List<LastResult> list = new java.util.ArrayList<LastResult>();
        final Context context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        final Scriptable scope = context.initStandardObjects();
        context.evaluateString(scope, "var out = 'yes';", "expr", 1, null);
        final Object out = scope.get("out", scope);
        final LastResult result = new LastResult("yeah", .75f, "voice", out);
        list.add(result);
        final String json = (String) converter
                .convertApplicationLastResult(list);
        Assert.assertEquals("vxml.input = {\"utterance\":\"yeah\","
                + "\"confidence\":0.75,\"interpretation\":\"yes\","
                + "\"mode\":\"voice\"}", json);
    }

    @Test
    public void testConvertApplicationLastResultComplexSemanticInterpretation()
            throws Exception {
        final JsonExtensionNotificationDataConverter converter = new JsonExtensionNotificationDataConverter();
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
        final String json = (String) converter
                .convertApplicationLastResult(list);
        Assert.assertEquals("vxml.input = {\"utterance\":\"yes\","
                + "\"confidence\":0.75,\"interpretation\":{\"order\":"
                + "{\"topping\":\"Salami\",\"size\":\"medium\"},"
                + "\"date\":\"now\"},\"mode\":\"voice\"}", json);
    }

}
