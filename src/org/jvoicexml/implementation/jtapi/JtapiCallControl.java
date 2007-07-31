/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/implementation/KeyedResourcePool.java $
 * Version: $LastChangedRevision: 330 $
 * Date:    $Date: 2007-06-21 09:15:10 +0200 (Do, 21 Jun 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jtapi;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.callmanager.jtapi.JVoiceXmlTerminal;
import org.jvoicexml.callmanager.jtapi.JtapiRemoteClient;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControlListener;
import org.jvoicexml.implementation.ObservableCallControl;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * JTAPI based implementation of a {@link CallControl}.
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle
 *
 * @version $Revision: 206 $
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class JtapiCallControl implements CallControl,
        ObservableCallControl {
    /** Logger instance. */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JtapiCallControl.class);

    /** Listener to this call control. */
    private final Collection<CallControlListener> callControlListeners;

    /** The JTAPI connection. */
    private JVoiceXmlTerminal terminal;

    /**
     * Constructs a new object.
     */
    public JtapiCallControl() {
        callControlListeners = new ArrayList<CallControlListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void play(final URI uri) throws NoresourceError, IOException {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("playing from URI '" + uri + "...");
        }
        firePlayEvent();
        terminal.play(uri);
    }

    /**
     * {@inheritDoc}
     */
    public void record(final URI uri) throws NoresourceError, IOException {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        fireRecordEvent(); // may be after record method!!!

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("recording to URI '" + uri + "'...");
        }
        terminal.record(uri);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() throws NoresourceError {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        terminal.stopRecord();
    }

    /**
     * Inform the {@link CallControlListener} about an answered event.
     */
    protected void fireAnswerEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.answered();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a play started event.
     */
    protected void firePlayEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStarted();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a play stopped event.
     */
    protected void fireplayStoppedEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStopped();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a record started event.
     */
    protected void fireRecordEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.recordStarted();
            }
        }
    }

    /**
     * Inform the {@link CallControlListener} about a hangup event.
     */
    protected void firehangedUpEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.hangedup();
            }
        }
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
    }

    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "jtapi";
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
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
        final JtapiRemoteClient remote = (JtapiRemoteClient) client;
        terminal = remote.getTerminal();
    }
}
