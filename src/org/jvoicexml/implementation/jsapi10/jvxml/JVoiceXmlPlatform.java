/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation.jsapi10.jvxml;

import org.jvoicexml.SpokenInput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.implementation.jsapi10.AbstractJsap10Platform;
import org.jvoicexml.implementation.jsapi10.AudioInput;
import org.jvoicexml.implementation.jsapi10.AudioOutput;
import org.jvoicexml.implementation.jsapi10.RecognitionEngine;
import org.jvoicexml.implementation.jsapi10.TTSEngine;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * The platform, hosting all resources needed by the implementation platform.
 *
 * <p>
 * This platform manages the resources <a
 * href="http://freetts.sourceforge.net">FreeTTS</a> for audio output and <a
 * href="http://cmusphinx.sourceforge.net">sphinx 4</a> for audio input, to
 * demo the integration of an implementation platform.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlPlatform
        extends AbstractJsap10Platform {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlPlatform.class);

    /** The type of this platform. */
    public static final String TYPE = "jsapi1.0";

    /** The implemented recognition engine. */
    private RecognitionEngine recognitionEngine;

    /** The implemented TTS engine. */
    private TTSEngine ttsEngine;

    /** The related user input. */
    private SpokenInput input;

    /** The related system output. */
    private SystemOutput output;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlPlatform() {
    }


    /**
     * {@inheritDoc}
     *
     * @return <code>jsapi1.0</code>.
     */
    public String getType() {
        return TYPE;
    }

    /**
     * Toggle support of system output.
     * @param enable <code>true</code> if system output is enabled.
     */
    public void setOutput(final boolean enable) {
        if (enable) {
            ttsEngine = new JVoiceXmlTTSEngine();
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("synthesis disabled");
            }

            ttsEngine = null;
        }
    }

    /**
     * Toggle support of user input.
     * @param enable <code>true</code> if user input is enabled.
     */
    public void setInput(final boolean enable) {
        if (enable) {
            recognitionEngine = new JVoiceXmlRecognitionEngine();
        } else {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("recognition disabled");
            }

            recognitionEngine = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public RecognitionEngine getRecognitionEngine() {
        return recognitionEngine;
    }

    /**
     * {@inheritDoc}
     */
    public TTSEngine getTTSEngine() {
        return ttsEngine;
    }

    /**
     * {@inheritDoc}
     */
    public SystemOutput getSystemOutput() {
        if (output == null) {
            output = new AudioOutput(this);
        }

        return output;
    }

    /**
     * {@inheritDoc}
     */
    public SpokenInput getSpokenInput() {
        if (input == null) {
            input = new AudioInput(this);
        }

        return input;
    }

    /**
     * {@inheritDoc}
     */
    public void activate() {
        if (input != null) {
            input.activate();
        }

        if (output != null) {
            output.activate();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        if (input != null) {
            input.passivate();
        }

        if (output != null) {
            output.passivate();
        }
    }
}
