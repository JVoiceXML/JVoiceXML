/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/RecognitionResult.java $
 * Version: $LastChangedRevision: 299 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.external;

import org.jvoicexml.RecognitionResult;

/**
 * Result of the recognition process.
 *
 * @author Dirk Schnelle
 * @version $Revision: 299 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.6
 */
public final class ExternalRecognitionResult {
    /** The confidence of the result. */
    private final float confidence;

    /** The mode. */
    private final String mode;

    /** The utterance. */
    private final String utterance;

    /** Result accepted. */
    private final boolean accepted;

    /** Result rejected. */
    private final boolean rejected;

    /**
     * Constructs a new object.
     * @param result the observed recognition.
     */
    public ExternalRecognitionResult(final RecognitionResult result) {
        utterance = result.getUtterance();
        confidence = result.getConfidence();
        mode = result.getMode();
        accepted = result.isAccepted();
        rejected = result.isRejected();
    }

    /**
     * {@inheritDoc}
     */
    public float getConfidence() {
        return confidence;
    }

    /**
     * {@inheritDoc}
     */
    public String getMode() {
        return mode;
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
        return accepted;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRejected() {
        return rejected;
    }
}
