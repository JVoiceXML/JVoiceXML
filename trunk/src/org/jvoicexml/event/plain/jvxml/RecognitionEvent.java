/*
 * File:    $RCSfile: RecognitionEvent.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.plain.PlainEvent;
import org.jvoicexml.implementation.RecognitionResult;

/**
 * The user has responded within the timeout interval.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public class RecognitionEvent
        extends PlainEvent {
    /** The serial version UID. */
    private static final long serialVersionUID = 7484790388252735175L;

    /** The detail message. */
    public static final String EVENT_TYPE = RecognitionEvent.class.getName();

    /** The result of the recognition process. */
    private final RecognitionResult result;

    /**
     * Constructs a new event with the event type as its detail message. The
     * cause is not initialized.
     *
     * @param recognitionResult
     *        Result of the recognition process.
     */
    public RecognitionEvent(final RecognitionResult recognitionResult) {
        super();

        result = recognitionResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * Retrieves the resultof the recognition process.
     *
     * @return RecognitionResult
     */
    public final RecognitionResult getRecognitionResult() {
        return result;
    }
}
