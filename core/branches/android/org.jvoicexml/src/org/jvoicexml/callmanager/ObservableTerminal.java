/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;


/**
 * A {@link org.jvoicexml.callmanager.Terminal} that can be monitored by
 * {@link TerminalListener}s.
 *
 * <p>
 * Implementations must implement this interface to propagate input events
 * to the {@link org.jvoicexml.CallManager}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public interface ObservableTerminal {
    /**
     * Adds a listener for terminal events.
     *
     * <p>
     * The implementation of this interface must notify the listener
     * about all events.
     * </p>
     *
     * @param listener The listener.
     */
    void addListener(final TerminalListener listener);

    /**
     * Removes a listener for terminal events.
     *
     * @param listener The listener.
     */
    void removeListener(final TerminalListener listener);
}
