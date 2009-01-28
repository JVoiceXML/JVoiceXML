package org.jvoicexml.callmanager;

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
public interface CallControlListener {
    /**
     *
     */
    void answered();
    /**
     *
     */
    void hangedup();
    /**
     *
     */
    void playStarted();
    /**
     *
     */
    void playStopped();
    /**
     *
     */
    void recordStarted();
    /**
     *
     */
    void recordStopped();

}
