/*
 * File:    $RCSfile: $
 * Version: $Revision: $
 * Date:    $Date: $
 * Author:  $Author: $
 * State:   $State: $
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


package org.jvoicexml.implementation.mrcpv2;

import org.jvoicexml.RecognitionResult;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * Result of the recognition process.
 *
 * @author Spencer Lord
 * @version $Revision: $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Mrcpv2RecognitionResult
        implements RecognitionResult {
    /** The result returned by the recognizer. */
    private final org.speechforge.cairo.client.recog.RecognitionResult result;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /** The confidence of the result */
    private float confidenceResult;

    /**
     * Constructs a new object.
     * @param res The result returned by the recognizer.
     */
    public Mrcpv2RecognitionResult(final org.speechforge.cairo.client.recog.RecognitionResult res) {
        result = res;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        if (result == null) {
            return null;
        }

        return result.getText();
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
        markname = mark;
    }

    /**
     * {@inheritDoc}
     */
    public String getMark() {
        return markname;
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return 1.0f;
    }

    /**
     * {@inheritDoc}
     */
    public float[] getWordsConfidence() {
       return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getWords() {
        final String[] words =  result.getText().split(" ");
        return words;
    }



    /**
     * {@inheritDoc}
     */
    public ModeType getMode() {
        return ModeType.valueOf("VOICE");
    }
}
