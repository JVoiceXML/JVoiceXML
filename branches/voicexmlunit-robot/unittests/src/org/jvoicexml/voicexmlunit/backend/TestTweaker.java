/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jvoicexml.voicexmlunit.backend;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.config.JVoiceXmlConfiguration;

/**
 *
 * @author raphael
 */
public class TestTweaker {
    final Tweaker tweaker;
    
    public TestTweaker() {
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
    public void shouldLoadDocumentServerFromConfig() 
            throws ConfigurationException {
        final Configuration config = tweaker.getConfiguration();
        assertNotNull(config.loadObject(DocumentServer.class));
    }
}
