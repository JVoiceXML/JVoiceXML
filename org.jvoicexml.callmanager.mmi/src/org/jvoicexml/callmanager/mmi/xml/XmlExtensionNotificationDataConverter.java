/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.callmanager.mmi.xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.LastResult;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.callmanager.mmi.ConversionException;
import org.jvoicexml.callmanager.mmi.ExtensionNotificationDataConverter;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.OutputStartedEvent;
import org.jvoicexml.event.plain.implementation.RecognitionEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Converts the extension notifications into the XML format. Standards like <a
 * href="http://www.w3.org/TR/emma/">EMMA</a> are used where possible.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public class XmlExtensionNotificationDataConverter
        implements ExtensionNotificationDataConverter {
    public static final String JVXML_MMI_NAMESPACE = "http://www.nowhere.org/jvxmlmmi";
    /** The EMMA namespace. */
    private static final String EMMA_NAMESPACE = "http://www.w3.org/2003/04/emma";

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertApplicationLastResult(
            final List<LastResult> lastresults) throws ConversionException {
        if (lastresults == null || lastresults.isEmpty()) {
            return null;
        }
        final LastResult lastresult = lastresults.get(0);
        final String utterance = lastresult.getUtterance();
        final String mode = lastresult.getInputmode();
        final float confidence = lastresult.getConfidence();
        final Object interpretation = lastresult.getInterpretation();
        try {
            return createEmma("lastresult", utterance, mode, confidence,
                    interpretation);
        } catch (ParserConfigurationException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

    /**
     * Creates a semantic interpretation from the given recognition result
     * 
     * @param id
     *            the emma id
     * @param utterance
     *            the recognized utterance
     * @param mode
     *            the input mode
     * @param confidence
     *            the confidence value
     * @param semanticInterpretation
     *            the semantic interpretation
     * @return emma document
     * @throws ParserConfigurationException
     *             error creating an EMMA document
     */
    private Element createEmma(final String id, final String utterance,
            final String mode, final float confidence,
            final Object semanticInterpretation)
            throws ParserConfigurationException {
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
        interpretation.setAttribute("id", id);
        interpretation
                .setAttributeNS(EMMA_NAMESPACE, "emma:medium", "acoustic");
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:mode", mode);
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:confidence",
                Float.toString(confidence));
        interpretation.setAttributeNS(EMMA_NAMESPACE, "emma:tokens", utterance);
        addSemanticInterpretation(document, interpretation,
                semanticInterpretation);
        emma.appendChild(interpretation);
        return emma;
    }

    /**
     * Possibly add semantic interpretation to the given document
     * 
     * @param document
     *            the document
     * @param parent
     *            the parent node
     * @param object
     *            the semantic interpretation to add
     */
    private void addSemanticInterpretation(final Document document,
            final Element parent, final Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof ScriptableObject) {
            final ScriptableObject scriptable = (ScriptableObject) object;
            addSemanticInterpretation(document, parent, scriptable);
        } else {
            final Element literal = document.createElementNS(EMMA_NAMESPACE,
                    "emma:literal");
            final Text text = document.createTextNode(object.toString());
            literal.appendChild(text);
            parent.appendChild(literal);
        }
    }

    /**
     * Possibly add semantic interpretation as a compound object to the given
     * document
     * 
     * @param document
     *            the document
     * @param parent
     *            the parent node
     * @param object
     *            the semantic interpretation to add
     */
    private void addSemanticInterpretation(final Document document,
            final Element parent, final ScriptableObject object) {
        if (object == null) {
            return;
        }

        // Recursively retrieve the properties of the ECMAScript object and
        // add them as nested tags
        final Object[] ids = ScriptableObject.getPropertyIds(object);
        for (Object id : ids) {
            final String key = id.toString();
            Object value = object.get(key, object);
            if (value instanceof ScriptableObject) {
                final Element element = document.createElement(key);
                parent.appendChild(element);
                final ScriptableObject scriptable = (ScriptableObject) value;
                addSemanticInterpretation(document, element, scriptable);
            } else {
                final Element element = document.createElement(key);
                final Text text = document.createTextNode(value.toString());
                element.appendChild(text);
                parent.appendChild(element);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertSynthesizedOutputEvent(SynthesizedOutputEvent output)
            throws ConversionException {

        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        factory.setNamespaceAware(true);
        try {
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.newDocument();
            final Element data = document.createElementNS(JVXML_MMI_NAMESPACE,
                    "jvxmlmmi:data");
            document.appendChild(data);
            final SpeakableText speakable = getSpeakable(output);
            if (speakable != null) {
                final Document ssml = toDocument(speakable);
                final Node speak = ssml.getFirstChild();
                if (speak != null) {
                    document.adoptNode(speak);
                    data.appendChild(speak);
                }
            }
            return data;
        } catch (ParserConfigurationException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ConversionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves a {@link SpeakableText} from the received event, if present.
     * 
     * @param output
     *            the received event
     * @return the speakable if the events knows about it, {@code null} else
     */
    private SpeakableText getSpeakable(final SynthesizedOutputEvent output) {
        if (output instanceof OutputStartedEvent) {
            final OutputStartedEvent started = (OutputStartedEvent) output;
            return started.getSpeakable();
        } else if (output instanceof OutputEndedEvent) {
            final OutputEndedEvent ended = (OutputEndedEvent) output;
            return ended.getSpeakable();
        }
        return null;
    }

    /**
     * Converts the speakable to an XML document that can be included into the
     * data attribute.
     * 
     * @param speakable
     *            the received speakable
     * @return the converted document
     * @throws ParserConfigurationException
     *             error parsing
     * @throws SAXException
     *             error parsing
     * @throws IOException
     *             error parsing
     */
    private Document toDocument(final SpeakableText speakable)
            throws ParserConfigurationException, SAXException, IOException {
        final String text = speakable.getSpeakableText();
        final StringReader reader = new StringReader(text);
        final InputSource source = new InputSource(reader);
        final DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertSpokenInputEvent(final SpokenInputEvent input)
            throws ConversionException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object convertRecognitionEvent(final DataModel model,
            final RecognitionEvent event) throws ConversionException {
        final RecognitionResult result = event.getRecognitionResult();
        final String utterance = result.getUtterance();
        final String mode = result.getMode().getMode();
        final float confidence = result.getConfidence();
        try {
            final Object interpretation = result.getSemanticInterpretation(model);
            return createEmma("recognition", utterance, mode, confidence,
                    interpretation);
        } catch (ParserConfigurationException | SemanticError e) {
            throw new ConversionException(e.getMessage(), e);
        }
    }
}
