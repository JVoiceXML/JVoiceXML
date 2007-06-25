/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import java.io.IOException;

import org.jvoicexml.CallControl;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import java.net.URI;
import java.util.Map;
import org.jvoicexml.callmanager.jtapi.JtapiCallManager;
import org.jvoicexml.callmanager.jtapi.JtapiCallControl;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Dummy implementation of a {@link CallControl} resource.
 *
 * <p>
 * This implementation of a {@link CallControl} resource can be used, if there
 * is no telephony support.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
public final class DummyCallControl implements CallControl {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DummyCallControl.class);

    private JtapiCallControl _callControl;

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("activating call...");
        }
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
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    public void open() throws NoresourceError {

        /**
         * @todo terminal name can't be hard code
         * change this!
         */
        _callControl = JtapiCallManager.getTerminal("sip:1002@172.16.4.20");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("allocating terminal...");
        }

    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("passivating call...");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void connect(final RemoteClient client) throws IOException {
    }

    /**
     *
     * @param sourceUri URI
     */
    public void play(URI sourceUri) {
        _callControl.play(sourceUri);
    }

    /**
     *
     */
    public void stopPlay() {
        _callControl.stopPlay();
    }

    /**
     * Start to record or stream audio to the ASR
     * @param destinationUri URI
     */
    public void record(URI destinationUri) {
        _callControl.record(destinationUri);
    }

    /**
     * Stop to record or stop the streaming to the recognize
     */
    public void stopRecord() {
        _callControl.stopRecord();
    }

    /**
     *
     * @param destinationPhoneUri URI
     */
    public void tranfer(URI destinationPhoneUri) {
    }

    public void tranfer(URI destinationPhoneUri, Map props) {
    }
}
