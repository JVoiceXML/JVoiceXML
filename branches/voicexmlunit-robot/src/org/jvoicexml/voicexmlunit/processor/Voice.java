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
import java.net.URISyntaxException;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
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

    private final Dialog dialog;
    private final JVoiceXmlDocumentServer documentServer;
    private final JVoiceXmlGrammarProcessor grammarProcessor;
    
    private JVoiceXmlSession session;
    
    /**
     *
     * @param uri
     * @throws JVoiceXMLEvent
     */
    public Voice(final URI uri) {
        dialog = new Dialog(uri);
        documentServer = new JVoiceXmlDocumentServer();
        grammarProcessor = new JVoiceXmlGrammarProcessor();
        session = null;
    }
    
    public Session getSession() {
        return session;
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
    public Session createSession(ConnectionInformation info) throws ErrorEvent {
        try {
            ImplementationPlatform platform = dialog.getPlatform();
            session = new JVoiceXmlSession(platform, this, info);
            session.addSessionListener(dialog);
            session.call(dialog.getURI());
        } catch (JVoiceXMLEvent ex) {
            throw new NoresourceError(ex);
        }
        return session;
    }

    @Override
    public void shutdown() {
        if (session != null) {
            session.hangup();
            session = null;
        }
    }

    void fail(ErrorEvent ex) {
        
    }
}
