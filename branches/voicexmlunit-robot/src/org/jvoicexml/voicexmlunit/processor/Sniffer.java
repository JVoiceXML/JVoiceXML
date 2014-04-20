/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.processor;


import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.voicexmlunit.backend.Tweaker;
import org.jvoicexml.voicexmlunit.backend.Voice;

/**
 * Sniffer simulates a telephony conversation.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class Sniffer {
    
    final Voice voice;

    Throwable error;

    /**
     * Minimum of time in msec to wait for a session timeout/shutdown.
     */
    static long SESSION_TIMEOUT_MIN = 2500;

    /**
     * Constructs a new call
     *
     * @param configPath path to load configuration 
     */
    public Sniffer(final String configPath) {
        Voice currentVoice = null; // variable might not been initialized ...
        try {
            final Tweaker tweak = new Tweaker(configPath);
            currentVoice = new Voice(tweak);
        } catch (JVoiceXMLEvent|Exception ex) {
            Logger.getLogger(Sniffer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            voice = currentVoice;
        }
     }

    /**
     * Processes a document.
     * @param uri the document
     * @param timeout maximal msec, can not be lesser than a default value
     */
    public void process(final URI uri, final long timeout) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    voice.dial(uri);
                } catch (ErrorEvent ex) {
                    error = ex;
                } finally {
                    synchronized (uri) {
                        uri.notify();
                    }
                }
            }
        };
        final Thread t = new Thread(r);
        t.start();
        try {
            synchronized (uri) {
                uri.wait(timeout);
            }
        } catch (InterruptedException ex) {
            error = ex;
        }
    }
}
