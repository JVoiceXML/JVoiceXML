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

import org.jvoicexml.xml.srgs.ModeType;

/**
 * Result of the recognition process.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.5
 */
public interface RecognitionResult {
    /**
     * Retrieves the result as a single string.
     * @return Result, obtained from the recognizer, <code>null</code> if
     * no result is given or the result is not accepted.
     */
    String getUtterance();

    /**
     * Retrieves the whole utterance confidence level for this interpretation
     * from <code>0.0</code> - <code>1.0</code>.
     *
     * <p>
     * A confidence level of <code>0.0</code> denotes the lowest confidence
     * and a level of <code>1.0</code> denotes the highest confidence.
     * </p>
     *
     * @return confidence level.
     *
     * @since 0.6
     */
    float getConfidence();

    /**
     * Retrieves the mode in which user input was provided: dtmf or voice.
     * @return mode.
     *
     * @since 0.6
     */
    ModeType getMode();

    /**
     * Checks if this result is accepted.
     * @return <code>true</code> if the result is accepted.
     */
    boolean isAccepted();

    /**
     * Checks if this result is rejected.
     * @return <code>true</code> if the result is rejected.
     */
    boolean isRejected();

    /**
     * Sets the mark reached that is reached while playing back an
     * SSML formatted document.
     * @param mark Name of the mark.
     */
    void setMark(final String mark);

    /**
     * Retrieves the name of the mark, that has been reached while playing
     * back an SSML formatted document.
     * @return name of the mark.
     */
    String getMark();
}
