/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Locale;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.ssml.Speak;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.BargeInType;
import org.jvoicexml.xml.vxml.PriorityType;

/**
 * Text to be passed to the TTS engine. This text may contain SSML markups,
 * as defined in
 * <a href="http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/">
 * http://www.w3.org/TR/2003/CR-speech-synthesis-20031218/</a>.
 *
 *
 * @author Dirk Schnelle-Walka
 * @since 0.4
 */
public final class SpeakableSsmlText
        implements SpeakableText {
    /** The SSML formatted text to be spoken. */
    private final SsmlDocument document;

    /** The barge-in type. */
    private final BargeInType bargeInType;

    /** Flag, if barge-in is supported. */
    private final boolean bargein;

    /** The priority of this prompt. */
    private PriorityType priority;
    
    /**
     * Constructs a new object.
     * @param doc
     *        The SSML document to speak.
     */
    public SpeakableSsmlText(final SsmlDocument doc) {
        this(doc, true, null);
    }

    /**
     * Constructs a new object.
     * @param doc
     *        the SSML document to speak.
     * @param useBargein
     *        <code>true</code> if bargein is supported.
     * @param type
     *        the barge-in type, maybe <code>null</code> if no bargein is
     *        supported.
     */
    public SpeakableSsmlText(final SsmlDocument doc, final boolean useBargein,
            final BargeInType type) {
        document = doc;
        bargeInType = type;
        bargein = useBargein;
        priority = PriorityType.APPEND;
    }

    /**
     * Constructs a new object. The given text is encapsulated in an
     * {@link SsmlDocument}. The speakable defaults to a priority of
     * {@link PriorityType#APPEND}.
     * @param text the text that should be contained in the speakable
     * @param locale the locale of this speakable
     * @throws ParserConfigurationException
     *         error creating the {@link SsmlDocument}.
     */
    public SpeakableSsmlText(final String text, final Locale locale)
            throws ParserConfigurationException {
        this(text, locale, PriorityType.APPEND);
    }
    
    /**
     * Constructs a new object. The given text is encapsulated in an
     * {@link SsmlDocument}.
     * @param text the text that should be contained in the speakable
     * @param locale the locale of this speakable
     * @param speakablePriority the priority of this speakable
     * @throws ParserConfigurationException
     *         error creating the {@link SsmlDocument}.
     */
    public SpeakableSsmlText(final String text, final Locale locale,
            final PriorityType speakablePriority)
            throws ParserConfigurationException {
        document = new SsmlDocument();
        final Speak speak = document.getSpeak();
        speak.setXmlLang(locale);
        speak.addText(text);
        bargeInType = null;
        bargein = true;
        priority = speakablePriority;
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
     * {@inheritDoc}
     */
    @Override
    public boolean isBargeInEnabled(final BargeInType type) {
        return bargein && ((bargeInType == null) || (type == bargeInType));
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
     * Sets the priority of this speakeable.
     * @param speakablePriority the priority
     * @since 0.7.9
     */
    public void setPriority(final PriorityType speakablePriority) {
        priority = speakablePriority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PriorityType getPriority() {
        return priority;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(bargeInType, bargein, document, priority);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SpeakableSsmlText)) {
            return false;
        }
        SpeakableSsmlText other = (SpeakableSsmlText) obj;
        return bargeInType == other.bargeInType && bargein == other.bargein
                && Objects.equals(getSpeakableText(), other.getSpeakableText())
                && priority == other.priority;
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
