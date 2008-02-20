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

package org.jvoicexml.implementation.jsapi10.jvxml;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.client.rtp.RtpConfiguration;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControlListener;
import org.jvoicexml.implementation.ObservableCallControl;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.SynthesizedOutputProvider;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.jsapi10.StreamableSpokenOutput;

/**
 * RTP frontend for the JSAPI 1.0 layer.
 *
 * <p>
 * This class does not offer any telephony support but streaming
 * capablities with RTP technology for other clients, e.g. a console.
 * </p>
 *
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

public final class RtpTelephony implements Telephony, ObservableCallControl {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(RtpTelephony.class);

    /** delay in msec before ending a play. */
    private static final int DELAY = 2000;

    /** RTP server. */
    private final RtpServer server;

    /** Registered call control listeners. */
    private final Collection<CallControlListener> listener;

    /** Set to <code>true</code> if this device is playing back. */
    private boolean playing;

    /**
     * Constructs a new object.
     */
    public RtpTelephony() {
        server = new RtpServer();
        listener = new java.util.ArrayList<CallControlListener>();
    }
    /**
     * {@inheritDoc}
     */
    public synchronized void play(final SystemOutput output,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        final SynthesizedOutput synthesizedOutput;
        if (output instanceof SynthesizedOutputProvider) {
            SynthesizedOutputProvider provider =
                (SynthesizedOutputProvider) output;
            synthesizedOutput = provider.getSynthesizedOutput();
        } else {
            synthesizedOutput = null;
        }
        if (!(synthesizedOutput instanceof StreamableSpokenOutput)) {
            throw new IOException("output does not support streams!");
        }
        playing = true;
        final StreamableSpokenOutput streamable =
            (StreamableSpokenOutput) synthesizedOutput;
        final InputStream stream = streamable.getSynthesizerStream();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing output...");
        }
        firePlayStarted();
        server.sendData(stream);
        // TODO Replace this by a timing solution.
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("... done playing output");
        }
        playing = false;
        firePlayStopped();
    }

    /**
     * Notifies all listeners that play has started.
     */
    private void firePlayStarted() {
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.playStarted();
            }
        }
    }

    /**
     * Notifies all listeners that play has stopped.
     */
    private void firePlayStopped() {
        synchronized (listener) {
            final Collection<CallControlListener> copy =
                new java.util.ArrayList<CallControlListener>();
            copy.addAll(listener);
            for (CallControlListener current : copy) {
                current.playStopped();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void record(final UserInput input,
            final Map<String, String> parameters)
            throws NoresourceError, IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        server.close();
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jsapi10-rtp";
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        return !playing;
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        close();
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        RtpConfiguration rtpClient = (RtpConfiguration) client;
        server.open();
        final InetAddress address = rtpClient.getAddress();
        final int port = rtpClient.getPort();
        final int controlPort = rtpClient.getControlPort();
        server.addTarget(address, port, controlPort);


    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final RemoteClient client) {
        server.close();
    }

    /**
     * {@inheritDoc}
     */
    public void addCallControlListener(final CallControlListener callListener) {
        synchronized (listener) {
            listener.add(callListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeCallControlListener(
            final CallControlListener callListener) {
            synchronized (listener) {
                listener.remove(callListener);
            }
    }

}
