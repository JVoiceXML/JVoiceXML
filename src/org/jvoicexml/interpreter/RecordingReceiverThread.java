/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;

/**
 * Asynchronous recording from the input device.
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */

final class RecordingReceiverThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RecordingReceiverThread.class);

    /** The input to record from. */
    private final UserInput input;

    /** The document server to store the recorded audio. */
    private final DocumentServer server;

    /** The event handler to propagate the end of the recording. */
    private final EventHandler handler;

    /** Maximal recording time. */
    private final long maxTime;

    /**
     * Creates a new object.
     * @param userInput the input to record from.
     * @param documentServer the document server to store the recorded audio.
     * @param eventHandler the event handler to propagate the end of the
     *          recording.
     * @param recordingTime maximal recording time.
     */
    public RecordingReceiverThread(final UserInput userInput,
            final DocumentServer documentServer,
            final EventHandler eventHandler, final long recordingTime) {
        input = userInput;
        server = documentServer;
        handler = eventHandler;
        maxTime = recordingTime;
        setDaemon(true);
        setName("RecordingReceiverThread");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
        final URI clientURI;
        try {
            final String recordURIServer = "rtp://localhost:44000/audio";
            final String recordURIClient =
                recordURIServer + "?participant=localhost:44002";
            clientURI = new URI(recordURIClient);
        } catch (URISyntaxException e) {
            final JVoiceXMLEvent event = new SemanticError(e.getMessage());
            handler.notifyEvent(event);
            return;
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final long startTime = System.currentTimeMillis();
        URL url;
        try {
            url = clientURI.toURL();
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream urlInStream =
                connection.getInputStream();
            int br;
            byte[] buffer = new byte[1024];
            while ((br = urlInStream.read(buffer)) != -1) {
                baos.write(buffer, 0, br);

                //Validate the recording time
                if ((System.currentTimeMillis() - startTime)
                        > maxTime) {
                    // TODO Create a handler to notify the end of the recording.
                    break;
                }
            }
            baos.close();
            urlInStream.close();
        } catch (MalformedURLException e) {
            final JVoiceXMLEvent event = new SemanticError(e.getMessage());
            handler.notifyEvent(event);
            return;
        } catch (IOException e) {
            final JVoiceXMLEvent event = new NoresourceError(e.getMessage());
            handler.notifyEvent(event);
        }
    }
}
