/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
    implements ExternalSynthesisListener {
    /** Logger instance. */
    private static final Logger LOGGER =
             Logger.getLogger(SocketExternalSynthesisListener.class);
    
    /** the port to be listening on. */
    private int port;
    
    /** internal Thread handling all connections. */
    private SocketExternalListenerWorker worker;
    
    /**
     * "synthesis" - The workerthread will use this string to mark log messages.
     */
    private static final String ASSIGNMENT = "synthesis";
    
    /** Status of this listener. */
    private boolean running = false;

    /**
     * Constructs a new SocketExternalSynthesisListener.
     */
    public SocketExternalSynthesisListener() {
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
     * {@inheritDoc}
     * Sends synthesized text as String.
     */
    @Override
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        if (event.getEvent() == SynthesizedOutputEvent.OUTPUT_STARTED) { 
            
            SpeakableText speakable =
                ((OutputStartedEvent) event).getSpeakable();
            String text = null;
            
            if (speakable instanceof SpeakableSsmlText) {
                SsmlDocument document =
                    ((SpeakableSsmlText) speakable).getDocument();
                Speak speak = document.getSpeak();
                text = getConcatenatedText(speak);
            }

            if (running) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Synthesis Listener sent Message:"
                            + text.toString() + "...");
                }
                worker.postMessage("Synthesised: '" + text + "'.");
            }
        }
    }
       
    /**
     * Generates a formated String out of the 
     * contents of the Node and their childs.
     *
     * @param node
     *            XML Node
     * @return the node contents as a string 
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
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        //if the workerthread has not been stopped, stop it
        if (worker != null) {
            worker.stopWorker();
        }
        
        worker = new SocketExternalListenerWorker(port, ASSIGNMENT);
        worker.start();
        running = true;
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("started socket external synthesis listener at port '"
                    + port + "...");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        running = false;
        if (worker != null) {
            worker.stopWorker();
            worker = null;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...stopped socket external synthesis listener");
        }
    }
}
