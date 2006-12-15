/*
 * File:    $RCSfile: UserInputListener.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation;

import org.jvoicexml.UserInput;
import org.jvoicexml.xml.vxml.BargeInType;

/**
 * Listener for events from the <code>UserInput</code> implementation.
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
 * @see UserInput
 * @since 0.5
 */
public interface UserInputListener {
    /**
     * Notification that the user started to speak.
     *
     * <p>
     * This method is called whenever the recognizer arrives at a supported
     * barge-in type.
     * </p>
     *
     * @param type
     *        Type of the notificatation.
     */
    void speechStarted(final BargeInType type);

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
