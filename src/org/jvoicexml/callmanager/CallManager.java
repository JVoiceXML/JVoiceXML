package org.jvoicexml.callmanager;

import java.net.URI;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Inesc-id</p>
 *
 * @author
 * @version 1.0
 */
public interface CallManager /*extends ExternalResource*/ {

    /**
     *
     */
    void start();
    /**
     *
     */
    void stop();

    /**
     *
     * @param application URI
     * @param terminal String
     * @return boolean
     */
    boolean addTerminal(URI application, String terminal);

}
