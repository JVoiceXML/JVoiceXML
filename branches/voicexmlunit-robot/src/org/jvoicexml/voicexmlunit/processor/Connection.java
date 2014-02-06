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


import junit.framework.Assert;
import org.jvoicexml.Session;
import org.jvoicexml.event.ErrorEvent;

/**
 * Connection simulates a telephony conversation.
 *
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 *
 */
public final class Connection implements Runnable {
    
    private Voice voice;
    private ErrorEvent error;

    /**
     * Maximum of time in msec to wait for a session timeout/shutdown.
     */
    public static long SESSION_TIMEOUT = 2500;
    
    /**
     * Constructs a new call
     *
     * @param 
     */
    public Connection(final Voice voice) {
        this.voice = voice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        if (voice == null) {
            return;
        }

        voice.dial();
        final Session session = voice.getSession();
        Assert.assertNotNull("no dialer session available!", session);
        try {
            //final Session session = voice.createSession(null);
            session.waitSessionEnd();
        } catch (ErrorEvent ex) {
            
        } finally {
            voice.shutdown();
        }
    }
}
