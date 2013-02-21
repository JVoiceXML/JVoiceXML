package org.jvoicexml.callmanager;

import java.net.URI;
import java.util.Map;

/**
 *
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: L2f,Inesc-id</p>
 *
 * @author
 * @version 1.0
 */
public interface ObservableCallControl {
    /**
     *
     * @param listener CallControlListener
     */
    void addListener(CallControlListener listener);
    /**
     *
     * @param listener CallControlListener
     */
    void removeListener(CallControlListener listener);
}

