package org.jvoicexml.processor.vxml3.event.internal;

import org.w3c.dom.events.Event;

/**
 * The VoiceXML 3.0 Event interface extends the DOM Level 3 Event interface to
 * support voice specific event information.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1662 $
 * @updated 07-Jun-2011 08:39:03
 */
public interface VoiceXmlEvent extends Event {

    /**
     * Retrieves the number of times a resources emits a particular event type.
     * 
     * @return count for the event type
     */
    int getCount();

}