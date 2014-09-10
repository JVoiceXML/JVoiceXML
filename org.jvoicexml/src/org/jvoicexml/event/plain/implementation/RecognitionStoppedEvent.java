/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/event/plain/implementation/OutputStartedEvent.java $
 * Version: $LastChangedRevision: 4233 $
 * Date:    $Date: 2014-09-02 09:14:31 +0200 (Tue, 02 Sep 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.event.plain.implementation;

import org.jvoicexml.implementation.SpokenInput;

/**
 * Notification that the speech recognition has stopped.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4233 $
 * @since 0.7.7
 */
@SuppressWarnings("serial")
public final class RecognitionStoppedEvent extends SpokenInputEvent {
    /** The unsupported element. */
    public static final String DETAIL = "stop";

    /** The detail message. */
    public static final String EVENT_TYPE = SpokenInputEvent.class
            .getCanonicalName() + "." + DETAIL;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized
     * 
     * <p>
     * The {@link #DETAIL} is used to construct the event type.
     * </p>
     * 
     * @see #getEventType()
     * 
     * @param input
     *            object that caused the event.
     * @param sessionId
     *            the session id
     */
    public RecognitionStoppedEvent(final SpokenInput input,
            final String sessionId) {
        super(input, DETAIL, sessionId);
    }
}
