/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.datamodel.ecmascript;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.Connection;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.srgs.ModeType;
import org.mockito.Mockito;

/**
 * Test cases for {@link ImplicitVariable}.
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class ImplicitVariableTest {

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.datamodel.ecmascript.ImplicitVariable#get(java.lang.String, org.mozilla.javascript.Scriptable)}
     * .
     * 
     * @throws SemanticError
     */
    @Test
    public void testGetStringScriptable() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        Assert.assertEquals(0, data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(0,
                data.createVariable(testvar, testvalue, Scope.SESSION));
        Assert.assertEquals(testvalue,
                data.readVariable("session." + testvar, Integer.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.datamodel.ecmascript.ImplicitVariable#get(java.lang.String, org.mozilla.javascript.Scriptable)}
     * .
     * 
     * @throws SemanticError
     */
    @Test
    public void testPutStringScriptable() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        Assert.assertEquals(0, data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(0,
                data.createVariable("session." + testvar, testvalue));
        Assert.assertEquals(testvalue,
                data.readVariable(testvar, Scope.SESSION, Integer.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.datamodel.ecmascript.ImplicitVariable#get(java.lang.String, org.mozilla.javascript.Scriptable)}
     * .
     * 
     * @throws SemanticError
     */
    @Test
    public void testGetStringScriptableConnectionInfo() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        Assert.assertEquals(0, data.createScope(Scope.APPLICATION));
        final ConnectionInformation info = Mockito.mock(ConnectionInformation.class);
        final Connection connection = new Connection(info);
        Assert.assertEquals(0, data.createVariable("session.connection",
                connection, Scope.SESSION));
        Assert.assertEquals(0,
                data.createVariable("con", connection, Scope.SESSION));
        Assert.assertEquals(info.getProtocolName(), data.evaluateExpression(
                "session.connection.protocol.name", Object.class));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.datamodel.ecmascript.ImplicitVariable#get(java.lang.String, org.mozilla.javascript.Scriptable)}
     * .
     * 
     * @throws SemanticError
     */
    @Test
    public void testGetApplicationLastResult() throws SemanticError {
        final DataModel model = new EcmaScriptDataModel();
        Assert.assertEquals(0, model.createScope(Scope.SESSION));
        Assert.assertEquals(0, model.createScope(Scope.APPLICATION));
        final RecognitionResult result = Mockito.mock(RecognitionResult.class);
        Mockito.when(result.getUtterance()).thenReturn("hello world");
        Mockito.when(result.getSemanticInterpretation(model)).thenReturn("hi");
        Mockito.when(result.getConfidence()).thenReturn(0.7f);
        Mockito.when(result.getMode()).thenReturn(ModeType.VOICE);
        Assert.assertEquals(0,
                model.createArray("lastresult$", 0, Scope.APPLICATION));
        Assert.assertEquals(0,
                model.resizeArray("lastresult$", 1, Scope.APPLICATION));
        final Object value = model.readArray("lastresult$", 0,
                Scope.APPLICATION, Object.class);
        Assert.assertEquals(
                0,
                model.createVariableFor(value, "confidence",
                        result.getConfidence()));
        Assert.assertEquals(
                0,
                model.createVariableFor(value, "utterance",
                        result.getUtterance()));
        Assert.assertEquals(0, model.createVariableFor(value, "inputmode",
                result.getMode().name()));
        Assert.assertEquals(
                0,
                model.createVariableFor(value, "interpretation",
                        result.getSemanticInterpretation(model)));
        Assert.assertEquals(0,
                model.updateArray("lastresult$", 0, value, Scope.APPLICATION));
        Assert.assertEquals(0, model.createVariable(
                "lastresult$.interpretation",
                result.getSemanticInterpretation(model), Scope.APPLICATION));
        Assert.assertEquals(
                0,
                model.createVariable("lastresult$.confidence",
                        result.getConfidence(), Scope.APPLICATION));
        Assert.assertEquals(
                0,
                model.createVariable("lastresult$.utterance",
                        result.getUtterance(), Scope.APPLICATION));
        Assert.assertEquals(0, model.createVariable("lastresult$.inputmode",
                result.getMode().name(), Scope.APPLICATION));
        Assert.assertEquals(result.getUtterance(), model.evaluateExpression(
                "application.lastresult$.utterance", String.class));
        Assert.assertEquals(new Float(result.getConfidence()), model
                .evaluateExpression("application.lastresult$.confidence",
                        Float.class));
        Assert.assertEquals(result.getSemanticInterpretation(model), model.evaluateExpression(
                "application.lastresult$.interpretation", String.class));
        Assert.assertEquals(result.getUtterance(), model.evaluateExpression(
                "application.lastresult$[0].utterance", String.class));
        Assert.assertEquals(new Float(result.getConfidence()), model
                .evaluateExpression("application.lastresult$[0].confidence",
                        Float.class));
        Assert.assertEquals(result.getSemanticInterpretation(model), model.evaluateExpression(
                "application.lastresult$[0].interpretation", String.class));
    }
}
