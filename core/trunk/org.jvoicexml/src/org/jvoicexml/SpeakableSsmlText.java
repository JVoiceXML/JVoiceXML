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
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Text to be passed to the TTS engine. This text may contain SSML markups,
 * as defined in
 * <a href="http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/">
 * http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/</a>.
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.4
 */
public final class SpeakableSsmlText
        implements SpeakableText {
    /** Base hash code. */
    private static final int HASH_CODE_BASE = 5;

    /** Multiplier for hash code generation. */
    private static final int HASH_CODE_MULTIPLIER = 59;

    /** The SSML formatted text to be spoken. */
    private final SsmlDocument document;

    /** Timeout that will be used for the following user input. */
    private long timeout;

    /** The barge-in type. */
    private final BargeInType bargeInType;

    /**
     * Constructs a new object.
     * @param doc
     *        The SSML document to speak.
     */
    public SpeakableSsmlText(final SsmlDocument doc) {
        document = doc;
        timeout = -1;
        bargeInType = null;
    }

    /**
     * Constructs a new object.
     * @param doc
     *        the SSML document to speak.
     * @param type
     *        the barge-in type.
     */
    public SpeakableSsmlText(final SsmlDocument doc, final BargeInType type) {
        document = doc;
        timeout = -1;
        bargeInType = type;
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
     * Retrieves the barge-in type.
     * @return the barge-in type
     * @since 0.7.1
     */
    public BargeInType getBargeInType() {
        return bargeInType;
    }

    /**
     * Retrieves the timeout of this speakable to wait before a
     * noinput event is generated.
     * @return number of milliseconds to wait.
     * @since 0.6
     */
    public long getTimeout() {
        if (document == null) {
            return -1;
        }

        return timeout;
    }

    /**
     * Sets the timeout that will be used for the following user input.
     * @param msec number of milliseconds to wait.
     * @since 0.6
     */
    public void setTimeout(final long msec) {
        timeout = msec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof SpeakableSsmlText)) {
            return false;
        }
        final SpeakableSsmlText speakable = (SpeakableSsmlText) other;
        final String text = getSpeakableText();
        final String otherText = speakable.getSpeakableText();
        if (text != speakable.getSpeakableText()) {
            if ((text != null) && !text.equals(otherText)) {
                return false;
            }
        }
        return timeout == speakable.getTimeout();
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
        hash *= HASH_CODE_MULTIPLIER;

        return (int) (hash + timeout);
    }

    /**
     * {@inheritDoc}
     * @since 0.6
     */
    @Override
    public String toString() {
        return getSpeakableText();
    }
}
