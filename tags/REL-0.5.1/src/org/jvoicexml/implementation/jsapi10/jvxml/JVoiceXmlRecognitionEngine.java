/*
 * File:    $RCSfile: JVoiceXmlRecognitionEngine.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/03/28 08:02:53 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineModeDesc;

import org.apache.log4j.Logger;
import org.jvoicexml.implementation.jsapi10.RecognitionEngine;

/**
 * Audio input that uses
 * <a href="http://cmusphinx.sourceforge.net/sphinx4/">sphinx 4</a>
 * as the recognition engine.
 *
 * <p>
 * Support for audio output using audio files and text-to-speech (TTS).
 * The implementation platform must be able to freely sequence TTS and
 * audio output.
 * </p>
 *
 * <p>
 * <b>NOTE:</b>  this uses the Central class
 * of JSAPI to find a Synthesizer.  The Central class
 * expects to find a speech.properties file in user.home
 * or java.home/lib.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JVoiceXmlRecognitionEngine
        implements RecognitionEngine {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlRecognitionEngine.class);

    /**
     * Create a new Object.
     */
    public JVoiceXmlRecognitionEngine() {
    }

    /**
     * Register the speech engines with the Central class for use by
     * JVoiceXML.
     *
     * @see javax.speech.EngineCentral
     */
    public void registerEngines() {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("registering sphinx4 engine central...");
            }
            Central.registerEngineCentral(Sphinx4EngineCentral.class.getName());
        } catch (EngineException ee) {
            LOGGER.error("error registering engine central", ee);
        }
    }

    /**
     * Get the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     *   engine selection
     *
     * @todo This is more or less a bogus implementation and has to be replaced,
     * if  sphinx4 is more JSAPI compliant.
     */
    public EngineModeDesc getEngineProperties() {
        return new Sphinx4RecognizerModeDesc();
    }
}
