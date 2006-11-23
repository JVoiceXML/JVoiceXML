/*
 * File:    $RCSfile: AbstractJsap10Platform.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2006/07/17 14:08:23 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.SpokenInput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Basic JSAPI 1.0 platform.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public abstract class AbstractJsap10Platform
        implements Jsapi10Platform {
    /**
     * Constructs a new object.
     */
    public AbstractJsap10Platform() {
    }

    /**
     * {@inheritDoc}
     */
    public final void open()
            throws NoresourceError {
        final TTSEngine tts = getTTSEngine();
        if (tts != null) {
            tts.registerEngines();

            final SystemOutput output = getSystemOutput();
            if (output != null) {
                output.open();
            }
        }

        final RecognitionEngine recognizer = getRecognitionEngine();
        if (recognizer != null) {
            recognizer.registerEngines();

            final SpokenInput input = getSpokenInput();
            if (input != null) {
                input.open();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
    }

}
