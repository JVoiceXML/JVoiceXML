/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.external;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.ExternalRecognitionListener;

/**
 * Class to send RecognitionResults as String 
 * to external clients.
 * 
 * @author Josua Arndt
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class SocketExternalRecognitionListener
    implements ExternalRecognitionListener {
    /** Logger instance. */
    private static final Logger LOGGER =
             Logger.getLogger(SocketExternalRecognitionListener.class);
    
    /** the port to be listening on. */
    private int port;
    
    /** internal Thread handling all connections. */
    private SocketExternalListenerWorker worker = null;
    
    /** "recognition"
     *  - The workerthread will use this string to mark log messages. */
    private final String ASSIGNMENT = "recognition";
    
    /** Status of this listener. */
    private boolean running = false;
    
    /**
     * Constructs a new SocketExternalRecognitionListener.
     */
    public SocketExternalRecognitionListener() {
    }
    
    /**
     * Set the port to be used.
     *
     * @param portnumber
     *            used port
     */
    public void setPort(final int portnumber) {
        port = portnumber;
    }
    
    /**
     * Post a message to all connected Clients via this class's Worker.
     * @param msg
     *          the textmessage to be sent
     * @since 0.7.5
     */
    private void postMessage(final String msg) {
        worker.postMessage(msg);
    }

    /**
     * {@inheritDoc}
     * Sends recognized result text as string.
     */
    @Override
    public void resultAccepted(final RecognitionResult result) {
        final String[] textArry = result.getWords();
        final StringBuffer text = new StringBuffer();
        
        for (int i = 0; i < textArry.length; i++) {
            text.append(textArry[i].toString());
            text.append(" ");
        }
        
        if (running) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Recognition Listener sent Message:"
                            + text.toString() + "...");
            }
            postMessage("Recognized: '" + text.toString() + "'.");
        }
    }

    /**
     * {@inheritDoc}
     * Sends "Recognition Listener: result rejected." text as String.
     */
    @Override
    public void resultRejected(final RecognitionResult result) {
        final String msg = "Recognition Listener: result rejected.";
        if (running) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(msg);
            }
            postMessage(msg);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void start() {
        //if the workerthread has not been stopped, stop it
        if (worker != null) {
            worker.stopWorker();
        }
        
        worker = new SocketExternalListenerWorker(port, ASSIGNMENT);
        worker.start();
        running = true;
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("started socket external recognition listener at port '"
                    + port + "...");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void stop() {
        running = false;
        if (worker != null) {
            worker.stopWorker();
            worker = null;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...stopped socket external recognition listener");
        }
    }
}
