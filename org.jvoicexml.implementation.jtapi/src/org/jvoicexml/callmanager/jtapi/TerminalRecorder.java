/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision: $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.jtapi;

import java.net.URI;
import java.util.Dictionary;

import javax.telephony.media.MediaResourceException;
import javax.telephony.media.NotBoundException;
import javax.telephony.media.RTC;
import javax.telephony.media.RecorderConstants;

import net.sourceforge.gjtapi.media.GenericMediaService;

import org.apache.log4j.Logger;
import org.jvoicexml.implementation.TelephonyEvent;

/**
 * Thread to record a stream from a given URI.
 *
 * @author lyncher
 * @version $Revision: $
 * @since 0.6
 */
class TerminalRecorder extends TerminalMedia {
    /** Logger instance. */
    private static final Logger LOGGER = Logger
            .getLogger(TerminalRecorder.class);

    /** Reference to the terminal. */
    private final JVoiceXmlTerminal terminal;

    /**
     * Constructs a new object.
     * @param term the terminal to notify about events.
     * @param service media service to play the audio.
     */
    public TerminalRecorder(final JVoiceXmlTerminal term,
            final GenericMediaService service) {
        super(service);
        terminal = term;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleStopProcessing() {
        try {
            final GenericMediaService service = getMediaService();
            service.triggerRTC(RecorderConstants.rtca_Stop);
        } catch (NotBoundException ex) {
            LOGGER.error("error stopping the media service", ex);
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final URI uri, final RTC[] rtc,
            final Dictionary<?, ?> optargs) throws
            MediaResourceException {
        final GenericMediaService service = getMediaService();
        service.record(uri.toString(), rtc, optargs);
    }

    /**
     * Prepocessing.
     */
    public void onPreProcess() {
        final TelephonyEvent event = new TelephonyEvent(terminal,
                TelephonyEvent.RECORD_STARTED);
        terminal.fireMediaEvent(event);
    }

    /**
     * Postprocessing.
     */
    public void onPostProcess() {
        final TelephonyEvent event = new TelephonyEvent(terminal,
                TelephonyEvent.RECORD_STOPPED);
        terminal.fireMediaEvent(event);
    }
}
