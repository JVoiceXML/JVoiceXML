/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/JVoiceXmlStub.java $
 * Version: $LastChangedRevision: 2430 $
 * Date:    $Date: 2010-12-21 09:21:06 +0100 (Di, 21 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit.processor;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.JVoiceXmlMain;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.JVoiceXmlDocumentServer;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.grammar.JVoiceXmlGrammarProcessor;

/**
 * Voice provides direct access to JVoiceXML via GenericClient.
 * @author Raphael Groner
 * @author Dirk Schnelle-Walka
 */
public final class Voice implements JVoiceXmlCore {

    private final JVoiceXmlDocumentServer documentServer;
    private final JVoiceXmlGrammarProcessor grammarProcessor;
    
    private Dialog dialog;
    private JVoiceXmlSession session;
    
    private final Long timeout;
    private final Object lock;
    
     
    /**
     * Constructor.
     * Simplest initialization.
     *  
     * @param timeout maximum of time (msec) to wait for fine session shutdown
     */
    public Voice(final long t) {
        documentServer = new JVoiceXmlDocumentServer();
        grammarProcessor = new JVoiceXmlGrammarProcessor();
        dialog = null;
        session = null;
        timeout = t;
        lock = new Object();
    }
    
    public Dialog getDialog(final URI uri) throws JVoiceXMLEvent {
        if (dialog == null) {
            dialog = new Dialog();
        }
        // is there a new uri?
        if (0 == uri.compareTo(dialog.get())) {
            if (dialog.getPlatform() != null) {
                dialog.finish();
                dialog.set(uri);
            }
        }
        return dialog;
    }
    
    public Session getSession() {
        return session;
    }
    
    /**
     * Dials to an internally created JVoiceXML sub application.
     * It creates a valid session objects and connects it to JVoiceXML engine.
     * Then, the internal dialog will be executed in a separate thread,
     * so this function won't block till the session ends. 
     * Session termination must be handled outside of this function.
     */
    public void dial() {
        try {
            if (session != null) {
                shutdown();
            }
            session = (JVoiceXmlSession) this.createSession(null);
            session.addSessionListener(dialog);
            session.call(dialog.get());
        } catch (JVoiceXMLEvent ex) {
            shutdown();
        }
    }
    
    public boolean isDialing() {
        return (session != null) && session.isAlive() && !session.hasEnded();
    }

    @Override
    public DocumentServer getDocumentServer() {
        return documentServer;
    }

    @Override
    public GrammarProcessor getGrammarProcessor() {
        return grammarProcessor;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }

    @Override
    public String getVersion() {
        final JVoiceXmlMain main = new JVoiceXmlMain();
        return main.getVersion();
    }

    @Override
    public Session createSession(ConnectionInformation info) 
            throws ErrorEvent {
        try {
            return (Session) new JVoiceXmlSession(dialog.getPlatform(), 
                    this, info);
        } catch (JVoiceXMLEvent ex) {
            throw new NoresourceError(ex);
        }
    }

    @Override
    public void shutdown() {
        if (session != null) {
            // wait till session ends or terminate it after the timeout
            try {
                final Runnable terminator; 
                terminator = new Runnable() {
                    @Override
                    public void run() {
                        session.hangup();
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                };
                new Thread(terminator).start();                        
                synchronized (lock) {
                    lock.wait(timeout);
                }
            } catch (InterruptedException ex) {
            } finally {
                session = null;
            }
        }
    }
}
