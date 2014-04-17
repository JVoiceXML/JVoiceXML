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
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Session;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;

/**
 * Voice provides direct access to JVoiceXML via some Mock.
 * @author Raphael Groner
 */
public final class Voice {
    
    @Mock SystemOutput output;
    //@Mock UserInput input;
    //@Mock CharacterInput dtmf;
    @Mock ImplementationPlatform platform;
    @Mock JVoiceXmlCore jvxml;
    
    final Configuration config;
    Session session;

    public Voice() throws JVoiceXMLEvent, ConfigurationException {
        MockitoAnnotations.initMocks(this); // warning about suspection!
        Mockito.when(platform.getSystemOutput()).thenReturn(output);
        //Mockito.when(platform.hasUserInput()).thenReturn(true);
        //Mockito.when(platform.getUserInput()).thenReturn(input);
        //Mockito.when(platform.getCharacterInput()).thenReturn(dtmf);

        config = Mockito.spy(new JVoiceXmlConfiguration());
        Mockito.when(jvxml.getConfiguration()).thenReturn(config);
        

        final DocumentServer server = config.loadObject(DocumentServer.class);
        Mockito.when(jvxml.getDocumentServer()).thenReturn(server);
        final GrammarProcessor proc = config.loadObject(GrammarProcessor.class);
        proc.init(config);
        Mockito.when(jvxml.getGrammarProcessor()).thenReturn(proc);
    }
    
    public Session dial(final URI uri) throws ErrorEvent {
        session = Mockito.spy(new JVoiceXmlSession(platform, jvxml, null));
        session.call(uri);
        return session;
     }
    
    public Session getSession() {
        return session;
    }
    
    public void shutdown() {
        if (session != null && !session.hasEnded()) {
            session.hangup();
        }
        session = null;
    }
}