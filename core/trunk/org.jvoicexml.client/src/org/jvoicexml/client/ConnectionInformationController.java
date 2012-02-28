package org.jvoicexml.client;

import org.jvoicexml.ConnectionInformation;

/**
 * Provides extended functionality to a {@link ConnectionInformation} object,
 * such as lifecycle management through the {@link #cleanup()} method.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.6
 */
public interface ConnectionInformationController {

    /**
     * Retrieves the encapsulated connection info.
     * @return the connection info object
     */
    ConnectionInformation getConnectionInformation();

    /**
     * Performs some additional cleanup.
     */
    void cleanup();
}
