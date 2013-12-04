/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm.callmanager.mgcp;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jain.protocol.ip.mgcp.JainMgcpCommandEvent;
import jain.protocol.ip.mgcp.JainMgcpListener;
import jain.protocol.ip.mgcp.JainMgcpResponseEvent;
import jain.protocol.ip.mgcp.message.Constants;
import jain.protocol.ip.mgcp.message.NotificationRequest;
import jain.protocol.ip.mgcp.message.Notify;
import jain.protocol.ip.mgcp.message.NotifyResponse;
import jain.protocol.ip.mgcp.message.parms.EndpointIdentifier;
import jain.protocol.ip.mgcp.message.parms.EventName;
import jain.protocol.ip.mgcp.message.parms.RequestIdentifier;
import jain.protocol.ip.mgcp.message.parms.RequestedAction;
import jain.protocol.ip.mgcp.message.parms.RequestedEvent;
import jain.protocol.ip.mgcp.message.parms.ReturnCode;
import jain.protocol.ip.mgcp.pkg.MgcpEvent;
import jain.protocol.ip.mgcp.pkg.PackageName;

import org.apache.log4j.Logger;
import org.mobicents.protocols.mgcp.jain.pkg.AUMgcpEvent;
import org.mobicents.protocols.mgcp.jain.pkg.AUPackage;

import org.mobicents.servlet.sip.restcomm.FiniteStateMachine;
import org.mobicents.servlet.sip.restcomm.State;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.au.AdvancedAudioParametersBuilder;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe
public final class MgcpIvrEndpoint extends FiniteStateMachine implements JainMgcpListener, MgcpEndpoint {

    private static final Logger LOGGER = Logger.getLogger(MgcpIvrEndpoint.class);
    private static final PackageName PACKAGE_NAME = AUPackage.AU;
    private static final RequestedEvent[] REQUESTED_EVENTS = new RequestedEvent[2];
    public static final State IDLE = new State("IDLE");
    public static final State PLAY = new State("PLAY");
    public static final State PLAY_COLLECT = new State("PLAY_COLLECT");
    public static final State PLAY_RECORD = new State("PLAY_RECORD");
    public static final State STOP = new State("STOP");
    public static final State FAILED = new State("FAILED");

    static {
        		final RequestedAction[] action = new RequestedAction[] { RequestedAction.NotifyImmediately };
		REQUESTED_EVENTS[0] = new RequestedEvent(new EventName(PACKAGE_NAME, AUMgcpEvent.auoc), action);
		REQUESTED_EVENTS[1] = new RequestedEvent(new EventName(PACKAGE_NAME, AUMgcpEvent.auof), action);

        IDLE.addTransition(PLAY);
        IDLE.addTransition(PLAY_COLLECT);
        IDLE.addTransition(PLAY_RECORD);
        IDLE.addTransition(FAILED);
        IDLE.addTransition(STOP);
        PLAY.addTransition(IDLE);
        PLAY.addTransition(STOP);
        PLAY.addTransition(FAILED);
        PLAY_COLLECT.addTransition(IDLE);
        PLAY_COLLECT.addTransition(STOP);
        PLAY_COLLECT.addTransition(FAILED);
        PLAY_RECORD.addTransition(IDLE);
        PLAY_RECORD.addTransition(STOP);
        PLAY_RECORD.addTransition(FAILED);
        STOP.addTransition(IDLE);
        STOP.addTransition(FAILED);
        //Shadowman
        STOP.addTransition(PLAY);
    }
    private final MgcpServer server;
    private final EndpointIdentifier any;
    private volatile EndpointIdentifier endpointId;
    private volatile RequestIdentifier requestId;
    private String digits;
    private final List<MgcpIvrEndpointObserver> observers;

    public MgcpIvrEndpoint(final MgcpServer server) {
        super(IDLE);
        addState(IDLE);
        addState(PLAY);
        addState(PLAY_COLLECT);
        addState(PLAY_RECORD);
        addState(STOP);
        addState(FAILED);
        server.addNotifyListener(this);
        this.server = server;
        this.any = new EndpointIdentifier("mobicents/ivr/$", server.getDomainName());
        this.observers = Collections.synchronizedList(new ArrayList<MgcpIvrEndpointObserver>());
    }

    public void addObserver(final MgcpIvrEndpointObserver observer) {
        observers.add(observer);
    }

     private void fireOperationCompleted(State oldstate)
     {
        setOldState(oldstate);
        for (final MgcpIvrEndpointObserver observer : observers) 
        {
            LOGGER.debug(" for observer:"+observer);
            observer.operationCompleted(this,oldstate);
        }
    }

    private void fireOperationFailed() {
        for (final MgcpIvrEndpointObserver observer : observers) {
            observer.operationFailed(this);
        }
    }

    public String getDigits() {
        assertState(IDLE);
        return digits;
    }

    @Override
    public EndpointIdentifier getId() {
        if (endpointId != null) {
            return endpointId;
        } else {
            return any;
        }
    }

    public synchronized void play(final List<URI> announcements, final int iterations) 
    {
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(IDLE);
        possibleStates.add(STOP);
        assertState(possibleStates);
        // Create the signal parameters.
        final AdvancedAudioParametersBuilder builder = new AdvancedAudioParametersBuilder();
        for (final URI announcement : announcements) {
            LOGGER.debug("Announcement URI: " + announcement);
            builder.addAnnouncement(announcement);
        }
        builder.setIterations(iterations);
        final String parameters = builder.build();
        // Create the signal.
        final EventName[] signal = new EventName[1];
//        signal[0] = new EventName(PACKAGE_NAME, MgcpEvent.factory("pa").withParm(parameters));
        signal[0] = new EventName(PACKAGE_NAME, AUMgcpEvent.aupa.withParm(parameters));
        // Create notification request.
        requestId = server.generateRequestIdentifier();
        final NotificationRequest request = new NotificationRequest(this, endpointId, requestId);
        request.setSignalRequests(signal);
        request.setNotifiedEntity(server.getCallAgent());
        request.setRequestedEvents(REQUESTED_EVENTS);
        // Send the request.
        LOGGER.debug("NotificationRequest: " + request);
        server.sendCommand(request, this);
        setState(PLAY);
    }

    public synchronized void playCollect(final List<URI> prompts, final int maxNumberOfDigits, final int minNumberOfDigits,
            final long firstDigitTimer, final long interDigitTimer, final String patterns) {
        assertState(IDLE);
        //Issue 106: http://code.google.com/p/vnxivr/issues/detail?id=106
        digits = null;
        // Create the signal parameters.
        final AdvancedAudioParametersBuilder builder = new AdvancedAudioParametersBuilder();
        for (final URI prompt : prompts) {
            builder.addInitialPrompt(prompt);
        }
        builder.setClearDigitBuffer(true);
        builder.setMaxNumberOfDigits(maxNumberOfDigits);
        builder.setMinNumberOfDigits(minNumberOfDigits);
        builder.setFirstDigitTimer(firstDigitTimer);
        builder.setInterDigitTimer(interDigitTimer);
        builder.setDigitPattern(patterns);
        final String parameters = builder.build();
        // Create the signal.
        final EventName[] signal = new EventName[1];
        signal[0] = new EventName(PACKAGE_NAME, AUMgcpEvent.aupc.withParm(parameters));
        // Create notification request.
        requestId = server.generateRequestIdentifier();
        final NotificationRequest request = new NotificationRequest(this, endpointId, requestId);
        request.setSignalRequests(signal);
        request.setNotifiedEntity(server.getCallAgent());
        request.setRequestedEvents(REQUESTED_EVENTS);
        // Send the request.
        server.sendCommand(request, this);
        setState(PLAY_COLLECT);
    }

    public synchronized void playRecord(final List<URI> prompts, final URI recordId, final long postSpeechTimer,
            final long recordingLength, final String patterns) 
    {
        try{
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(IDLE);
        possibleStates.add(STOP);
        assertState(possibleStates);
        // Create the signal parameters.
        final AdvancedAudioParametersBuilder builder = new AdvancedAudioParametersBuilder();
        if(prompts !=null)
        {
            for (final URI prompt : prompts) {
                LOGGER.debug("Prompt added: " + prompt.toString());
                builder.addInitialPrompt(prompt);
            }
        }
        builder.setNonInterruptPlay(true);
        builder.setClearDigitBuffer(true);
        builder.setRecordId(recordId);
        builder.setPostSpeechTimer(postSpeechTimer);
        builder.setRecordingLength(recordingLength);
        builder.setDigitPattern(patterns);
        final String parameters = builder.build();
        // Create the signal.
        final EventName[] signal = new EventName[1];
        signal[0] = new EventName(PACKAGE_NAME, AUMgcpEvent.aupr.withParm(parameters));
        // Create notification request.
        requestId = server.generateRequestIdentifier();
        final NotificationRequest request = new NotificationRequest(this, endpointId, requestId);
        request.setSignalRequests(signal);
        request.setNotifiedEntity(server.getCallAgent());
        request.setRequestedEvents(REQUESTED_EVENTS);
        // Send the request.
        server.sendCommand(request, this);
        setState(PLAY_RECORD);
        }catch(Exception ex)
        {
            LOGGER.error(ex);
        }
    }

    private Map<String, String> parseAdvancedAudioParameters(final String input) {
        final Map<String, String> parameters = new HashMap<String, String>();
        final String[] tokens = input.split(" ");
        for (final String token : tokens) {
            final String[] values = token.split("=");
            if (values.length == 1) {
                parameters.put(values[0], null);
            } else if (values.length == 2) {
                parameters.put(values[0], values[1]);
            }
        }
        return parameters;
    }

    @Override
    public synchronized void processMgcpCommandEvent(final JainMgcpCommandEvent command) {
        LOGGER.debug("incomming MGCP command event:" + command + " currentState:"+getState());
        final int commandValue = command.getObjectIdentifier();
        switch (commandValue) {
            case Constants.CMD_NOTIFY: {
                final Notify request = (Notify) command;
                if (requestId != null && request.getRequestIdentifier().toString().equals(requestId.toString())) {
                    final EventName[] observedEvents = request.getObservedEvents();
                    // We are only waiting for "operation completed" or "operation failed" events.
                    if (observedEvents.length == 1) {
                        final MgcpEvent response = observedEvents[0].getEventIdentifier();
                        final Map<String, String> parameters = parseAdvancedAudioParameters(response.getParms());
                        // Process parameters.
                        final int returnCode = Integer.parseInt(parameters.get("rc"));
                        if (returnCode == 100 || returnCode == 326 || returnCode == 327 || returnCode == 328) 
                        {
                            final State currentState = getState();
                            if (currentState.equals(PLAY_COLLECT) || currentState.equals(PLAY_RECORD)) 
                            {
                                if (returnCode == 100) 
                                {
                                    digits = parameters.get("dc");
                                } else if (returnCode == 328) 
                                {
                                    stop();
                                }
                            }
                            setState(IDLE);
                            fireOperationCompleted(currentState);
                        } else {
                            setState(FAILED);
                            fireOperationFailed();
                            final StringBuilder buffer = new StringBuilder();
                            buffer.append(response.getName()).append(" failed with the following error code: ").append(returnCode);
                            LOGGER.error(buffer.toString());
                        }
                    } else {
                        setState(FAILED);
                        fireOperationFailed();
                        LOGGER.error("The Notify request does not contain any observed events.");
                    }
                    // Notify the media server that the response was properly handled.
                    final NotifyResponse response = new NotifyResponse(this, ReturnCode.Transaction_Executed_Normally);
                    response.setTransactionHandle(command.getTransactionHandle());
                    server.sendResponse(response);
                }
                break;
            }
            default: {
                return;
            }
        }
    }

    @Override
    public void processMgcpResponseEvent(final JainMgcpResponseEvent response) {
        LOGGER.debug("incomming MGCP response event:" + response + " currentState:"+getState().getName());
        final ReturnCode code = response.getReturnCode();
        if (code.getValue() == ReturnCode.TRANSACTION_BEING_EXECUTED) {
            return;
        } else if (code.getValue() == ReturnCode.TRANSACTION_EXECUTED_NORMALLY) {
            final State currentState = getState();
            if (STOP.equals(currentState)) 
            {
                setState(IDLE);
                fireOperationCompleted(currentState);
            }
            return;
        } else {
            setState(FAILED);
            fireOperationFailed();
            final String error = new StringBuilder().append(code.getValue()).append(" ").append(code.getComment()).toString();
            LOGGER.error(error);
        }
    }

    public void release() {
        final State currentState = getState();
        if (currentState.equals(PLAY) || currentState.equals(PLAY_COLLECT)
                || currentState.equals(PLAY_RECORD)) {
            stop();
        }
        server.removeNotifyListener(this);
    }

    public void removeObserver(final MgcpIvrEndpointObserver observer) {
        observers.remove(observer);
    }

    public void stop() 
    {
        LOGGER.debug("stoping ivrendpoint...");
        final List<State> possibleStates = new ArrayList<State>();
        possibleStates.add(PLAY);
        possibleStates.add(PLAY_COLLECT);
        possibleStates.add(PLAY_RECORD);
        possibleStates.add(IDLE);
        assertState(possibleStates);
        // Create the signal.
        final EventName[] signal = new EventName[1];
        signal[0] = new EventName(PACKAGE_NAME, AUMgcpEvent.aues);
        // Create notification request.
        requestId = server.generateRequestIdentifier();
        final NotificationRequest request = new NotificationRequest(this, endpointId, requestId);
        request.setSignalRequests(signal);
        request.setNotifiedEntity(server.getCallAgent());
        request.setRequestedEvents(REQUESTED_EVENTS);
        // Send the request.
        server.sendCommand(request, this);
        setState(STOP);
    }

    @Override
    public synchronized void updateId(final EndpointIdentifier endpointId) {
        this.endpointId = endpointId;
    }
    
     public String toString()
    {
        return " ivrEndpoint:" + " state:"+getState()
                + " oldstate:"+getOldState();
    }
}
