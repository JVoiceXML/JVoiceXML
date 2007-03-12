package org.jvoicexml.callmanager;

import java.net.URI;
import java.util.Map;
import org.jvoicexml.callmanager.implementation.CallControlListener;

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
public interface CallControl {
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
    /**
     *
     * @param sourceUri URI
     */
    void play(URI sourceUri);
    /**
     *
     */
    void stopPlay();
    /**
     *
     * @param destinationUri URI
     */
    void record(URI destinationUri);
    /**
     *
     */
    void stopRecord();
    /**
     *
     * @param destinationPhoneUri URI
     */
    void tranfer(URI destinationPhoneUri);
    /**
     *
     * @param destinationPhoneUri URI
     * @param props Map
     */
    /** @todo */
    void tranfer(URI destinationPhoneUri, Map props);

}

