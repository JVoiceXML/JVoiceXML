/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.voicexmlunit.backend;

import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.MockitoAnnotations.Mock;

/**
 *
 * @author raphael
 */
public class Tweaker {
    @Mock SystemOutput output;
    //@Mock UserInput input;
    //@Mock CharacterInput dtmf;
    @Mock ImplementationPlatform platform;
    @Mock JVoiceXmlCore jvxml;
    
    Configuration config;
    
    public Tweaker(final String path) {
        MockitoAnnotations.initMocks(this); // warning about suspection!
        System.setProperty("jvoicexml.config", path);
        config = new JVoiceXmlConfiguration();
        //config = Mockito.spy(config); // TODO: final class → PowerMock
    }
    
    public Configuration getConfiguration() {
        return config;
    }

    public JVoiceXmlCore mockCore() throws ConfigurationException {
        Mockito.when(jvxml.getConfiguration()).thenReturn(config);

        final DocumentServer server = config.loadObject(DocumentServer.class);
        Mockito.when(jvxml.getDocumentServer()).thenReturn(server);
        final GrammarProcessor proc = config.loadObject(GrammarProcessor.class);
        if (proc != null) {
            proc.init(config);
            Mockito.when(jvxml.getGrammarProcessor()).thenReturn(proc);
        }
        return jvxml;
    }
    
    public ImplementationPlatform mockPlatform() 
            throws ConnectionDisconnectHangupEvent, NoresourceError {
        Mockito.when(platform.getSystemOutput()).thenReturn(output);
        //Mockito.when(platform.hasUserInput()).thenReturn(true);
        //Mockito.when(platform.getUserInput()).thenReturn(input);
        //Mockito.when(platform.getCharacterInput()).thenReturn(dtmf);
        return platform;
    }
}
