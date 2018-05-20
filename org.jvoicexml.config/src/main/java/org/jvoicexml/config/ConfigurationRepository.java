package org.jvoicexml.config;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface ConfigurationRepository {
    byte[] getConfigurationFile(File file);

    Collection<File> getConfigurationFiles(String root)
        throws IOException;
}
