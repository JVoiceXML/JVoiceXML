/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.scope.Scope;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test cases for {@link DataModel}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class EcmaScriptDataModelTest {
    /** Any exception in async tests. */
    Throwable throwable;

    @Test
    public void testGetUndefinedValue() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(Context.getUndefinedValue(),
                data.getUndefinedValue());
    }

    @Test
    public void testCreateScopeScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
    }

    @Test
    public void testCreateScopeNullScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope(null));
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope(null));
    }

    @Test
    public void testCreateScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope());
    }

    @Test
    public void testDeleteScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope());
        Assert.assertEquals(DataModel.ERROR_SCOPE_NOT_FOUND,
                data.deleteScope());
    }

    @Test
    public void testDeleteScopeScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.ERROR_SCOPE_NOT_FOUND,
                data.deleteScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.SESSION));
        Assert.assertEquals(DataModel.ERROR_SCOPE_NOT_FOUND,
                data.deleteScope());
    }

    @Test
    public void testDeleteScopeScopeWithAnonymous() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope());
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope());
        Assert.assertEquals(DataModel.ERROR_SCOPE_NOT_FOUND,
                data.deleteScope());
    }

    @Test
    public void testCreateVariableString() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
    }

    @Test
    public void testCreateVariableStringMultipleScopes() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
    }

    @Test
    public void testCreateVariableStringMultipleScopesReenter() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
    }

    @Test
    public void testCreateVariableStringMultipleScopesReenterSetValue()
            throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope(Scope.DIALOG));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.updateVariable(testvar, 42));
        Assert.assertEquals(new Integer(42),
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.ANONYMOUS));
        Assert.assertEquals(Boolean.TRUE, data.existsVariable(testvar));
        Assert.assertEquals(new Integer(42),
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.updateVariable(testvar, 43));
        Assert.assertEquals(new Integer(43),
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.ANONYMOUS));
        Assert.assertEquals(Boolean.TRUE, data.existsVariable(testvar));
        Assert.assertEquals(new Integer(42),
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope(Scope.DIALOG));
        Assert.assertEquals(Boolean.FALSE, data.existsVariable(testvar));
        Assert.assertEquals(DataModel.NO_ERROR, data.createScope(Scope.DIALOG));
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteScope(Scope.DIALOG));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.APPLICATION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteScope(Scope.SESSION));
    }

    @Test
    public void testCreateVariableStringNested() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvarlevel2 = "testvar.level1.level2";
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvarlevel2));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1.level2"));
    }

    @Test
    public void testCreateVariableStringObject() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar, testvalue));
    }

    @Test
    public void testCreateVariableStringObjectScope() {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue, Scope.SESSION));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar, testvalue, Scope.SESSION));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable(testvar, testvalue, Scope.APPLICATION));
    }

    @Test
    public void testDeleteVariableString() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue, Scope.SESSION));
        Assert.assertEquals(testvalue,
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR, data.deleteVariable(testvar));
        SemanticError error = null;
        try {
            data.readVariable(testvar, Integer.class);
        } catch (SemanticError e) {
            error = e;
        }
        Assert.assertNotNull(testvar + " is not deleted", error);
    }

    @Test
    public void testDeleteVariableStringUndefined() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.ERROR_VARIABLE_NOT_FOUND,
                data.deleteVariable(testvar));
    }

    @Test
    public void testDeleteVariableStringScope() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue, Scope.SESSION));
        Assert.assertEquals(testvalue,
                data.readVariable(testvar, Integer.class));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.deleteVariable(testvar, Scope.SESSION));
        SemanticError error = null;
        try {
            data.readVariable(testvar, Integer.class);
        } catch (SemanticError e) {
            error = e;
        }
        Assert.assertNotNull(testvar + " is not deleted", error);
    }

    @Test
    public void testUpdateVariableStringObject() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue1 = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue1));
        Assert.assertEquals(testvalue1,
                data.readVariable(testvar, Integer.class));
        final Object testvalue2 = "testvalue";
        Assert.assertEquals(DataModel.NO_ERROR,
                data.updateVariable(testvar, testvalue2));
        Assert.assertEquals(testvalue2,
                data.readVariable(testvar, String.class));
        data.createVariable("hurz", new Object());
    }

    @Test
    public void testUpdateVariableStringObjectScope() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final Object testvalue1 = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue1, Scope.SESSION));
        Assert.assertEquals(testvalue1,
                data.readVariable(testvar, Integer.class));
        final Object testvalue2 = "testvalue";
        Assert.assertEquals(DataModel.NO_ERROR,
                data.updateVariable(testvar, testvalue2, Scope.APPLICATION));
        Assert.assertEquals(testvalue2,
                data.readVariable(testvar, Scope.SESSION, String.class));
        Assert.assertEquals(testvalue2,
                data.readVariable(testvar, Scope.APPLICATION, String.class));
    }

    @Test
    public void testReadVariableString() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        Assert.assertEquals(DataModel.NO_ERROR, data.createVariable(testvar));
        Assert.assertEquals(null, data.readVariable(testvar, Object.class));
    }

    @Test
    public void testReadVariableStringNested() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar.level1.level2";
        final Object testvalue = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, testvalue));
        Assert.assertEquals(testvalue,
                data.readVariable(testvar, Integer.class));
        Assert.assertNotNull(data.readVariable("testvar.level1", Object.class));
        Assert.assertNotNull(data.readVariable("testvar", Object.class));
    }

    @Test(expected = SemanticError.class)
    public void testReadVariableStringUndefined() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        data.readVariable(testvar, Object.class);
    }

    @Test
    public void testReadVariableStringArray() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        final String script = "var testvar = new Array(3);testvar[0] = 42;"
                + "testvar[1] = 44;testvar[2] = 93;";
        data.evaluateExpression(script, Object.class);
        final Integer[] values = data.readVariable(testvar, Integer[].class);
        Assert.assertEquals(3, values.length);
        Assert.assertEquals(new Integer(42), values[0]);
        Assert.assertEquals(new Integer(44), values[1]);
        Assert.assertEquals(new Integer(93), values[2]);
    }

    @Test
    public void testReadVariableStringScope() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar1 = "testvar1";
        final Object value1 = new Integer(42);
        final String testvar2 = "testvar2";
        final Object value2 = "this is a test";
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar1, value1, Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar2, value2, Scope.APPLICATION));
        Assert.assertEquals(value1,
                data.readVariable(testvar1, Scope.SESSION, Integer.class));
        Assert.assertEquals(value1,
                data.readVariable(testvar1, Scope.APPLICATION, Integer.class));
        Assert.assertEquals(value2,
                data.readVariable(testvar2, Scope.APPLICATION, String.class));
    }

    @Test
    public void testReadVariableStringScopeWrongScope() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar1";
        final Object value = new Integer(42);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, value, Scope.APPLICATION));
        Assert.assertEquals(value,
                data.readVariable(testvar, Scope.APPLICATION, Integer.class));
        Assert.assertFalse(data.existsVariable(testvar, Scope.SESSION));
    }

    @Test
    public void testEvaluateExpressionString() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final Integer value = data.evaluateExpression("3 + 4;", Integer.class);
        Assert.assertEquals(new Integer(7), value);
    }

    @Test
    public void testEvaluateExpressionWithVariable() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable("testvar", 7));
        final Integer value = data.evaluateExpression("4 + testvar",
                Integer.class);
        Assert.assertEquals(new Integer(11), value);
    }

    @Test
    public void testEvaluateExpressionStringObject() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        final String testvar = "testvar";
        final TestObject value = new TestObject(42, 43);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, value));
        Assert.assertEquals(new Integer(42),
                data.evaluateExpression("testvar.value1", Integer.class));
        Assert.assertEquals(new Integer(43),
                data.evaluateExpression("testvar.value2", Integer.class));
        data.evaluateExpression("testvar.value2 = 44", Integer.class);
        Assert.assertEquals(new Integer(44),
                data.evaluateExpression("testvar.value2", Integer.class));
    }

    @Test
    public void testEvaluateExpressionStringScope() throws SemanticError {
        final DataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.SESSION));
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createScope(Scope.APPLICATION));
        final String testvar = "testvar";
        final TestObject value = new TestObject(42, 43);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable(testvar, value, Scope.SESSION));
        Assert.assertEquals(new Integer(42),
                data.evaluateExpression("testvar.value1", Integer.class));
        Assert.assertEquals(new Integer(43),
                data.evaluateExpression("testvar.value2", Integer.class));
        data.evaluateExpression("testvar.value2 = 44", Integer.class);
        Assert.assertEquals(new Integer(44),
                data.evaluateExpression("testvar.value2", Integer.class));
        data.deleteScope(Scope.APPLICATION);
        Assert.assertEquals(new Integer(42),
                data.evaluateExpression("testvar.value1", Integer.class));
        Assert.assertEquals(new Integer(44),
                data.evaluateExpression("testvar.value2", Integer.class));
    }

    @Test
    public void testCopyValuesDatamodel() throws SemanticError {
        final EcmaScriptDataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        final String testvarlevel2 = "testvar.level1.level2";
        Assert.assertEquals(0, data.createVariable(testvarlevel2));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1.level2"));
        final EcmaScriptDataModel targetModel = new EcmaScriptDataModel();
        data.copyValues(targetModel);
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                targetModel.createVariable("testvar"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                targetModel.createVariable("testvar.level1"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                targetModel.createVariable("testvar.level1.level2"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                targetModel.createVariable("session.testvar.level1.level2"));
    }

    @Test
    public void testCopyValuesDatamodelAsync()
            throws SemanticError, InterruptedException {
        final EcmaScriptDataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        final String testvarlevel2 = "testvar.level1.level2";
        Assert.assertEquals(0, data.createVariable(testvarlevel2));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1"));
        Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                data.createVariable("testvar.level1.level2"));
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final EcmaScriptDataModel targetModel = new EcmaScriptDataModel();
                try {
                    data.copyValues(targetModel);
                } catch (SemanticError e) {
                    throwable = e;
                    e.printStackTrace();
                }
                Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                        targetModel.createVariable("testvar"));
                Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                        targetModel.createVariable("testvar.level1"));
                Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                        targetModel.createVariable("testvar.level1.level2"));
                Assert.assertEquals(DataModel.ERROR_VARIABLE_ALREADY_DEFINED,
                        targetModel.createVariable(
                                "session.testvar.level1.level2"));
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
        Thread.sleep(2000);
        thread.join();
        Assert.assertNull(throwable);
    }

    @Test
    public void testSetXmlValue() throws SemanticError,
            ParserConfigurationException, SAXException, IOException {
        final EcmaScriptDataModel data = new EcmaScriptDataModel();
        Assert.assertEquals(0, data.createScope(Scope.SESSION));
        final InputStream in = EcmaScriptDataModelTest.class
                .getResourceAsStream("/test.xml");
        final InputSource source = new InputSource(in);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document document = builder.parse(source);
        Assert.assertEquals(DataModel.NO_ERROR,
                data.createVariable("xml", document));
        final String price = data.evaluateExpression(
                "xml.documentElement.getElementsByTagNameNS("
                        + "\"http://www.example.org\", \"last\").item(0).firstChild."
                        + "data",
                String.class);
        Assert.assertEquals("30.00", price);
    }

    public class TestObject {
        private final int value1;

        private int value2;

        public TestObject(final int val1, final int val2) {
            value1 = val1;
            value2 = val2;
        }

        public Integer getValue1() {
            return value1;
        }

        public int getValue2() {
            return value2;
        }

        public void setValue2(int val) {
            value2 = val;
        }

        public int get(int index) {
            return index + 1;
        }
    }

    /**
     * Retrieves the contents of the specified resource as a string.
     * 
     * @param resource
     *            path of the resource
     * @return contents of the resource
     * @throws IOException
     *             if the resource could not be found
     */
    final String getResourceAsString(final String resource) throws IOException {
        final InputStream in = EcmaScriptDataModelTest.class
                .getResourceAsStream(resource);
        if (in == null) {
            throw new IOException("Resource '" + resource + "' not found");
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] readBuffer = new byte[1024];
        int num;
        do {
            num = in.read(readBuffer);
            if (num >= 0) {
                out.write(readBuffer, 0, num);
            }
        } while (num >= 0);
        return out.toString();
    }

    /**
     * Tests a conversion from JSON.
     * @throws IOException test failed
     * @since 0.7.9
     */
    @Test
    public void testFromJSON() throws IOException {
        final String json = getResourceAsString("/address.json");
        final EcmaScriptDataModel model = new EcmaScriptDataModel();
        model.createScope();
        final ScriptableObject object = model.fromJSON(json);
        Assert.assertNotNull(object);
        Assert.assertEquals("man", object.get("gender"));
        Assert.assertEquals(24, object.get("age"));
    }
}
