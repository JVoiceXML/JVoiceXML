/*
 * File:    $RCSfile: CharacterInput.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/06/23 08:43:57 $
 * Author:  $Author: schnelle $
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

import org.jvoicexml.InputDevice;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Facade for easy control and monitoring of the user's DTMF input.
 *
 * <p>
 * Objects that implement this interface are able to detect character input and
 * to control input detection interval duration with a timer whose length is
 * specified by a VoiceXML document.
 * </p>
 *
 * <p>
 * If an input resource is not available, an <code>error.noresource</code>
 * event must be thrown.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
public interface CharacterInput
        extends InputDevice {
    /**
     * The user entered a DTMF.
     *
     * @param dtmf Entered dtmf.
     */
    void addCharacter(final char dtmf);

    /**
     * Detects and reports character and/or spoken input simultaneously.
     *
     * @exception NoresourceError
     * The input resource is not available.
     * @exception BadFetchError
     * The active grammar contains some errors.
     */
    void startRecognition()
            throws NoresourceError, BadFetchError;

    /**
     * Stops a previously started recognition.
     *
     * @see #startRecognition
     */
    void stopRecognition();
}
