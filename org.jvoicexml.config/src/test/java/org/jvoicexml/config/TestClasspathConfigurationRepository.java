package org.jvoicexml.config;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test cases for {@link ClasspathConfigurationRepository}.
 *
 * @author Stuart 'Stevie' Leitch
 * @since 0.7.8
 */
public class TestClasspathConfigurationRepository {

    /**
     * Location of test repository files
     */
    private static final String CONFIG_REPOSITORY_PATH = "classpath-config/";

    /**
     * List of repository files
     */
    private static final List<String> REPOSITORY_FILES = Arrays.asList(
            CONFIG_REPOSITORY_PATH + "test-implementation.wrongextension",
            CONFIG_REPOSITORY_PATH + "test-implementation.xml",
            CONFIG_REPOSITORY_PATH + "test-wrongroot.xml"
    );
    private static File classpathRepository;


    @BeforeClass
    public static void init() {
        URL repositoryUrl = TestJVoiceXmlConfigurationFromClasspath.class.getClassLoader().getResource(CONFIG_REPOSITORY_PATH);
        classpathRepository = new File(repositoryUrl.getFile());
    }

    @Test
    public void testGetConfigurationFile() throws IOException {
        final File fileInRepository = new File(classpathRepository, "test-implementation.xml");
        ConfigurationRepository repository = new ClasspathConfigurationRepository(REPOSITORY_FILES);
        byte[] fileContent = repository.getConfigurationFile(fileInRepository);
        assertNotNull("File is not found", fileContent);
        assertTrue("File is empty", fileContent.length > 0);
    }

    @Test
    public void testGetConfigurationFiles() throws IOException {
        ConfigurationRepository repository = new ClasspathConfigurationRepository(REPOSITORY_FILES);
        Collection<File> configurationFiles = repository.getConfigurationFiles("implementation");
        assertNotNull("Null configuration file list", configurationFiles);
        assertFalse("No configuration files", configurationFiles.isEmpty());
    }

    @Test
    public void testGetConfigurationFilesExcludesNonXmlFiles() throws Exception {
        ConfigurationRepository repository = new ClasspathConfigurationRepository(REPOSITORY_FILES);
        Collection<File> configurationFiles = repository.getConfigurationFiles("implementation");
        assertNotNull("Null configuration file list", configurationFiles);
        for (File configurationFile : configurationFiles) {
            assertTrue(
                    "Configuration file has invalid extension: " + configurationFile.getName(),
                    configurationFile.getName().endsWith(".xml"));
        }
    }

    @Test
    public void testGetConfigurationFilesExcludesUnmatchedRoot() throws IOException {
        ConfigurationRepository repository = new ClasspathConfigurationRepository(REPOSITORY_FILES);
        Collection<File> configurationFiles = repository.getConfigurationFiles("implementation");
        assertFalse(configurationFiles.isEmpty());
        for (File configurationFile : configurationFiles) {
            assertFalse(
                    "Configuration file has wrong root element: "  + configurationFile,
                    configurationFile.getName().contains("wrongroot"));
        }
    }

    /**
     * Verify realistic usage of repository: get list of files then load them.
     */
    @Test
    public void testLoadAllConfigurationFiles() throws IOException {
        ConfigurationRepository repository = new ClasspathConfigurationRepository(REPOSITORY_FILES);
        Collection<File> configurationFiles = repository.getConfigurationFiles("implementation");
        assertFalse(configurationFiles.isEmpty());
        for (File configurationFile : configurationFiles) {
            assertTrue(repository.getConfigurationFile(configurationFile).length > 0);
        }
    }
}