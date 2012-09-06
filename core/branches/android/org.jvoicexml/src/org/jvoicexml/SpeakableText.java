/*
 * File:    $RCSfile: SpeakableText.java,v $
 * Version: $Revision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
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

package org.jvoicexml;

/**
 * Objects that implement this interface contain some text, that is
 * passed to the TTS engine.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.4
 */
public interface SpeakableText {
    /**
     * Appends the given text to this speakable.
     * @param text Text to be appended.
     * @return This object.
     */
    SpeakableText appendSpeakableText(final String text);

    /**
     * Retrieves the text to be passed to the TTS Engine.
     * @return Text to be spoken.
     */
    String getSpeakableText();

    /**
     * Checks if this speakable contains any text to be passed to the
     * TTS engine.
     * @return <code>true</code> if this speakable contains text.
     */
    boolean isSpeakableTextEmpty();

    /**
     * Checks is barge-in is enabled for this speakable.
     * @return <code>true</code> if barge-in is enabled.
     * @since 0.7.1
     */
    boolean isBargeInEnabled();
}
