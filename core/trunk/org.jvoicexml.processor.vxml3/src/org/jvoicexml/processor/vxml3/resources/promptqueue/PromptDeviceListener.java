package org.jvoicexml.processor.vxml3.resources.promptqueue;

/**
 * A listener to events from the prompt device.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 07-Jun-2011 08:38:44
 */
public interface PromptDeviceListener {
    /**
     * Sent whenever a single prompt or piece of fetch audio finishes playing.
     */
    void playerDone();

}