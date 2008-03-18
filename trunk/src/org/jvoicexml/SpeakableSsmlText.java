/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

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
 * Copyright &copy; 2006-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class SpeakableSsmlText
        implements SpeakableText {
    /** Base hash code. */
    private static final int HASH_CODE_BASE = 5;

    /** Multiplier for hash code generation. */
    private static final int HASH_CODE_MULTIPLIER = 59;

    /** The SSML formatted text to be spoken. */
    private SsmlDocument document;

    /** Timeout that will be used for the following user input. */
    private long timeout;

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

        final Speak speak = document.getSpeak();
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

    /**
     * Retrieves the timeout in msec that will be used for the following user
     * input.
     * @return timeout in milliseconds.
     * @since 0.6
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for the following user input.
     * @param value timeout in milliseconds.
     * @since 0.6
     */
    public void setTimeout(final long value) {
        timeout = value;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof SpeakableSsmlText)) {
            return false;
        }
        // TODO use all attributes.
        final SpeakableSsmlText speakable = (SpeakableSsmlText) other;
        final String text = getSpeakableText();
        if (text == null) {
            return speakable.getSpeakableText() == null;
        }

        return text.equals(speakable.getSpeakableText());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = HASH_CODE_BASE;
        hash *= HASH_CODE_MULTIPLIER;
        if (document != null) {
            hash += document.hashCode();
        }
        return hash;
    }
}
