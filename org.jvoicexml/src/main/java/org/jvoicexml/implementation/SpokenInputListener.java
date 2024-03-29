/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.plain.implementation.SpokenInputEvent;


/**
 * Listener for events from the {@link SpokenInput} implementation.
 *
 * @author Dirk Schnelle-Walka
 * @see org.jvoicexml.UserInput
 * @since 0.5
 */
public interface SpokenInputListener {
    /**
     * Notification about status changes in the {@link SpokenInput}.
     * @param event the input event..
     * @since 0.6
     */
    void inputStatusChanged(SpokenInputEvent event);

    /**
     * An error occurred while an output processes an input.
     * <p>
     * This method is intended to feed back errors that happen while the
     * {@link org.jvoicexml.UserInput} processes an input asynchronously.
     * </p>
     * @param error the error
     * @since 0.7.4
     */
    void inputError(ErrorEvent error);
    
    /**
     * Notification that the user did not provide input in the specified
     * time.
     * 
     * @param timeout the timeout
     * 
     * @since 0.7.9
     */
    void timeout(long timeout);
}
