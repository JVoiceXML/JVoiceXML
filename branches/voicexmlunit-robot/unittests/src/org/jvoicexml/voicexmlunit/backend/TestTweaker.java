/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.voicexmlunit.backend;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.CharacterInput;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.mockito.Mockito;

/**
 *
 * @author raphael
 */
public class TestTweaker {
    final Tweaker tweaker;
    
    public TestTweaker() throws IOException {
        BasicConfigurator.configure(); // init console logger to debug spring
        tweaker = new Tweaker("../org.jvoicexml/config");
    }
    
    @Before
    public void setUp() {

    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void shouldLoadConfiguration() {        
        assertNotNull(tweaker.getConfiguration());
    }
    
    @Test
    public void shouldLoadFromConfig() 
            throws ConfigurationException {
        final Configuration config = tweaker.getConfiguration();
        assertNotNull(config.loadObject(DocumentServer.class));
    }
    
    @Test
    public void coreShouldStoreConfiguration() throws ConfigurationException {
        JVoiceXmlCore core = tweaker.mockCore();
        assertNotNull(core);
        Configuration configuration = tweaker.getConfiguration();
        assertSame(configuration, core.getConfiguration());
    }
    
    @Test
    public void platformShouldMock() throws JVoiceXMLEvent {
        ImplementationPlatform p = tweaker.mockPlatform();
        assertNotNull(p);
        assertNotNull(p.getSystemOutput());
    }
    
    @Test
    public void sessionShouldDeligateToCore() 
            throws ConfigurationException, JVoiceXMLEvent {
        JVoiceXmlCore core = tweaker.mockCore();
        ImplementationPlatform p = tweaker.mockPlatform();
        // prevent a possible NullPointer for DocumentServer
        final DocumentServer server = Mockito.mock(DocumentServer.class);
        Mockito.when(core.getDocumentServer()).thenReturn(server);
        JVoiceXmlSession session = new JVoiceXmlSession(p, core, null);
        try {
            session.call(null);
        } finally {
            Mockito.verify(core).getDocumentServer();
            //FIXME: verify platform constructor delegation (→ PowerMock)
            final CharacterInput input = Mockito.mock(CharacterInput.class);
            Mockito.when(p.getCharacterInput()).thenReturn(input);
            assertSame(input, session.getCharacterInput());
            Mockito.verify(p).getCharacterInput();
        }
        
    }
}
