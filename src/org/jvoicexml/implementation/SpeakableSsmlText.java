/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation;

import org.jvoicexml.SpeakableText;
import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Text to be passed to the TTS engine. This text may contain SSML markups,
 * as defined in
 * <a href="http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/">
 * http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/</a>.
 *
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class SpeakableSsmlText
        implements SpeakableText {
    /**
     * The SSML formatted text to be spoken.
     * @todo Replace the text with a SSML document.
     */
    private SsmlDocument document;

    /**
     * Constructs a new object.
     * @param doc
     *        The SSML document to speak.
     */
    public SpeakableSsmlText(final SsmlDocument doc) {
        document = doc;
    }


    /**
     * {@inheritDoc}
     */
    public String getSpeakableText() {
        if (document == null) {
            return null;
        }

        return document.toString();
    }

    /**
     * Retrieves the SSML document to speak.
     * @return SSML document to speak.
     * @since 0.5
     */
    public SsmlDocument getDocument() {
        return document;
    }

    /**
     * {@inheritDoc}
     */
    public SpeakableText appendSpeakableText(final String str) {
        if (document == null) {
            return this;
        }

        final Speak speak = (Speak) document.getFirstChild();
        speak.addText(str);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSpeakableTextEmpty() {
        if (document == null) {
            return true;
        }

        final Speak speak = document.getSpeak();

        return !speak.hasChildNodes();
    }
}
