/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/QueueEmptyEvent.java $
 * Version: $LastChangedRevision: 2694 $
 * Date:    $Date: 2011-06-03 04:28:55 -0500 (vie, 03 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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


/**
 * Notification that the output queue is empty.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2694 $
 * @since 0.7.1
 */
public final class QueueEmptyEvent extends SynthesizedOutputEvent {
    /**
     * Constructs a new object.
     * @param output object that caused the event.
     * @param sessionId the session id
     */
    public QueueEmptyEvent(final ObservableSynthesizedOutput output,
            final String sessionId) {
        super(output, SynthesizedOutputEvent.QUEUE_EMPTY, sessionId);
    }
}
