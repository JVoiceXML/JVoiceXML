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
import java.util.Map;

import javax.telephony.CallEvent;
import javax.telephony.CallObserver;
import javax.telephony.Event;
import javax.telephony.MetaEvent;
import javax.telephony.events.CallActiveEv;
import javax.telephony.events.CallEv;
import javax.telephony.events.CallInvalidEv;
import javax.telephony.events.ConnAlertingEv;
import javax.telephony.events.ConnConnectedEv;
import javax.telephony.events.ConnCreatedEv;
import javax.telephony.events.ConnDisconnectedEv;
import javax.telephony.events.ConnFailedEv;
import javax.telephony.events.ConnInProgressEv;
import javax.telephony.events.ConnUnknownEv;
import javax.telephony.events.TermConnActiveEv;
import javax.telephony.events.TermConnCreatedEv;
import javax.telephony.events.TermConnDroppedEv;
import javax.telephony.events.TermConnPassiveEv;
import javax.telephony.events.TermConnRingingEv;
import javax.telephony.events.TermConnUnknownEv;
import javax.telephony.media.PlayerEvent;
import javax.telephony.media.PlayerListener;

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
        ObservableCallControl, CallObserver, PlayerListener {
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

        firePlayEvent();
        terminal.play(uri);
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecord() {
        // mediaService.stop();
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final URI destinationPhoneUri) {
    }

    /**
     * {@inheritDoc}
     */
    public void tranfer(final URI destinationPhoneUri, final Map props) {
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
    public void callActive(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("active call event with cause " + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void callInvalid(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("invalid call event with cause " + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void callEventTransmissionEnded(final CallEvent callEvent) {
        final int cause = callEvent.getCause();
        LOGGER.error("event transmission ended event with cause "
                + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressStarted(MetaEvent metaEvent) {
        final int cause = metaEvent.getCause();
        LOGGER.error("single call progress started event with cause "
                + causeToString(cause));
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaProgressEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void singleCallMetaSnapshotEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaMergeEnded(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferStarted(MetaEvent metaEvent) {
    }

    /**
     * {@inheritDoc}
     */
    public void multiCallMetaTransferEnded(MetaEvent metaEvent) {
    }

    /**
     * Convert the event cause string to a cause. Creation date: (2000-05-01
     * 9:58:39)
     * 
     * @author: Richard Deadman
     * @return English description of the cause
     * @param cause
     *            The Event cause id.
     */
    public String causeToString(final int cause) {
        switch (cause) {
        case Event.CAUSE_CALL_CANCELLED:
            return "Call canceled";
        case Event.CAUSE_DEST_NOT_OBTAINABLE:
            return "Destination not obtainable";
        case Event.CAUSE_INCOMPATIBLE_DESTINATION:
            return "Incompatible destination";
        case Event.CAUSE_LOCKOUT:
            return "Lockout";
        case Event.CAUSE_NETWORK_CONGESTION:
            return "Network congestion";
        case Event.CAUSE_NETWORK_NOT_OBTAINABLE:
            return "Network not obtainable";
        case Event.CAUSE_NEW_CALL:
            return "New call";
        case Event.CAUSE_NORMAL:
            return "Normal";
        case Event.CAUSE_RESOURCES_NOT_AVAILABLE:
            return "Resource not available";
        case Event.CAUSE_SNAPSHOT:
            return "Snapshot";
        case Event.CAUSE_UNKNOWN:
            return "Unknown";
        default:
            return "Cause mapping error: " + cause;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void callChangedEvent(final CallEv[] eventList) {
        String event = null;
        int id = eventList[0].getID();
        switch (id) {
        case CallActiveEv.ID: {
            event = "call active";
            break;
        }
        case CallInvalidEv.ID: {
            event = "call invalid";
            break;
        }
        case ConnAlertingEv.ID: {
            event = "Connection alerting";
            break;
        }
        case ConnConnectedEv.ID: {
            event = "Connection connected";
            break;
        }
        case ConnCreatedEv.ID: {
            event = "Connection created";
            break;
        }
        case ConnDisconnectedEv.ID: {
            event = "Connection disconnected";
            break;
        }
        case ConnFailedEv.ID: {
            event = "Connection failed";
            break;
        }
        case ConnInProgressEv.ID: {
            event = "Connection in progress";
            break;
        }
        case ConnUnknownEv.ID: {
            event = "Connection unknown";
            break;
        }
        case TermConnActiveEv.ID: {
            event = "Terminal Connection active";
            break;
        }
        case TermConnCreatedEv.ID: {
            event = "Terminal Connection created";
            break;
        }
        case TermConnDroppedEv.ID: {
            event = "Terminal Connection dropped";
            break;
        }
        case TermConnPassiveEv.ID: {
            event = "Terminal Connection passive";
            break;
        }
        case TermConnRingingEv.ID: {
            event = "Terminal Connection ringing";
            break;
        }
        case TermConnUnknownEv.ID: {
            event = "Terminal Connection unknown";
            break;
        }
        default:
            event = "unknown: " + id;
        }
        System.err.println("Observer event: " + event);
    }

    /**
     * {@inheritDoc}
     */
    public void onSpeedChange(PlayerEvent playerEvent) {
        System.err.print("\n onSpeedChange: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onVolumeChange(PlayerEvent playerEvent) {
        System.err.print("\n onVolumeChange: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onPause(PlayerEvent playerEvent) {
        System.err.print("\n onPause: "
                + playerEvent.getChangeType().toString());
    }

    /**
     * {@inheritDoc}
     */
    public void onResume(PlayerEvent playerEvent) {
        System.err.print("\n onResume: "
                + playerEvent.getChangeType().toString());
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
