/*
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

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;

/**
 * Retrieves extension information from received extension notifications.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public interface ExtensionNotificationDataExtractor {
    /**
     * Retrieves an external recognition result that should be fed into a
     * running VoiceXML session.
     * 
     * @param mmi
     *            received mmi event
     * @param ext
     *            received extension notification
     * @return retrieved recognition result. {@link MMIRecognitionResult} may be
     *         used as a container for the {@link RecognitionResult}
     * @throws ConversionException
     *             error extracting the {@link RecognitionResult}
     */
    RecognitionResult getRecognitionResult(Mmi mmi,
            ExtensionNotification ext) throws ConversionException;
}
