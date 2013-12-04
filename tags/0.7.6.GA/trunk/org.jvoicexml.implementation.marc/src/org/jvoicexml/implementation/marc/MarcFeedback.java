/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * Feedback channel from Marc.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 *
 */
class MarcFeedback extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(MarcFeedback.class);

    /** BUffer size when sending messages to AMRC. */
    private static final int BUFFER_SIZE = 1024;

    /** The feedback port from MARC. */
    private final int port;

    /** Output to MARC. */
    private final MarcSynthesizedOutput output;

    /**
     * Constructs a new object.
     * @param marcOutput output to MARC
     * @param portNumber the feedback port number from MARC.
     */
    public MarcFeedback(final MarcSynthesizedOutput marcOutput,
            final int portNumber) {
        setDaemon(true);
        output = marcOutput;
        port = portNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
            LOGGER.info("receiving feedback from MARC at port " + port);
            final byte[] buffer = new byte[BUFFER_SIZE];
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (true) {
                final DatagramPacket packet =
                        new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                out.write(buffer, 0, packet.getLength());
                final String response = out.toString();
                LOGGER.info("received from MARC: '" + response + "'");
                try {
                    final String id = parseId(response);
                    if (isEndOfSpeech(id)) {
                        output.playEnded(id);
                    }
                } catch (TransformerException e) {
                    LOGGER.warn("error parsing the response from MARC", e);
                } finally {
                    out.reset();
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    /**
     * Parses the event id from the response.
     * @param response the received response.
     * @return the parsed event id.
     * @throws TransformerException error parsing the id
     */
    private String parseId(final String response)
            throws TransformerException {
        final TransformerFactory transformerFactory =
                TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final StringReader reader = new StringReader(response);
        final Source source = new StreamSource(reader);
        final ResponseExtractor extractor = new ResponseExtractor();
        final Result result = new SAXResult(extractor);
        transformer.transform(source, result);
        return extractor.getEventId();
    }

    /**
     * Checks if the given id denotes the end of a speech command.
     * @param id the id
     * @return <code>true</code> if the id denotes the end of a speech command.
     * @since 0.7.5
     */
    private boolean isEndOfSpeech(final String id) {
        return id.equals("SpeechCommand:end");
    }
}
