package org.jvoicexml.config;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jvxml.DesktopTelephonySupportFactory;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestJVoiceXmlConfigurationFromClasspath {

    private static final String APPLICATION_CONTEXT = "classpath-config/jvoicexml.xml";
    private static final String CONFIG_REPOSITORY_PATH = "classpath-config/";
    private static final List<String> REPOSITORY_FILES = Collections.singletonList(CONFIG_REPOSITORY_PATH + "test-implementation.xml");

    @Test
    public void testGetConfigurationFiles() throws Exception {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration(APPLICATION_CONTEXT, REPOSITORY_FILES);
        final Collection<File> files = config.getConfigurationFiles("implementation");
        assertEquals(1, files.size());
    }

    @Test
    public void testLoadObjects() throws Exception {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration(APPLICATION_CONTEXT, REPOSITORY_FILES);
        final Collection<ResourceFactory> factories = config.loadObjects(ResourceFactory.class, "implementation");
        Assert.assertEquals(1, factories.size());
        final ResourceFactory factory = factories.iterator().next();
        Assert.assertEquals(DesktopTelephonySupportFactory.class, factory.getClass());
    }

}