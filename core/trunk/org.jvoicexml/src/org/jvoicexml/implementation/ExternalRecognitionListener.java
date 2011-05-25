/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RecognitionResult;

/**
 * Listener for events from the <code>UserInput</code> implementation to be used
 * in external applications, e.g. a X+V implementation to display the
 * recognition result.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @see org.jvoicexml.UserInput
 * @since 0.6
 */
public interface ExternalRecognitionListener {
    /**
     * Starts this external recognition listener.
     * 
     * @since 0.7.5
     */
    void start();

    /**
     * The user made an utterance, that matched an active grammar.
     * @param result The recognition result.
     */
    void resultAccepted(final RecognitionResult result);

    /**
     * The user made an utterance, that did not match an active grammar.
     * @param result The recognition result.
     */
    void resultRejected(final RecognitionResult result);
}
