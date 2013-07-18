/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.umundo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.mmi.DecoratedMMIEvent;
import org.jvoicexml.callmanager.mmi.MMIEventListener;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.LifeCycleEvent;
import org.jvoicexml.mmi.events.LifeCycleRequest;
import org.jvoicexml.mmi.events.NewContextRequest;
import org.jvoicexml.mmi.events.PrepareRequest;
import org.jvoicexml.mmi.events.StartRequest;
import org.jvoicexml.mmi.events.protobuf.LifeCycleEvents;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Receiver for MMI events over umundo.
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.7.6
 */
public final class MmiReceiver implements ITypedReceiver {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(MmiReceiver.class);

    /** Registered listeners for MMI events. */
    private final Collection<MMIEventListener> listeners;

    /** The used protocol adapter. */
    private final String sourceUrl;

    /** The document builder. */
    private DocumentBuilder builder;

    /** The XSL template. */
    private Templates template;

    /**
     * Constructs a new object.
     *
     * @param source
     *            the source URL of this endpoint
     * @exception IOException
     *            error creating the receiver
     */
    public MmiReceiver(final String source) throws IOException {
        listeners = new java.util.ArrayList<MMIEventListener>();
        sourceUrl = source;

        final DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Configure the factory to ignore comments
        factory.setIgnoringComments(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException(e.getMessage(), e);
        }
        TransformerFactory transFact = TransformerFactory.newInstance();
        try {
            final URL xsltURL = UmundoETLProtocolAdapter.class.getResource(
                    "VoiceXmlTemplate.xsl");
            final String xsltSystemID = xsltURL.toExternalForm();
            template = transFact.newTemplates(
                    new StreamSource(xsltSystemID));
        } catch (TransformerConfigurationException tce) {
            throw new IOException("Unable to compile stylesheet", tce);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveObject(final Object object, final Message msg) {
        // bugfix
        // Java uses the system classloader when being invoked from JNI.
        // Thereby, it forgets about all the stuff it knew before.
        final ClassLoader loader = getClass().getClassLoader();
        Thread.currentThread().setContextClassLoader(loader);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("received '" + object + "'");
        }
        final LifeCycleEvent event;
        try {
            event = convertToLifeCycleEvent(object);
        } catch (SAXException | IOException | TransformerException
                | ParserConfigurationException e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }
        if (event == null) {
            return;
        }

        // Notify all listeners about the event
        final DecoratedMMIEvent docatedEvent = new DecoratedMMIEvent(sourceUrl,
                event);
        synchronized (listeners) {
            for (MMIEventListener listener : listeners) {
                listener.receivedEvent(docatedEvent);
            }
        }
    }

    /**
     * Converts the received object into a {@link LifeCycleEvent} that can be
     * handled by the {@link org.jvoicexml.callmananager.mmi.MMICallManager}.
     * @param object the received object
     * @return the converted lifecycle event, <code>null</code> if the
     * object could not be converted or is not addressed to this modality
     * component
     * @throws ParserConfigurationException 
     * @throws TransformerException 
     * @throws IOException 
     * @throws SAXException 
     */
    private LifeCycleEvent convertToLifeCycleEvent(
            final Object object) throws SAXException, IOException,
                TransformerException, ParserConfigurationException {
        if (!(object instanceof LifeCycleEvents.LifeCycleEvent)) {
            return null;
        }
        final LifeCycleEvents.LifeCycleEvent receivedEvent =
                (LifeCycleEvents.LifeCycleEvent) object;
        final String target = receivedEvent.getTarget();
        if (!target.equals(sourceUrl)) {
            return null;
        }
        final LifeCycleEvent event;
        final LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
            type = receivedEvent.getType();
        if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .PREPARE_REQUEST)) {
            final PrepareRequest request = new PrepareRequest();
            final LifeCycleEvents.LifeCycleRequest decodedLifeCycleRequest =
                    extractContext(receivedEvent, request);
            final LifeCycleEvents.PrepareRequest decodedPrepareRequest =
                    decodedLifeCycleRequest.getExtension(
                            LifeCycleEvents.PrepareRequest.request);
            request.setContentURL(decodedPrepareRequest.getContentURL());
            final String content = decodedPrepareRequest.getContent();
            if ((content != null) && !content.isEmpty()) {
                final AnyComplexType any = new AnyComplexType();
                final Object convertedContent = convertContent(content);
                any.getContent().add(convertedContent);
                request.setContent(any);
            }
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .NEW_CONTEXT_REQUEST)) {
            final NewContextRequest request = new NewContextRequest();
                    extractContext(receivedEvent, request);
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                    .START_REQUEST)) {
            final StartRequest request = new StartRequest();
            final LifeCycleEvents.LifeCycleRequest decodedLifeCycleRequest =
                    extractContext(receivedEvent, request);
            final LifeCycleEvents.StartRequest decodedStartRequest =
                    decodedLifeCycleRequest.getExtension(
                            LifeCycleEvents.StartRequest.request);
            request.setContentURL(decodedStartRequest.getContentURL());
            final String content = decodedStartRequest.getContent();
            if ((content != null) && !content.isEmpty()) {
                final AnyComplexType any = new AnyComplexType();
                final Object convertedContent = convertContent(content);
                any.getContent().add(convertedContent);
                request.setContent(any);
            }
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .CANCEL_REQUEST)) {
            final NewContextRequest request = new NewContextRequest();
            extractContext(receivedEvent, request);
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .PAUSE_REQUEST)) {
            final NewContextRequest request = new NewContextRequest();
            extractContext(receivedEvent, request);
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .RESUME_REQUEST)) {
            final NewContextRequest request = new NewContextRequest();
            extractContext(receivedEvent, request);
            event = request;
        } else if (type.equals(LifeCycleEvents.LifeCycleEvent.LifeCycleEventType
                .CLEAR_CONTEXT_REQUEST)) {
            final NewContextRequest request = new NewContextRequest();
            extractContext(receivedEvent, request);
            event = request;
        } else {
            event = null;
        }
        event.setTarget(receivedEvent.getTarget());
        event.setRequestId(receivedEvent.getRequestID());
        event.setSource(receivedEvent.getSource());
        return event;
    }

    /**
     * Extract a context identifier from the received message into the
     * current request.
     * @param receivedEvent the received event
     * @param request the current request.
     * @return decoded lifecycle request
     */
    private LifeCycleEvents.LifeCycleRequest extractContext(
            final LifeCycleEvents.LifeCycleEvent receivedEvent,
            final LifeCycleRequest request) {
        final LifeCycleEvents.LifeCycleRequest decodedLifeCycleRequest =
                receivedEvent.getExtension(LifeCycleEvents
                        .LifeCycleRequest.request);
        request.setContext(decodedLifeCycleRequest.getContext());
        return decodedLifeCycleRequest;
    }

    /**
     * Creates a VoiceXML snippet that can be pasted into the content.
     * @param xml the XML snippet
     * @return create VoiceXML document
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    private Object convertContent(final String xml)
            throws SAXException, IOException, TransformerException,
                ParserConfigurationException {
        if (xml.startsWith("<")) {
            final Reader reader = new StringReader(xml);
            final InputSource source = new InputSource(reader);
            Document document = builder.parse(source);
            Transformer transformer = template.newTransformer();
            final Source domSource = new DOMSource(document);
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Result result = new StreamResult(out); 
            transformer.transform(domSource, result);
            final InputStream in = new ByteArrayInputStream(out.toByteArray());
            final InputSource transformedSource = new InputSource(in);
            final VoiceXmlDocument doc =
                    new VoiceXmlDocument(transformedSource);
            final Vxml vxml = doc.getVxml();
            final List<Form> forms = vxml.getForms();
            final Form form = forms.get(0);
            final Collection<Block> blocks = form.getChildNodes(Block.class);
            final Block block = blocks.iterator().next();
            return block.getFirstChild();
        } else {
            return xml;
        }
    }

    /**
     * Adds the given listener to the list of known event listeners.
     * @param listener the listener to add
     */
    public void addMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the given listener from the list of known event listeners.
     * @param listener the listener to remove
     */
    public void removeMMIEventListener(final MMIEventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
