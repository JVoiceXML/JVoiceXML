/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

import java.util.List;

import org.jvoicexml.LastResult;
import org.jvoicexml.event.plain.implementation.SynthesizedOutputEvent;
import org.jvoicexml.event.plain.jvxml.RecognitionEvent;

/**
 * Converts data that is sent as extensions notification into a format that
 * allows for integrqation into MMI Extension notifications.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public interface ExtensionNotificationDataConverter {
    /**
     * Converts the last result from the corresponding shadow variable into a
     * something that can be placed in the status field of the
     * {@link org.jvoicexml.mmi.events.DoneNotification}.
     * 
     * @param lastresult
     *            the last result
     * @return converted last result
     * @throws ConversionException
     *             error converting the result
     */
    Object convertApplicationLastResult(final List<LastResult> lastresult)
            throws ConversionException;

    /**
     * Converts the given synthesized output event into the data field of an
     * {@link org.jvoicexml.mmi.events.ExtensionNotification}.
     * 
     * @param event
     *            the event
     * @return converted last result
     * @throws ConversionException
     *             error converting the result
     */
    Object convertSynthesizedOutputEvent(final SynthesizedOutputEvent event)
            throws ConversionException;

    /**
     * Converts the given recognition event into the data field of an
     * {@link org.jvoicexml.mmi.events.ExtensionNotification}.
     * 
     * @param event
     *            the event
     * @return converted last result
     * @throws ConversionException
     *             error converting the result
     */
    Object convertRecognitionEvent(final RecognitionEvent event)
            throws ConversionException;
}
