/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation;

import org.jvoicexml.SpeakableText;

/**
 * Notification that the output of a {@link SpeakableText} has
 * started.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.1
 */
public final class OutputStartedEvent extends SynthesizedOutputEvent {
    /** The speakable. */
    private final SpeakableText speakable;

    /**
     * Constructs a new object.
     * @param output object that caused the event.
     * @param speakableText the speakable that has started
     */
    public OutputStartedEvent(final ObservableSynthesizedOutput output,
            final SpeakableText speakableText) {
        super(output, SynthesizedOutputEvent.OUTPUT_STARTED);
        speakable = speakableText;
    }

    /**
     * Retrieves the speakable.
     * @return the speakable
     */
    public SpeakableText getSpeakable() {
        return speakable;
    }
}
