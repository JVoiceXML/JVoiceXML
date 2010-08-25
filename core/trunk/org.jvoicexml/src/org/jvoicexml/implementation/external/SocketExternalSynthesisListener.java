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
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableSsmlText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.implementation.ExternalSynthesisListener;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.w3c.dom.Node;

/**
 * Class to send the synthesized output as String 
 * to a connected serversocket on localhost.
 * 
 * @author Josua Arndt
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public final class SocketExternalSynthesisListener
    extends Thread implements ExternalSynthesisListener {
    /** Logger instance. */
    private static final Logger LOGGER =
             Logger.getLogger(SocketExternalSynthesisListener.class);
     
    private Socket socket;
    private ObjectOutputStream oos;
    private int port;

    /**
     * Constructs a new SocketExternalSynthesisListener.
     */
    public SocketExternalSynthesisListener() {
    }

    /**
     * set the Port to be used and starts the the thread 
     * that contains the connection to TCP port.
     *
     * @param portnumber
     *            used port
     */
    public void setPort(final int portnumber){
        port = portnumber;
        start();
    }
   
    /**
     * Starts the TCP connection.
     * and initializes the ObjectOutputStream
     */
    @Override
    public void run() {
         try {               
             socket = new Socket("localhost", port);
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
            LOGGER.error(e.getMessage(), e);
            return;
         }

         LOGGER.info("Connect external synthesizer socket listener to port: "
                 + port + ".");
    }
    
    /**
     * {@inheritDoc}
     * Sends synthesized text as String.
     */
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {

        if (event.getEvent() == SynthesizedOutputEvent.OUTPUT_STARTED){ 
            
            SpeakableText speakable = ((OutputStartedEvent)event).getSpeakable();
            String text = null;
            
            if (speakable instanceof SpeakablePlainText) {
                text = ((SpeakablePlainText)speakable).getSpeakableText();
            }
            
            if (speakable instanceof SpeakableSsmlText) {
                SsmlDocument document = ((SpeakableSsmlText)speakable).getDocument();
                Speak speak = document.getSpeak();
                text = getConcatenatedText(speak);       
            }
                
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug( "Synthesis Listener sended Message:" + text.toString()+ "...");
            }
                    
             try {
                oos.writeObject("Synthesised: '" + text + "'.");
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }
       
    /**
     * Generates a formated String out of the 
     * contend of the Node and their childs.
     *
     * @param node
     *            XML Node 
     */
    public String getConcatenatedText(final Node node) {
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
            if (child.getFirstChild() != null) {
                LOGGER.info("get next child");
                str.append(getConcatenatedText(child));
            }
            child = child.getNextSibling();
        }  
        
        return str.toString();
    }

    @Override
    public void outputError(final ErrorEvent error) {
        // TODO Auto-generated method stub
        
    }
}
