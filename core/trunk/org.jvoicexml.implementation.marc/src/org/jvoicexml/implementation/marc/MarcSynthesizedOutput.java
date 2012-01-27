/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.marc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.OutputEndedEvent;
import org.jvoicexml.implementation.QueueEmptyEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Demo implementation for a synthesized output for MARC.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class MarcSynthesizedOutput
    implements SynthesizedOutput, MarcClient {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarcSynthesizedOutput.class);

    /** MARC Namespace URI. */
    private static final String MARC_NAMESPACE_URI = "http://marc.limsi.fr/";

    /** The encoding to use for the BML. */
    private static final String ENCODING = "UTF-8";

    /** Type of the created resources. */
    private String type;

    /** Connection to MARC. */
    private DatagramSocket socket;

    /** MARC host. */
    private InetAddress host;

    /** MARC port number. */
    private int port;

    /** Port number for feedback from MARC. */
    private int feedbackPort;

    /** Feedback channel from MARC. */
    private MarcFeedback feedback;

    /** Known synthesized output listeners. */
    private final Collection<SynthesizedOutputListener> listeners;

    /** The queued speakables. */
    private final SpeakableQueue speakables;

    /** the current session id. */
    private String sessionId;

    /** The current event id. */
    private int marcEventId;

    /** An external MARC publisher. */
    private ExternalMarcPublisher external;

    /**
     * Constructs a new object.
     */
    public MarcSynthesizedOutput() {
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        speakables = new SpeakableQueue();
    }

    /**
     * Sets the external publisher.
     * @param publisher
     *        the external publisher
     */
    public void setExternalMarcPublisher(
            final ExternalMarcPublisher publisher) {
        external = publisher;

        // Set a reference to this object in the publisher.
        if (external != null) {
            external.setMarcClient(this);
        }
    }

    /**
     * Retrieves the external publisher.
     * @return the external publisher
     */
    public ExternalMarcPublisher getExternalMarcPublisher() {
        return external;
    }
    /**
     * Sets the host name of MARC.
     *
     * @param value
     *            the host to set
     * @throws UnknownHostException
     *         if the host is unknown
     */
    public void setHost(final String value) throws UnknownHostException {
        if (value == null) {
            host = null;
        } else {
            host = InetAddress.getByName(value);
        }
    }

    /**
     * Sets the port number of MARC.
     *
     * @param portNumber
     *            the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Sets the feedback port number of MARC.
     *
     * @param portNumber
     *            the port to set
     */
    public void setFeedbackPort(final int portNumber) {
        feedbackPort = portNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the created resource.
     * @param typeName name of the resource
     */
    void setType(final String typeName) {
        type = typeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open() throws NoresourceError {
        if (external != null) {
            try {
                connect();
                LOGGER.info("starting external MARC publisher");
                external.start();
            } catch (IOException e) {
                throw new NoresourceError(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void activate() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
        speakables.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (external != null) {
            try {
                LOGGER.info("stopping external MARC publisher");
                external.stop();
                disconnect();
            } catch (IOException e) {
                LOGGER.warn("error closing the external listener", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBusy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("busy status: " + speakables.size()
                    + " messages to send");
        }
        return !speakables.isEmpty();
    }

    /**
     * Connect to MARC.
     * @throws IOException
     *         if the connection to MARC could not be established
     */
    private void connect() throws IOException {
        // Do nothing if we are already connectd.
        if (socket != null) {
            return;
        }

        // Connect to MARC
        if (host == null) {
            host = InetAddress.getLocalHost();
        }
        socket = new DatagramSocket();
        socket.connect(host, port);
        LOGGER.info("connected to MARC at '" + host + ":" + port);
        feedback = new MarcFeedback(this, feedbackPort);
        feedback.start();
    }

    /**
     * Disconnects from MARC.
     * @throws IOException
     *         if the connection to MARC could not be disconnected.
     */
    private void disconnect() throws IOException {
        // Do nothing if we are already disconnected
        if (socket == null) {
            return;
        }

        // disconnect.
        try {
            feedback.interrupt();
            feedback = null;
            socket.disconnect();
            socket.close();
        } finally {
            socket = null;
            feedback = null;
        }
        LOGGER.info("diconnected from MARC");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect(final ConnectionInformation client) throws IOException {
        if (external == null) {
            connect();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect(final ConnectionInformation client) {
        if (external == null) {
            try {
                disconnect();
            } catch (IOException ex) {
                LOGGER.warn("error diconnecting from MARC", ex);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsBargeIn() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelOutput() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(final SynthesizedOutputListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(final SynthesizedOutputListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getUriForNextSynthesisizedOutput() throws NoresourceError,
            URISyntaxException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void queueSpeakable(final SpeakableText speakable,
            final String id, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        sessionId = id;
        speakables.add(speakable);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("queued speakable '" + speakable + "'");
        }
        if (speakables.size() == 1) {
            sendNextSpeakable();
        }
    }

    /**
     * Sends the next speakable to MARC.
     */
    private void sendNextSpeakable() {
        final QueuedSpeakable next = speakables.peek();
        final SpeakableText speakable = next.getSpeakable();
        final String utterance;
        final SsmlDocument document;
        if (speakable instanceof SpeakablePlainText) {
            final SpeakablePlainText plain = (SpeakablePlainText) speakable;
            utterance = plain.getSpeakableText();
            document = null;
        } else if (speakable instanceof SpeakableSsmlText) {
            final SpeakableSsmlText ssml = (SpeakableSsmlText) speakable;
            document = ssml.getDocument();
            final Speak speak = document.getSpeak();
            utterance = speak.getTextContent();
        } else {
            utterance = speakable.getSpeakableText();
            document = null;
        }
        ErrorEvent error = null;
        try {
            final String bml = createBML(utterance, document);
            sendToMarc(bml);
        } catch (SocketException e) {
           error = new BadFetchError(e.getMessage(), e);
        } catch (UnknownHostException e) {
            error = new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            error = new BadFetchError(e.getMessage(), e);
        } catch (XMLStreamException e) {
            error = new BadFetchError(e.getMessage(), e);
        }
        if (error != null) {
            synchronized (listeners) {
                for (SynthesizedOutputListener listener : listeners) {
                    listener.outputError(error);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public void sendToMarc(final String bml) throws IOException {
        final byte[] buffer = bml.getBytes();
        final DatagramPacket packet = new DatagramPacket(buffer,
                buffer.length, host, port);
        socket.send(packet);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("sent utterance '" + bml + "' to '"
                    + host + ":" + port);
        }
    }

    /**
     * Creates the
     * <a href="http://wiki.mindmakers.org/projects:BML:main">Behavior Markup Language</a>
     * string for MARC.
     * @param utterance the utterance to speak <code>null</code>
     * @param ssml SSML with MARC annotations
     * @return created XML string
     * @throws XMLStreamException
     *         if the stream could not be created.
     */
    private String createBML(final String utterance, final SsmlDocument ssml)
            throws XMLStreamException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XMLOutputFactory factory = XMLOutputFactory.newInstance();
        final XMLStreamWriter writer =
                factory.createXMLStreamWriter(out, ENCODING);
        writer.writeStartDocument(ENCODING, "1.0");
        writer.writeStartElement("bml");
        writer.writeNamespace("marc", MARC_NAMESPACE_URI);
        writer.writeAttribute("id", "JVoiceXMLTrack_" + marcEventId);
        marcEventId++;
        writer.writeStartElement(MARC_NAMESPACE_URI, "agent");
        writer.writeAttribute("name", "Agent_1");
        writer.writeEndElement();
        if (ssml != null) {
            final Speak speak = ssml.getSpeak();
            final NodeList children = speak.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                final String namespace = child.getNamespaceURI();
                if (namespace != null) {
                    writeMarcNode(writer, child);
                }
            }
        }
        writer.writeStartElement(MARC_NAMESPACE_URI, "fork");
        writer.writeAttribute("id", "JVoiceXMLTrack_fork");
        writer.writeStartElement("speech");
        writer.writeAttribute("id", "SpeechCommand");
        writer.writeAttribute(MARC_NAMESPACE_URI, "synthesizer",
                "OpenMary");
        writer.writeAttribute(MARC_NAMESPACE_URI, "voice", "DEFAULT");
        writer.writeAttribute(MARC_NAMESPACE_URI, "options", "");
        writer.writeAttribute(MARC_NAMESPACE_URI, "f0_shift", "0.0");
        writer.writeAttribute("text", utterance);
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        try {
            return out.toString(ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
            return out.toString();
        }
    }
    
    /**
     * Writes the contents of the given node into the <code>writer</code>.
     * @param writer the writer
     * @param node the current node
     * @throws XMLStreamException
     *         error writing to the stream
     */
    private void writeMarcNode(final XMLStreamWriter writer, final Node node)
            throws XMLStreamException {
        if (node instanceof Text) {
            final Text text = (Text) node;
            final String content = text.getTextContent();
            writer.writeCharacters(content);
            return;
        }

        final String tag = node.getNodeName();
        writer.writeStartElement(tag);
        final NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
            for (int k = 0; k < attributes.getLength(); k++) {
                final Node attribute = attributes.item(k);
                final String name = attribute.getNodeName();
                final String value = attribute.getNodeValue();
                writer.writeAttribute(name, value);
            }
        }
        final NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            writeMarcNode(writer, child);
        }
        writer.writeEndElement();
    }


    /**
     * Notification that the playback with the given id has ended. 
     * @param id the id
     */
    void playEnded(final String id) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("speech with id '" + id + "' ended");
        }
        final QueuedSpeakable queuedSpeakable = speakables.poll();
        if (queuedSpeakable == null) {
            return;
        }
        final SpeakableText speakable = queuedSpeakable.getSpeakable();
        final SynthesizedOutputEvent event = new OutputEndedEvent(this,
                sessionId, speakable);
        synchronized (listeners) {
            for (SynthesizedOutputListener listener : listeners) {
                listener.outputStatusChanged(event);
            }
        }

        if (speakables.isEmpty()) {
            LOGGER.info("MARC queue is empty");
            final SynthesizedOutputEvent emptyEvent = new QueueEmptyEvent(this,
                    sessionId);
            synchronized (listeners) {
                for (SynthesizedOutputListener listener : listeners) {
                    listener.outputStatusChanged(emptyEvent);
                }
            }
        } else {
            sendNextSpeakable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitNonBargeInPlayed() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void waitQueueEmpty() {
        try {
            speakables.waitQueueEmpty();
        } catch (InterruptedException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            }
            e.printStackTrace();
        }
    }
}
