/*
 * File:    $RCSfile: TTSEngine.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

import java.io.ObjectOutputStream;

import javax.speech.EngineModeDesc;
import javax.speech.synthesis.Synthesizer;

/**
 * Interace to the JSAPI 1.0 compliant TTS engine.
 *
 * <p>
 * Classes that implement this interface provide an interface to the TTS engine
 * to use.
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
public interface TTSEngine {
    /**
     * Registers the speech engines with the Central class for use by JVoiceXML.
     *
     * @see javax.speech.EngineCentral
     */
    void registerEngines();

    /**
     * Retrieves the required engine properties.
     *
     * @return Required engine properties or <code>null</code> for default
     * engine selection
     */
    EngineModeDesc getEngineProperties();

    /**
     * Retrieves the name of the default voice.
     *
     * @return Name of the default voice.
     */
    String getDefaultVoice();

    /**
     * Sets the output stream, where the output should be directed to for the
     * given <code>Synthesizer</code>.
     *
     * <p>
     * The <code>OutputStream</code> is obtained from the calling device.
     * </p>
     *
     * <p>
     * <b>Note:</b> Unfortunately this is not a feature of all TTS engines. If
     * no <code>OutputStream</code> is supported, the default output of the
     * TTS engine is used. This may have consequences on the usability with a
     * calling device.
     * </p>
     *
     * @param synthesizer
     * The current synthesizer.
     * @param out
     * The <code>OutputStream</code> to use for the audio output. If this
     * parameter is <code>null</code> the default output of the TTS engine
     * shall be used.
     */
    void setOutputStream(final Synthesizer synthesizer,
                         final ObjectOutputStream out);
}
