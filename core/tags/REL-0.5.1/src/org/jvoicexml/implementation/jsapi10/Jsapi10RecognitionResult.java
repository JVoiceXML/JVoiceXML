/*
 * File:    $RCSfile: Jsapi10RecognitionResult.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/15 09:36:24 $
 * Author:  $Author: schnelle $
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

import javax.speech.recognition.Result;
import javax.speech.recognition.ResultToken;
import org.jvoicexml.implementation.RecognitionResult;

/**
 * JSAPI 10 implementation of the result of the recognition process.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class Jsapi10RecognitionResult
        implements RecognitionResult {
    /** The result returned by the recognizer. */
    private final Result result;

    /** The name of the mark last executed by the SSML processor. */
    private String markname;

    /**
     * Constructs a new object.
     * @param res The result returned by the recognizer.
     */
    public Jsapi10RecognitionResult(final Result res) {
        result = res;
    }

    /**
     * {@inheritDoc}
     */
    public String getUtterance() {
        if (result == null) {
            return null;
        }

        if (!isAccepted()) {
            return null;
        }

        final ResultToken[] tokens = result.getBestTokens();
        final StringBuilder utterance = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            utterance.append(tokens[i].getSpokenText());
            utterance.append(' ');
        }

        return utterance.toString().trim();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAccepted() {
        if (result == null) {
            return false;
        }

        return result.getResultState() == Result.ACCEPTED;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        if (result == null) {
            return false;
        }

        return result.getResultState() == Result.REJECTED;
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
}
