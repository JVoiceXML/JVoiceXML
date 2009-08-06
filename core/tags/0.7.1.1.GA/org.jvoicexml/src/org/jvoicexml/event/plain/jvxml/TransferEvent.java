/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/org.jvoicexml/src/org/jvoicexml/event/plain/jvxml/RecognitionEvent.java $
 * Version: $LastChangedRevision: 755 $
 * Date:    $Date: 2008-03-27 21:33:02 +0100 (Do, 27 Mrz 2008) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.event.plain.jvxml;


/**
 * The call has been successfully transferred.
 *
 * @author Dirk Schnelle
 * @version $Revision: 755 $
 * @since 0.7
 */
@SuppressWarnings("serial")
public final class TransferEvent
        extends AbstractInputEvent {
    /** The detail message. */
    public static final String EVENT_TYPE = TransferEvent.class.getName();

    /** Destination of the transfer. */
    private final String destination;

    /** Result of the call. */
    private final String result;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     * @param uri destination of the transfer.
     * @param callResult result of the call.
     */
    public TransferEvent(final String uri, final String callResult) {
        destination = uri;
        result = callResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the destination of the transfer.
     * @return destination of the transfer.
     */
    public String getDestination() {
        return destination;
    }

    /**
     * {@inheritDoc}
     */
    public Object getInputResult() {
        return result;
    }
}
