/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
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

import java.util.Map;
import java.util.Stack;

import org.jvoicexml.callmanager.mmi.MMIRecognitionResult;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Content handler to parse any EMMA formatted semantic interpretation. 
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public class EmmaSemanticInterpretationExtractor implements ContentHandler {
    /** The EMMA namespace. */
    private static final String EMMA_NAMESPACE = "http://www.w3.org/2003/04/emma";

    private boolean inEmma;

    private float confidence;
    private String utterance;
    private Object interpretation;
    private final Map<String, Object> interpretations;
    private final Stack<String> interpretationTagNames;
    private StringBuilder text;
    private Map<String, Object> currentAssignments;
    private final Context context;
    private final ScriptableObject scope;

    /**
     * Constructs a new object.
     */
    public EmmaSemanticInterpretationExtractor() {
        interpretations = new java.util.HashMap<String, Object>();
        interpretationTagNames = new java.util.Stack<String>();
        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        scope = context.initStandardObjects();
    }

    /**
     * Retrieves the parsed recognition result.
     * 
     * @return the parsed recognition result.
     */
    public MMIRecognitionResult getRecognitonResult() {
        return new MMIRecognitionResult(utterance, interpretation, confidence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDocumentLocator(Locator locator) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startDocument() throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endDocument() throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (localName.equals("emma")) {
            inEmma = true;
        } else if (inEmma) {
            if (localName.equals("interpretation")) {
                utterance = atts.getValue(EMMA_NAMESPACE, "tokens");
                final String conf = atts.getValue(EMMA_NAMESPACE, "confidence");
                if (conf != null) {
                    confidence = Float.parseFloat(conf);
                }
            } else if (localName.equals("literal")) {
                text = new StringBuilder();
            } else {
                interpretationTagNames.push(localName);
                text = new StringBuilder();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (localName.equals("emma")) {
            inEmma = false;
            if (interpretation == null) {
                interpretation = evaluateInterpretations();
            }
        } else if (localName.equals("literal")) {
            final String value = getText();
            interpretation = assign("out", value);
            text = null;
        } else if (!interpretationTagNames.isEmpty()) {
            final String key = interpretationTagNames.pop();
            final String value = getText();
            addAssignment(key, value);
            text = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    private void addAssignment(final String key, final String value) {
        if (currentAssignments == null) {
            currentAssignments = new java.util.HashMap<String, Object>();
        }
        if (value == null) {
            final Object object = evaluateAssignments(key);
            if (interpretationTagNames.isEmpty()) {
                interpretations.put(key, object);
            }
        } else {
            currentAssignments.put(key, value);
        }
    }

    /**
     * Retrieves the trimmed character content of the parsed content.
     * 
     * @return trimmed parsed character content, {@code null} if there is no
     *         content
     */
    private String getText() {
        if (text == null) {
            return null;
        }
        if (text.length() == 0) {
            return null;
        }
        final String str = text.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        return str;
    }

    /**
     * {@inheritDoc}
     */
    private Object evaluateInterpretations() {
        if (currentAssignments == null) {
            return null;
        }
        final Object object = createObject("out");
        for (final String property : currentAssignments.keySet()) {
            final Object value = currentAssignments.get(property);
            assign(property, value);
            final String propertyName = "out." + property;
            assign(propertyName, value);
        }
        return object;
    }

    /**
     * Evaluate assignments of tag names to text content at one level.
     * 
     * @param variable
     *            the containing tag element
     * @return scriptable object with all nested properties
     */
    private Object evaluateAssignments(final String variable) {
        final Object object = createObject(variable);
        for (final String property : currentAssignments.keySet()) {
            final Object value = currentAssignments.get(property);
            assign(property, value);
            final String propertyName = variable + "." + property;
            assign(propertyName, value);
        }
        return object;
    }

    /**
     * Convenience method to create an empty javascript object with the given
     * name.
     * 
     * @param variable
     *            name of the object
     * @return created javascript object.
     */
    private Object createObject(final String variable) {
        String expr = "var " + variable + " = new Object();";
        context.evaluateString(scope, expr, "expr", 1, null);
        return scope.get(variable, scope);
    }

    /**
     * Assigns the given value to the given javascript variable.
     * @param variable name of the variable
     * @param value value of the variable
     * @return created javascript object
     */
    private Object assign(final String variable, final Object value) {
        String expr = "";
        if (variable.indexOf('.') < 0) {
            expr = "var ";
        }
        expr += variable + " = ";
        if (value instanceof String) {
            expr += "\"" + value + "\";";
        } else {
            expr += value + ";";
        }
        context.evaluateString(scope, expr, "expr", 1, null);
        return scope.get(variable, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (text != null) {
            text.append(ch, start, length);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void skippedEntity(String name) throws SAXException {
    }

}
