/*
 * File:    $RCSfile: CharacterInputRecognitionResult.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.implementation;

/**
 * Result of a DTMF recognition process.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
class CharacterInputRecognitionResult
        implements RecognitionResult {
    /** The recognized DTMF string. */
    private final String utterance;

    /** The last reached marker. */
    private String marker;

    /**
     * Constructs a new object.
     * @param dtmf The recognized DTMF string.
     */
    public CharacterInputRecognitionResult(final String dtmf) {
        utterance = dtmf;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return marker;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        return utterance;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return false;
    }

    /**
     * {@inheritDoc}
    */
    public void setMark(final String mark) {
        marker = mark;
    }
}
