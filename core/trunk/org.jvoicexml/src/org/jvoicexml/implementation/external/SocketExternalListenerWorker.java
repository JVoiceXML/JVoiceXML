/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/SessionStorage.java $
 * Version: $LastChangedRevision: 2839 $
 * Date:    $Date: 2011-10-13 09:33:06 +0200 (Do, 13 Okt 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.external;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * This class handles all communications with external clients 
 * and is managed by a {@link SocketExternalRecognitionListener} or
 * {@link SocketExternalSynthesisListener}.
 * 
 * @author Markus Baumgart <info@CIBEK.de>
 * @author Dirk SChnelle-Walka
 * @version $Revision: $
 * @since 0.7.5
 * @see SocketExternalRecognitionListener
 * @see SocketExternalSynthesisListener
 */
public class SocketExternalListenerWorker extends Thread {
    /** Logger instance. */
    private static final Logger LOGGER =
             Logger.getLogger(SocketExternalListenerWorker.class);

    /** The port to be listening on. */
    private final int port;
    
    /** 
     * Marks, if this thread handles Synthesis- 
     * or RecognitionListener.
     */
    private final String assignment;
    
    /** This instances ServerSocket - clients will connect to this socket. */
    private ServerSocket server;
    
    /** Status of this thread. */
    private boolean running;
    
    /** HashMap of connected clients and their corresponding OutputStream. */
    private HashMap<Socket, ObjectOutputStream> clients;

    
    /**
     * Initialize this WorkerThread with the given port and assignment.
     * @param serverport
     *          The port to use.
     * @param thisAssignment
     *          The kind of external listeners this class will handle
     *          (e.g. "recognition").
     */
    public SocketExternalListenerWorker(final int serverport,
            final String thisAssignment) {
        port = serverport;
        assignment = thisAssignment;
    }
    
    /**
     * Start the ServerSocket and handle incoming connections.
     */
    @Override
    public final void run() {
        running = true;
        clients = new HashMap<Socket, ObjectOutputStream>();
        
        final int soTimeout = 300;
        
         //configure ServerSocket
         try {
             server = new ServerSocket(port);
             server.setReuseAddress(true);
             server.setSoTimeout(soTimeout);
         } catch (UnknownHostException e) {
             if (LOGGER.isEnabledFor(Level.ERROR)) {
                 LOGGER.error(e.getMessage(), e);
             }
             return;
         } catch (IOException e) {
             if (LOGGER.isEnabledFor(Level.ERROR)) {
                 LOGGER.error(e.getMessage(), e);
             }
             return;
         }
         
         //listen for incoming connections
         while (running) {
             try {
                 final Socket clientSocket = server.accept();
                 if (LOGGER.isInfoEnabled()) {
                     LOGGER.info("ExternalListener(" + assignment + "): "
                                    + "Connection accepted from "
                                    + clientSocket.getRemoteSocketAddress());
                 }
                 final OutputStream out = clientSocket.getOutputStream();
                 clients.put(clientSocket, 
                         new ObjectOutputStream(out));
             } catch (SocketTimeoutException to) {
                 if (LOGGER.isTraceEnabled()) {
                     LOGGER.trace("No connections within socket-timeout...");
                 }
             } catch (IOException e) {
                 if (running) {
                     //only log errors if ServerSocket should be running
                     if (LOGGER.isEnabledFor(Level.ERROR)) {
                         LOGGER.error("IOError", e);
                     }
                 }
            }
         }
         
         //not running anymore
         //close connections to external listeners
         if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("Disconnecting external " + assignment
                     + " listener ...");
         }
         for (Socket client : clients.keySet()) {
             try {
                client.close();
            } catch (IOException e) {
                // errors here can be ignored
            }
         }
         if (LOGGER.isDebugEnabled()) {
             LOGGER.debug("...disconnected external " + assignment
                     + " listener (" + clients.size() + " total)");
         }
    }
    
    /**
     * Writes the given message to all connected clients.<br>
     * Note: This method also sorts out 
     *          none responding/already disconnected clients
     * @param msg
     *          The message which will be sent to the clients
     * @since 0.7.5
     */
    protected final void postMessage(final String msg) {
        ObjectOutputStream oos;
        Iterator<Entry<Socket, ObjectOutputStream>> it = 
                clients.entrySet().iterator();
        
        while (it.hasNext()) {
            Entry<Socket, ObjectOutputStream> entry = it.next();
            try {
                oos = entry.getValue();
                oos.writeObject(msg);
            } catch (IOException e) {
                //sort out clients not responding/already disconnected
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("removed " + assignment + " client "
                                    + entry.getKey().getRemoteSocketAddress(),
                                    e);
                }
                it.remove();
            }
        }
        
    }
    
    /**
     * Stops this Workerthread.
     * The ServerSocket will be closed and all connected clients disconnected.
     * 
     * @since 0.7.5
     */
    public final void stopWorker() {
        running = false;
        try {
            server.close();
        } catch (IOException e) {
            //suppress (expected) errors when closing a connection
        }
    }
}
