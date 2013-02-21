/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/SpeakablePlainText.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
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


/**
 * Plain text to be passed to the TTS engine.
 *
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.4
 */
public final class SpeakablePlainText
        implements SpeakableText {
    /** Plain text to be spoken. */
    private final StringBuilder text;

    /**
     * Constructs a new object.
     */
    public SpeakablePlainText() {
        text = new StringBuilder();
    }

    /**
     * Constructs a new object.
     * @param str The text to be spoken.
     */
    public SpeakablePlainText(final String str) {
        this();

        text.append(str);
    }

    /**
     * {@inheritDoc}
     */
    public String getSpeakableText() {
        if (text == null) {
            return null;
        }
        return text.toString();
    }

    /**
     * {@inheritDoc}
     */
    public SpeakableText appendSpeakableText(final String str) {
        if (text != null) {
            text.append(str);
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSpeakableTextEmpty() {
        return text.length() == 0;
    }

    /**
     * {@inheritDoc}
     * @since 0.6
     */
    @Override
    public String toString() {
        return getSpeakableText();
    }

    /**
     * {@inheritDoc}
     * @since 0.7
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SpeakablePlainText)) {
            return false;
        }
        final SpeakablePlainText speakable = (SpeakablePlainText) obj;
        if (text == null) {
            return speakable.text == null;
        }
        final String txt = getSpeakableText();
        final String otherTxt = speakable.getSpeakableText();
        return txt.equals(otherTxt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBargeInEnabled() {
        return false;
    }
}
