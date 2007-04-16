package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.telephony.Address;
import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.CallObserver;
import javax.telephony.Connection;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.Event;
import javax.telephony.InvalidStateException;
import javax.telephony.MetaEvent;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.PrivilegeViolationException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.TerminalConnection;
import javax.telephony.callcontrol.CallControlCall;
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
import javax.telephony.media.MediaResourceException;
import javax.telephony.media.PlayerEvent;
import javax.telephony.media.PlayerListener;

import net.sourceforge.gjtapi.media.GenericMediaService;
import net.sourceforge.gjtapi.raw.sipprovider.common.Console;

import org.jvoicexml.callmanager.CallControlListener;
import org.jvoicexml.callmanager.ObservableCallControl;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: L2f,Inesc-id</p>
 *
 * @author
 * @version 1.0
 */
public class JtapiXmlCallControl implements ObservableCallControl, ConnectionListener,
        CallObserver,
        PlayerListener {

//log
    protected static Console console = Console.getConsole(JtapiXmlCallControl.class);

    //listener
    private List<CallControlListener> callControlListeners;

    private GenericMediaService _mediaService;

    Connection _connection = null;

    /**
     *
     * @param mediaService GenericMediaService
     */
    public JtapiXmlCallControl(GenericMediaService mediaService) {
        console.logEntry();

        //listener to Jvxml
        callControlListeners = new ArrayList<CallControlListener>();

        //Media service object
        _mediaService = mediaService;

        //Adds an listener to a Call object when this Address object first becomes part of that Call.
        try {

            Address[] addr = _mediaService.getTerminal().getAddresses();
            Terminal[] term = addr[0].getTerminals();
            for (int i = 0; i < term.length; i++) {
                console.debug("added a listener to terminal : " +
                              term[i].getName());
            }

            Address[] addrs = _mediaService.getTerminal().getAddresses();
            CallListener[] listener = _mediaService.getTerminal().
                                      getCallListeners();
            String terminalName = _mediaService.getTerminal().getName();

            if (_mediaService.getTerminal().getCallListeners() == null) { //validate if Terminal have already a listener
                for (int i = 0; i < addrs.length; i++) {
                    if (terminalName.equals(addrs[i].getName())) { //search the address that correspond to this terminal
                        addrs[i].addCallListener(this); //add a call Listener
                        addrs[i].addCallObserver(this); //add a call Obeserver
                    }
                }
            }
        } catch (MethodNotSupportedException ex) {
            ex.printStackTrace();
            console.error("", ex);
        } catch (ResourceUnavailableException ex) {
            ex.printStackTrace();
            console.error("", ex);
        }

        console.logExit();
    }

    /**
     * addListener
     *
     * @param listener CallControlListener
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void addListener(CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.add(listener);
        }
    }

    /**
     *
     * @return String
     */
    public String getTerminalName() {
        return _mediaService.getTerminal().getName();
    }

    /**
     * removeListener
     *
     * @param listener CallControlListener
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void removeListener(CallControlListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(listener);
        }
    }

    /**
     * play
     *
     * @param sourceUri URI
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void play(URI sourceUri) {

        firePlayEvent();
        PlayerEvent pEvent = null;
        try {
            pEvent = _mediaService.play(
                    sourceUri.toString(), 0, null, null);
        } catch (MediaResourceException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    public void stopPlay() {
        _mediaService.stop();
    }

    /**
     * record
     *
     * @param destinationUri URI
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void record(URI destinationUri) {

        fireRecordEvent(); //may be after record method!!!

        try {
            _mediaService.record(destinationUri.toString(), null, null);
        } catch (MediaResourceException ex) {
            ex.printStackTrace();
        }

    }

    /**
     *
     */
    public void stopRecord() {
        _mediaService.stop();
    }

    /**
     * tranfer
     *
     * @param destinationPhoneUri URI
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void tranfer(URI destinationPhoneUri) {
    }

    /**
     *
     * @param destinationPhoneUri URI
     * @param props Map
     * @todo Implement this org.jvoicexml.callmanager.CallControl method
     */
    public void tranfer(URI destinationPhoneUri, Map props) {
    }

    /**
     *
     */
    protected void fireAnswerEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.answered();
            }
        }
    }

    /**
     *
     */
    protected void firePlayEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStarted();
            }
        }
    }

    /**
     *
     */
    protected void fireRecordEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.recordStarted();
            }
        }
    }

    /**
     *
     */
    protected void firehangedUpEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.hangedup();
            }
        }
    }

    /**
     *
     */
    protected void fireplayStoppedEvent() {
        synchronized (callControlListeners) {
            for (CallControlListener listener : callControlListeners) {
                listener.playStopped();
            }
        }
    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionAlerting(ConnectionEvent connectionEvent) {
        System.err.println("5.3.3: Alerting Connection event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionConnected(ConnectionEvent connectionEvent) {

        System.err.println("5.3.4: Connection Connected event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
        CallControlCall call = (CallControlCall) connectionEvent.getCall();
        System.err.println("CallingAddress: " +
                           call.getCallingAddress().getName());
        System.err.println("CalledAddress: " + call.getCalledAddress().getName());

        fireAnswerEvent();
    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionCreated(ConnectionEvent connectionEvent) {
        System.err.println("5.3.1: Connection Created event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionDisconnected(javax.telephony.ConnectionEvent
                                       connectionEvent) {
        System.err.println("5.3.5: Connection Disconnected event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
        /**
         * @todo doen't entry when HangUp- fix this problem
         */
        this.stopPlay();

        firehangedUpEvent();
        try {
            if (_connection != null) {
                System.err.println(
                        "\n Its going to disconnect the connection!!");
                _connection.disconnect();
            }

        } catch (InvalidStateException ex) {
            ex.printStackTrace();
        } catch (MethodNotSupportedException ex) {
            ex.printStackTrace();
        } catch (ResourceUnavailableException ex) {
            ex.printStackTrace();
        } catch (PrivilegeViolationException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionFailed(ConnectionEvent connectionEvent) {
        System.err.println("5.3.6: Connection Failed event with cause: " +
                           this.causeToString(connectionEvent.getCause()));

    }

    /**
     * call in progress. In this state we have to answer the call and
     * inform jvxml (may be!)
     * @param connectionEvent ConnectionEvent
     */
    public void connectionInProgress(ConnectionEvent connectionEvent) {
        System.err.println("5.3.2: Connection in Progress event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
        //we have to answer the call and inform jvxml

        Test test = new Test(this, "");

        TerminalConnection[] tc = _mediaService.getTerminal().
                                  getTerminalConnections();

        if (tc != null && tc.length > 0) {
            TerminalConnection termConn = tc[0];
            _connection = termConn.getConnection();
            try {
                termConn.answer();
            } catch (InvalidStateException ex) {
                ex.printStackTrace();
            } catch (MethodNotSupportedException ex) {
                ex.printStackTrace();
            } catch (ResourceUnavailableException ex) {
                ex.printStackTrace();
            } catch (PrivilegeViolationException ex) {
                ex.printStackTrace();
            }
        } else {
            System.err.println(" failed to find any TerminalConnections");
        }

    }

    /**
     *
     * @param connectionEvent ConnectionEvent
     */
    public void connectionUnknown(ConnectionEvent connectionEvent) {
        System.err.println("5.3.7: Connection Unknown event with cause: " +
                           this.causeToString(connectionEvent.getCause()));
    }

    /**
     *
     * @param callEvent CallEvent
     */
    public void callActive(CallEvent callEvent) {
        System.err.println("5.1.1: Active Call event with cause: " +
                           this.causeToString(callEvent.getCause()));
    }

    /**
     *
     * @param callEvent CallEvent
     */
    public void callInvalid(CallEvent callEvent) {
        System.err.println("5.1.2: Invalid Call event with cause: " +
                           this.causeToString(callEvent.getCause()));
    }

    /**
     *
     * @param callEvent CallEvent
     */
    public void callEventTransmissionEnded(CallEvent callEvent) {
        System.out.println(
                "5.1.3: Event Transmission Ended Call event with cause: " +
                this.causeToString(callEvent.getCause()));

    }

    /**
     *
     * @param metaEvent MetaEvent
     */
    public void singleCallMetaProgressStarted(MetaEvent metaEvent) {
        System.err.println("X.X: Multicall progress started event with cause: " +
                           this.causeToString(metaEvent.getCause()));
    }

    public void singleCallMetaProgressEnded(MetaEvent metaEvent) {

    }

    public void singleCallMetaSnapshotStarted(MetaEvent metaEvent) {
    }

    public void singleCallMetaSnapshotEnded(MetaEvent metaEvent) {
    }

    public void multiCallMetaMergeStarted(MetaEvent metaEvent) {
    }

    public void multiCallMetaMergeEnded(MetaEvent metaEvent) {
    }

    public void multiCallMetaTransferStarted(MetaEvent metaEvent) {
    }

    public void multiCallMetaTransferEnded(MetaEvent metaEvent) {
    }

    /**
     * Convert the event cause string to a cause.
     * Creation date: (2000-05-01 9:58:39)
     * @author: Richard Deadman
     * @return English description of the cause
     * @param cause The Event cause id.
     */
    public String causeToString(int cause) {
        switch (cause) {
        case Event.CAUSE_CALL_CANCELLED: {
            return "Call cancelled";
        }
        case Event.CAUSE_DEST_NOT_OBTAINABLE: {
            return "Destination not obtainable";
        }
        case Event.CAUSE_INCOMPATIBLE_DESTINATION: {
            return "Incompatable destination";
        }
        case Event.CAUSE_LOCKOUT: {
            return "Lockout";
        }
        case Event.CAUSE_NETWORK_CONGESTION: {
            return "Network congestion";
        }
        case Event.CAUSE_NETWORK_NOT_OBTAINABLE: {
            return "Network not obtainable";
        }
        case Event.CAUSE_NEW_CALL: {
            return "New call";
        }
        case Event.CAUSE_NORMAL: {
            return "Normal";
        }
        case Event.CAUSE_RESOURCES_NOT_AVAILABLE: {
            return "Resource not available";
        }
        case Event.CAUSE_SNAPSHOT: {
            return "Snapshot";
        }
        case Event.CAUSE_UNKNOWN: {
            return "Unknown";
        }
        }
        return "Cause mapping error: " + cause;
    }

    /**
     *
     * @param eventList CallEv[]
     */
    public void callChangedEvent(CallEv[] eventList) {
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

    public void onSpeedChange(PlayerEvent playerEvent) {
        System.err.print("\n onSpeedChange: " +
                         playerEvent.getChangeType().toString());
    }

    public void onVolumeChange(PlayerEvent playerEvent) {
        System.err.print("\n onVolumeChange: " +
                         playerEvent.getChangeType().toString());
    }

    public void onPause(PlayerEvent playerEvent) {
        System.err.print("\n onPause: " + playerEvent.getChangeType().toString());
    }

    public void onResume(PlayerEvent playerEvent) {
        System.err.print("\n onResume: " + playerEvent.getChangeType().toString());
    }

}
