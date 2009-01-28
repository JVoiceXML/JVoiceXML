/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation;

/**
 * Listener for events from the {@link Telephony} implementation.
 * implementation.
 *
 * @author Hugo Monteiro
 * @author Dirk Schnelle
 * @author Renato
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public interface TelephonyListener {
    /**
     * Invoked when the {@link Telephony} implementation triggered a
     * call answered event.
     * @param event the event.
     */
    void telephonyCallAnswered(final TelephonyEvent event);

    /**
     * Invoked when the {@link Telephony} implementation triggered an event.
     * @param event the event.
     */
    void telephonyMediaEvent(final TelephonyEvent event);

    /**
     * Invoked when the {@link Telephony} implementation triggered a
     * call hangup event.
     * @param event the event.
     */
    void telephonyCallHungup(final TelephonyEvent event);
}
