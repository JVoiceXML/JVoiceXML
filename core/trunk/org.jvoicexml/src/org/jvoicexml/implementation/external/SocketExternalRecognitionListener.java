/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.implementation.ExternalRecognitionListener;
import org.w3c.dom.Node;

/**
 * Class to send the Recognized Result as String 
 * to a connected server socket on localhost.
 * 
 * @author Josua Arndt
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class SocketExternalRecognitionListener
    extends Thread implements ExternalRecognitionListener {
    /** Logger instance. */
    private static final Logger LOGGER =
             Logger.getLogger(SocketExternalRecognitionListener.class);
    
    private ServerSocket server;
    private Socket socket;
    private ObjectOutputStream oos;
    private int port;

    /**
     * Constructs a new SocketExternalSynthesisListener.
     */
    public SocketExternalRecognitionListener() {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Create externalRecognizerSocketListener");  
        }
    }

    /**
     * Set the Port to be used and starts the the thread 
     * that contains the connection to TCP port.
     *
     * @param portnumber
     *            used port
     */
    public void setPort(final int portnumber) {
        port = portnumber;
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Set externalRecognizerSocketListener to port: "
                    + port + ".");  
        }
        
        start();       
        if(LOGGER.isInfoEnabled()){
            LOGGER.info("Start externalRecognizerSocketListener");  
        }
    }
   
    /**
     * Starts the TCP connection 
     * and initializes the ObjectOutputStream
     */
    @Override
    public void run() {
         try {               
             server = new ServerSocket(port);
             socket = server.accept();
         } catch (UnknownHostException e) {
             LOGGER.error(e.getMessage(), e);
             return;
         } catch (IOException e) {
             LOGGER.error(e.getMessage(), e);
             return;
         }
     
         try {
             oos = new ObjectOutputStream(socket.getOutputStream());
         } catch (IOException e) {            
            LOGGER.error(e.getMessage());
         }
         
         LOGGER.info("Connect external recognizer socket listener to port: "
                 + port + ".");
    }   
       
    public final String getConcatenatedText(final Node node) {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            return node.getNodeValue();
        }
        
        StringBuilder str = new StringBuilder();
        Node child = node.getFirstChild(); 
        
        while (child != null) {
            final short type = child.getNodeType();
            if ((type == Node.TEXT_NODE) || (type == Node.CDATA_SECTION_NODE)) {
                str.append(child.getNodeValue());
                str.append(" ");
            }
            if (child.getFirstChild() != null){
                str.append(getConcatenatedText(child));
            }
            child = child.getNextSibling();
        }  
        
        return str.toString();
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
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Recognition Listener sended Message:"
                    + text.toString() + "...");
        }
                
         try {
            oos.writeObject("Recognized: '" + text.toString() + "'.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } 
        
    }

    /**
     * {@inheritDoc}
     * Sends "Recognition Listener: result rejected." text as String.
     */
    @Override
    public void resultRejected(final RecognitionResult result) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Rocognition Listener: result rejected.");
        }

         try {
            oos.writeObject("Rocognition Listener: result rejected.");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        } 
    }
}
