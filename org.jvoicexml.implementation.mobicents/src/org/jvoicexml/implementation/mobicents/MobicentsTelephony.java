/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/core/trunk/org.jvoicexml.implementation.jtapi/src/org/jvoicexml/implementation/jtapi/JtapiTelephony.java $
 * Version: $LastChangedRevision: 2913 $
 * Date:    $Date: 2012-01-30 15:41:09 +0700 (Mon, 30 Jan 2012) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2012-01-30 15:41:09 +0700 (Mon, 30 Jan 2012) $, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.implementation.mobicents;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;

import org.jvoicexml.CallControlProperties;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.JVoiceXmlRecordResult;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.jvxml.RecordingEvent;
import org.jvoicexml.implementation.ObservableTelephony;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.TelephonyEvent;
import org.jvoicexml.implementation.TelephonyListener;
import org.jvoicexml.implementation.mobicents.callmanager.MobicentsConnectionInformation;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpCallTerminal;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpIvrEndpoint;
import org.mobicents.servlet.sip.restcomm.media.api.Call;
import org.mobicents.servlet.sip.restcomm.media.api.CallObserver;
import org.util.ExLog;

/**
 * JTAPI based implementation of a {@link Telephony}.
 *
 * <p>
 * Audio output and user input is achieved via URIs.
 * </p>
 *
 * @author Hugo Monteiro
 * @author Renato Cassaca
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2913 $
 * @since 0.6
 */
public final class MobicentsTelephony implements Telephony,
        ObservableTelephony, TelephonyListener,CallObserver {
    private static final Logger LOGGER = Logger.getLogger(MobicentsTelephony.class);
    /** Listener to this call control. */
    private final Collection<TelephonyListener> callControlListeners;
    /** The SIP MGCP connection. */
    private MgcpCallTerminal terminal;
    
    private CallControlProperties props;

    /**
     * Constructs a new object.
     */
    public MobicentsTelephony() {
        callControlListeners = new java.util.ArrayList<TelephonyListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(final TelephonyListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.add(listener);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(final TelephonyListener listener) {
        synchronized (callControlListeners) {
            callControlListeners.remove(listener);
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation uses
     * {@link SynthesizedOutput#getUriForNextSynthesisizedOutput()} to obtain
     * a URI that is being used to stream to the terminal.
     *
     * <p>
     * Although this terminal is the source where to stream the audio this
     * implementation makes no assumptions about the URI. In most cases
     * this will be related to the {@link SpokenInput} implementation. In
     * the simplest case this implementation <emph>invents</emph> a
     * unique URI.
     * </p>
     */
    public void play(final SynthesizedOutput output,
            final CallControlProperties props)
            throws NoresourceError, IOException {
        try {
            if (terminal == null) {
                throw new NoresourceError("No active telephony connection!");
            }
            URI uri=null;
            try {
                LOGGER.debug("status of the terminal " + terminal );
                //checking termnal status before play
                if((terminal.getIVREndPointState()==MgcpIvrEndpoint.PLAY||
                        terminal.getIVREndPointState()==MgcpIvrEndpoint.PLAY_COLLECT||
                                terminal.getIVREndPointState()==MgcpIvrEndpoint.PLAY_RECORD)&&
                        terminal.getIVREndPointState()!=MgcpIvrEndpoint.STOP)
                {
                    LOGGER.debug("canceling current playing....  " );
//                    terminal.stopMedia();
                }
                uri = output.getUriForNextSynthesisizedOutput();
            } catch (URISyntaxException e) {
                throw new IOException(e.getMessage(), e);
            }
            LOGGER.debug("playing URI '" + uri + "'" + " CallControlProperties: "+props);
            terminal.play(uri, 1);
            terminal.addObserver((MobicentsSynthesizedOutput)output);
//            terminal.play(new URI("http://192.168.146.146:8080/VNXIVR/audio/dtmf_welcome.wav"), 1);
//            Thread.sleep(10000);
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }
    }

    /**
     * {@inheritDoc}
     *
     * This implementation uses
     * {@link SpokenInput#getUriForNextSpokenInput()} to obtain
     * a URI to stream from the terminal to the spoken input device.
     */
    @Override
    public void record(final SpokenInput input,
            final CallControlProperties props)
            throws NoresourceError, IOException 
    {
        LOGGER.debug(" with input:" + input + " CallControlProperties:"+props);
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        final URI uri;
        try {
            uri = input.getUriForNextSpokenInput();
        } catch (URISyntaxException e) {
            throw new IOException(e.getMessage(), e);
        }
        // TODO Do the actual recording.
        if (true) {
            LOGGER.debug("recording to URI '" + uri + "'...");
        }
        // TODO Move the code from the FIA to here.
//        terminal.record(uri, null);
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat getRecordingAudioFormat() 
    {
        LOGGER.error("current don't support this function");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void startRecording(final SpokenInput input,
            final OutputStream stream, final CallControlProperties props)
            throws NoresourceError, IOException 
    {
        LOGGER.debug("with input:" + input + " stream:"+stream + " props:"+props);
        if (terminal == null) 
        {
            throw new NoresourceError("No active telephony connection!");
        }
        this.props = props;
        terminal.addObserver(this);
        //adding observer this
        terminal.recordAfterBeep((long)props.getGenProperties(JVoiceXmlRecordResult.DURATION));
        
    }
     /**
     * 
     * @param call 
     * @Override
     */
    public void onStatusChanged(Call call) 
    {
        try 
        {
            MgcpCallTerminal term=(MgcpCallTerminal)call;
            LOGGER.debug(" for CallControlProperties:"+props + " " + call.toString() );
            if ((Call.Status.COMPLETED == call.getStatus() || Call.Status.IN_PROGRESS == call.getStatus())
                    && term.getIVREndPointState() ==MgcpIvrEndpoint.IDLE
                    && (term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY
                    || term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY_COLLECT
                    || term.getIVREndPointOldState() ==MgcpIvrEndpoint.PLAY_RECORD))
            {
                JVoiceXmlRecordResult result = new JVoiceXmlRecordResult();
                long duration=(long)props.getGenProperties(JVoiceXmlRecordResult.DURATION);
                result.setDuration(duration);
                if(duration >= JVoiceXmlRecordResult.DEFAULT_RECORDING_MAXTIME)
                    result.setMAXTIME("true");
                else result.setMAXTIME("false");
                result.setTermchar((String)props.getGenProperties(JVoiceXmlRecordResult.TERMCHAR));
                result.setSize(-1);
                final JVoiceXMLEvent event = new RecordingEvent(null);
                ((RecordingEvent)event).setInputResult(result);
                EventHandler handler=(EventHandler)props.getGenProperties("EventHandler");
                handler.notifyEvent(event);
            }
        } catch (Exception ex) {
            ExLog.exception(LOGGER, ex);
        }

    }

    /**
     * {@inheritDoc}
     */
    public void stopRecording() throws NoresourceError {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }
        LOGGER.debug(" ..." );
        terminal.stopMedia();
    }

    /**
     * Inform the {@link TelephonyListener} about an answered event.
     */
    protected void fireAnswerEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.ANSWERED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a play stopped event.
     * @param uri destination URI of the trasfer.
     */
    protected void fireTransferEvent(final String uri) {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.TRANSFERRED, uri);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallTransferred(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a play started event.
     */
    protected void firePlayEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.PLAY_STARTED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a play stopped event.
     */
    protected void firePlayStoppedEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.PLAY_STOPPED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a record started event.
     */
    protected void fireRecordStartedEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.RECORD_STARTED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a record stopped event.
     */
    protected void fireRecordStoppedEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.RECORD_STOPPED);
        for (TelephonyListener listener : tmp) {
            listener.telephonyMediaEvent(event);
        }
    }

    /**
     * Inform the {@link TelephonyListener} about a hangup event.
     */
    protected void fireHangedUpEvent() {
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        final TelephonyEvent event = new TelephonyEvent(this,
                TelephonyEvent.HUNGUP);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallHungup(event);
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
        return "mobicents";
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
        callControlListeners.clear();
    }

   

    /**
     * {@inheritDoc}
     */
    public void stopPlay() throws NoresourceError
    {
        if (terminal == null) 
        {
            throw new NoresourceError("No active telephony connection!");
        }
        LOGGER.info("hangup the terminal:"+terminal);
        terminal.stopMedia();
//        terminal.hangup();
        
    }

    /**
     * {@inheritDoc}
     */
    public void transfer(final String dest) throws NoresourceError {
        if (terminal == null) {
            throw new NoresourceError("No active telephony connection!");
        }

        LOGGER.error("not support yet");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBusy() {
        if (terminal == null) {
            return false;
        }
        LOGGER.error("not support yet");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallAnswered(final TelephonyEvent event) 
    {
        LOGGER.debug("event :"+event);
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallHungup(final TelephonyEvent event) 
    {
        LOGGER.debug("event :"+event);
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallHungup(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyMediaEvent(final TelephonyEvent event) 
    {
        LOGGER.debug("event :"+event);
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyCallAnswered(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void telephonyCallTransferred(final TelephonyEvent event) {
        LOGGER.debug("event :"+event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void telephonyError(final ErrorEvent error) {
        LOGGER.debug("ErrorEvent :"+error);
        final Collection<TelephonyListener> tmp =
                new java.util.ArrayList<TelephonyListener>(callControlListeners);
        for (TelephonyListener listener : tmp) {
            listener.telephonyError(error);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void connect(final ConnectionInformation info) throws IOException 
    {
        LOGGER.info(".. with ConnectionInformation:"+info);
        final MobicentsConnectionInformation vnxivrInfo =
            (MobicentsConnectionInformation) info;
        terminal = vnxivrInfo.getTerminal();
        terminal.addListener(this);
        
    }

    /**
     * {@inheritDoc}
     */
    public void disconnect(final ConnectionInformation info) 
    {
        LOGGER.info(".. with ConnectionInformation:"+info);
        final MobicentsConnectionInformation vnxivrInfo =
            (MobicentsConnectionInformation) info;
        terminal = vnxivrInfo.getTerminal();
        terminal.disconnect();
        terminal.removeListener(this);
    }
    public String toString()
    {
        return " telephone";
    }
}
