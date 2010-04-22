/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.text/src/org/jvoicexml/implementation/text/TextReceiverThread.java $
 * Version: $LastChangedRevision: 1304 $
 * Date:    $Date: 2008-12-08 11:10:04 +0200 (Δευ, 08 Δεκ 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.mary;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.log4j.Logger;

/**
 * Reads asynchronously some text input from the client.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1304 $
 * @since 0.6
 */
final class TextReceiverThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(TextReceiverThread.class);

    /** Maximum waiting time in msec. */
    private static final int MAX_WAIT = 300;

    /** The socket to read from. */
    private final Socket socket;

    /** Reference to the spoken input device. */
    private TextSpokenInput input;

    

    /** Set to <code>true</code> if the receiver thread is started. */
    private boolean started;
    
    
    private final  BufferedReader inBR;
    private final  InputStreamReader inISR;
    private  InputStream inIS;
    
    
    /**
     * Constructs a new object.
     * @param asyncSocket the socket to read from.
     */
    public TextReceiverThread(final Socket asyncSocket) {
        socket = asyncSocket;


        setDaemon(true);
        setName("TextReceiverThread");
        try {
            inIS = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        inISR = new InputStreamReader(inIS);
        inBR = new BufferedReader(inISR);


    }

    /**
     * Sets the spoken input device.
     * @param spokenInput
     *        the spoken input device.
     */
    void setSpokenInput(final TextSpokenInput spokenInput) {
        input = spokenInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread started");
        }
        
   
       synchronized (this) {
            notifyAll();
            started = true;
        }
        while (socket.isConnected() && !interrupted()) {
            String   str = null;
            try {
                
            str=inBR.readLine();     
          
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    
         input.notifyRecognitionResult(str);

        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("text receiver thread stopped");
        }
  //      telephony.recordStopped();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("text receiver thread stopped");
        }
    
        
    }      

    /**
     * Checks if the the receiver is in recording mode.
     * @return <code>true</code> if received user input is
     *         propagated to the user input.
     */
    boolean isRecording() {
        return input != null;
    }

    /**
     * Checks if the thread is started.
     * @return <code>true</code> if the thread is started.
     * @since 0.7
     */
    boolean isStarted() {
        return started;
    }

    /**
     * Delays until the receiver thread is started.
     * @exception InterruptedException
     *            waiting was interrupted.
     * @since 0.7
     */
    void waitStarted() throws InterruptedException {
        while (!isStarted()) {
            synchronized (this) {
                wait(MAX_WAIT);
            }
        }
    }
}
