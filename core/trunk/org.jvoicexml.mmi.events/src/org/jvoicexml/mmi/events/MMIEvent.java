/**
 * 
 */
package org.jvoicexml.mmi.events;

/**
 * Base class for all MMI events.
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 *
 */
public class MMIEvent {
    /** Source issuing this event. */
    private Object source;

    /**
     * Retrieves the source issuing this event.
     * @return source.
     */
    public Object getSource() {
        return source;
    }

    /**
     * Sets the source issuing this event.
     * @param eventSource the source
     */
    public void setSource(final Object eventSource) {
        source = eventSource;
    }

    
}
