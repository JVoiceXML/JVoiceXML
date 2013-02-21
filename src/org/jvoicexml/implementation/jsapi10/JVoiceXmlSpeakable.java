/*
 * File:    $RCSfile: JVoiceXmlSpeakable.java,v $
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

import javax.speech.synthesis.Speakable;

/**
 * A text to be spoken by the TTS engine.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
final class JVoiceXmlSpeakable
        implements Speakable {
    /** The SSML or JSML formatted text to be spoken. */
    private final StringBuilder text;

    /**
     * Constructs a ne empty <code>Speakable</code> object.
     *
     * @since 0.3
     */
    public JVoiceXmlSpeakable() {
        text = new StringBuilder();
    }

    /**
     * Creates a new object.
     * @param ssml The text to be spoken in any markup language, such as SSML
     * or JSML.
     */
    public JVoiceXmlSpeakable(final String ssml) {
        this();

        text.append(ssml);
    }

    /**
     * Checks if this speakable contains any text.
     * @return <code>true</code> if this is an empy <code>Speakable</code>
     *         object.
     */
    public boolean isEmpty() {
        return text.length() == 0;
    }

    /**
     * Append the givent string to this <code>Speakable</code> object.
     * @param str
     *        The text to be appended.
     * @return This object.
     *
     * @since 0.3
     */
    public JVoiceXmlSpeakable append(final String str) {
        if (text != null) {
            text.append(str);
        }

        return this;
    }

    /**
     * Returns text to be spoken formatted for the Java Speech Markup Language.
     * This method is called immediately when a Speakable object is passed to
     * the speak method of a Synthesizer. The text placed on the speaking queue
     * can be inspected through the SynthesizerQueueItem on the speech output
     * queue available through the synthesizer's enumerateQueue method.
     *
     * @return A string containing Java Speech Markup Language text
     */
    public String getJSMLText() {
        /** @todo Replace the ssml with a jsml. */
        return text.toString();
    }
}
