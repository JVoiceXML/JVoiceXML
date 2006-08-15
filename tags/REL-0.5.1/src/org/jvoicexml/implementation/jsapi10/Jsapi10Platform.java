/*
 * File:    $RCSfile: Jsapi10Platform.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/15 09:36:24 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10;

import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.Platform;


/**
 * Objects that implement this interface manage all resources, need for TTS and
 * voice recognition via JSAPI 1.0.
 *
 * <p>
 * The <code>ImplementationPlatform</code> instantiates the implementing
 * class and asks this instance for resources like TTS and recognition.
 * The platform does not need to support all resources. If the interpreter
 * detects a tag that addresses the missing resource, an
 * <code>error.noresource</code> event is thrown.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @see org.jvoicexml.implementation.ImplementationPlatform
 */
public interface Jsapi10Platform
        extends Platform, ExternalResource {
    /**
     * Retrieves the TTS engine provided by this platform.
     * @return The <code>TTSEngine</code> to use, <code>null</code> if this
     * platform does not support TTS.
     */
    TTSEngine getTTSEngine();

    /**
     * Retrieves the recognition engine provided by this platform.
     * @return The <code>RecognitionEngine</code> to use, <code>null</code>
     * if this platform does not support recognition.
     */
    RecognitionEngine getRecognitionEngine();
}
