/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/SynthesizedOutputListener.java $
 * Version: $LastChangedRevision: 2828 $
 * Date:    $Date: 2011-09-22 13:00:25 -0500 (jue, 22 sep 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * Listener for events from the {@link SynthesizedOutput} implementation.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2828 $
 *
 * @see org.jvoicexml.SystemOutput
 * @since 0.5
 */
public interface SynthesizedOutputListener {
    /**
     * Notification about status changes in the {@link SynthesizedOutput}.
     * @param event the output event.
     * @since 0.6
     */
    void outputStatusChanged(final SynthesizedOutputEvent event);

    /**
     * An error occurred while an output processes an output.
     * <p>
     * This method is intended to feed back errors that happen while the
     * {@link org.jvoicexml.SystemOutput} processes an output asynchronously.
     * Errors that happen while the output is queued should be reported
     * by throwing an appropriate error in the
     * {@link org.jvoicexml.SystemOutput#queueSpeakable(org.jvoicexml.SpeakableText, String, org.jvoicexml.DocumentServer)}
     * method.
     * </p>
     * @param error the error
     * @since 0.7.4
     */
    void outputError(final ErrorEvent error);
}
