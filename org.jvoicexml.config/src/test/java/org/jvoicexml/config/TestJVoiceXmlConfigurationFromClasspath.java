package org.jvoicexml.config;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.jvxml.DesktopTelephonySupportFactory;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;

public class TestJVoiceXmlConfigurationFromClasspath {

    private static final String APPLICATION_CONTEXT = "classpath-config/jvoicexml.xml";
    private static final String CONFIG_REPOSITORY_PATH = "classpath-config/";
    private static File classpathRepository;

    @BeforeClass
    public static void init() {
        URL repositoryUrl = TestJVoiceXmlConfigurationFromClasspath.class.getClassLoader().getResource(CONFIG_REPOSITORY_PATH);
        classpathRepository = new File(repositoryUrl.getFile());
    }


    @Test
    public void testGetConfigurationFiles() throws Exception {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration(APPLICATION_CONTEXT, classpathRepository);
        final Collection<File> files = config.getConfigurationFiles("implementation");
        assertEquals(1, files.size());
    }

    @Test
    public void testLoadObjects() throws Exception {
        JVoiceXmlConfiguration config = new JVoiceXmlConfiguration(APPLICATION_CONTEXT, classpathRepository);
        final Collection<ResourceFactory> factories = config.loadObjects(ResourceFactory.class, "implementation");
        Assert.assertEquals(1, factories.size());
        final ResourceFactory factory = factories.iterator().next();
        Assert.assertEquals(DesktopTelephonySupportFactory.class, factory.getClass());
    }

}