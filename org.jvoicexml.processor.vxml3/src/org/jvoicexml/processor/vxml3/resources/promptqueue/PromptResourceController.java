package org.jvoicexml.processor.vxml3.resources.promptqueue;

import org.jvoicexml.processor.vxml3.resources.ResouceController;

/**
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 07-Jun-2011 08:11:25
 */
public interface PromptResourceController extends ResouceController {

    /**
     * Sent at start of prompt play and whenever a new prompt or fetch audio is
     * played whose bargeinType differs from the preceding one.
     * 
     * @param type
     */
    void bargeintypeChange(Object type);

    /**
     * Indicates prompt queue has played to completion and is now empty.
     * 
     * @param markName
     *            name of a potentially reached mark
     * @param markTime
     *            the time when the mark was observed.
     */
    void promptDone(String markName, long markTime);
}