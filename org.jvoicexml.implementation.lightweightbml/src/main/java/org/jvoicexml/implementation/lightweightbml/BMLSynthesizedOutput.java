/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.lightweightbml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.implementation.OutputEndedEvent;
import org.jvoicexml.event.plain.implementation.QueueEmptyEvent;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Demo implementation for a synthesized output for
 * LightweightBML & TalkingHead.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @since 0.7.7
 */
public final class BMLSynthesizedOutput
    implements SynthesizedOutput, BMLClient {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(BMLSynthesizedOutput.class);

    /** BML Namespace URI. */
    private static final String BML_NAMESPACE_URI =
        "http://www.mindmakers.org/projects/bml-1-0/wiki";

    /** The encoding to use for the BML. */
    private static final String ENCODING = "UTF-8";

    /** Type of the created resources. */
    private String type;

    /** Connection to Avatar. */
    private DatagramSocket socket;

    /** Avatar host. */
    private InetAddress host;

    /** Avatar port number. */
    private int port;

    /** Port number for feedback from Avatar. */
    private int feedbackPort;

    /** Feedback channel from Avatar. */
    private BMLFeedback feedback;

    /** Known synthesized output listeners. */
    private final Collection<SynthesizedOutputListener> listeners;

    /** The queued speakables. */
    private final SpeakableQueue speakables;

    /** the current session id. */
    private SessionIdentifier sessionId;
    
    /** An external BML publisher. */
    private ExternalBMLPublisher external;

//    /** The voice to use for Avatar. */
//    private String voice;
//
//    /** The default locale for text to be synthesized. */
//    private String defaultLocale;
//
//    /**
//     * Time, when the last gesture ends.
//     */
//    private int lastGestureEndTime = 0;
    
    /**
     * Constructs a new object.
     */
    public BMLSynthesizedOutput() {
        listeners = new java.util.ArrayList<SynthesizedOutputListener>();
        speakables = new SpeakableQueue();
    }

    /**
     * Sets the external publisher.
     * @param publisher
     *        the external publisher
     */
    public void setExternalBMLPublisher(
            final ExternalBMLPublisher publisher) {
        external = publisher;

        // Set a reference to this object in the publisher.
        if (external != null) {
            external.setBMLClient(this);
        }
    }

    /**
     * Retrieves the external publisher.
     * @return the external publisher
     */
    public ExternalBMLPublisher getExternalBMLPublisher() {
        return external;
    }
    /**
     * Sets the host name of Avatar.
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
     * Sets the port number of Avatar.
     *
     * @param portNumber
     *            the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Sets the feedback port number of Avatar.
     *
     * @param portNumber
     *            the port to set
     */
    public void setFeedbackPort(final int portNumber) {
        feedbackPort = portNumber;
    }

//    /**
//     * Sets the name of the voice to use.
//     * @param name name of the voice
//     * @since 0.7.6
//     */
//    public void setVoice(final String name) {
//        voice = name;
//    }
//
//    /**
//     * Sets the default locale.
//     * @param locale the default locale
//     * @since 0.7.6
//     */
//    public void setDefaultLocale(final String locale) {
//        defaultLocale = locale;
//    }

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
        try {
            connect();
        } catch (IOException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        if (external != null) {
            try {
                LOGGER.info("starting external BML publisher");
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
        LOGGER.info("BML synthesized output activated");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passivate() throws NoresourceError {
        speakables.clear();
        LOGGER.info("BML synthesized output passivated");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (external != null) {
            try {
                LOGGER.info("stopping external BML publisher");
                external.stop();
            } catch (IOException e) {
                LOGGER.warn("error closing the external listener", e);
            }
        }
        try {
            disconnect();
        } catch (IOException e) {
            LOGGER.warn("error disconnecting from Avatar", e);
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
     * Connect to Avatar.
     * @throws IOException
     *         if the connection to Avatar could not be established
     */
    private void connect() throws IOException {
        // Do nothing if we are already connectd.
        if (socket != null) {
            return;
        }

        // Connect to Avatar
        if (host == null) {
            host = InetAddress.getLocalHost();
        }
        socket = new DatagramSocket();
        socket.connect(host, port);
        LOGGER.info("connected to Avatar at '" + host + ":" + port);
        feedback = new BMLFeedback(this, feedbackPort);
        feedback.start();
    }

    /**
     * Disconnects from Avatar.
     * @throws IOException
     *         if the connection to Avatar could not be disconnected.
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
        LOGGER.info("diconnected from Avatar");
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
                LOGGER.warn("error diconnecting from Avatar", ex);
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
    public void cancelOutput(final BargeInType bargeInType) throws NoresourceError {
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
    public void queueSpeakable(final SpeakableText speakable,
            final SessionIdentifier id, final DocumentServer documentServer)
        throws NoresourceError,
            BadFetchError {
        synchronized (speakables) {
            sessionId = id;
            speakables.offer(speakable);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("queued speakable '" + speakable + "'");
            }
            if (speakables.size() == 1) {
                sendNextSpeakable();
            }
        }
    }

    /**
     * Sends the next speakable to Avatar.
     */
    private void sendNextSpeakable() {
        final QueuedSpeakable next = speakables.peek();
        final SpeakableText speakable = next.getSpeakable();
        final String utterance;
        final SsmlDocument document;
        if (speakable instanceof SpeakableSsmlText) {
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
            sendToAvatar(bml);
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
     */
    @Override
    public void sendToAvatar(final String bml) throws IOException {
        synchronized (socket) {
            final byte[] buffer = bml.getBytes("UTF-8");
            final DatagramPacket packet = new DatagramPacket(buffer,
                    buffer.length, host, port);
            socket.send(packet);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sent utterance '" + bml + "' to '"
                        + host + ":" + port);
            }
        }
    }

    /**
     * Creates the
     * <a href="http://wiki.mindmakers.org/projects:BML:main">Behavior Markup Language</a>
     * string for Avatar.
     * @param utterance the utterance to speak <code>null</code>
     * @param ssml SSML with BML annotations
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
        writer.writeAttribute("id", "bml1");
        writer.writeNamespace("ns1", BML_NAMESPACE_URI);
        if (ssml != null) {
            final Speak speak = ssml.getSpeak();
            final NodeList children = speak.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                final String namespace = child.getNamespaceURI();
                if (namespace != null) {
                    writeBMLNode(writer, child, utterance);
                }
            }
        }
        
        writer.writeEndElement();
        writer.writeEndDocument();
        writer.flush();
        //lastGestureEndTime = 0;
        writer.close();
        try {
            String output = out.toString(ENCODING);
            return output;
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn(e.getMessage(), e);
            return out.toString();
        }
    }
    
    /**
     * Writes the contents of the given node into the <code>writer</code>.
     * @param writer the writer
     * @param node the current node
     * @param utterance ...
     * @throws XMLStreamException
     *         error writing to the stream
     */
    private void writeBMLNode(final XMLStreamWriter writer,
                              final Node node,
                              final String utterance)
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
            writeBMLNode(writer, child, utterance);
        }
        
        // add text-tag for bml-speech
        if (tag.contains("speech")) {
            writer.writeStartElement("text");
            writer.writeCharacters(utterance);
            writer.writeEndElement();
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
        
        synchronized (speakables) {
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
                LOGGER.info("BML queue is empty");
                final SynthesizedOutputEvent emptyEvent = new QueueEmptyEvent(
                        this, sessionId);
                synchronized (listeners) {
                    for (SynthesizedOutputListener listener : listeners) {
                        listener.outputStatusChanged(emptyEvent);
                    }
                }
            } else {
                sendNextSpeakable();
            }
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
