/*
 * File:    $RCSfile: Platform.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.implementation;

/**
 * Objects that implement this interface manage all resources, needes for
 * system output and spoken input.
 *
 * <p>
 * The <code>ImplementationPlatform</code> instantiates the implementing
 * class and asks this instance for resources like TTS and recognition.
 * The platform does not need to support all resources. If the interpreter
 * detects a tag that addresses the missing resource, an
 * <code>error.noresource</code> event is thrown.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @see org.jvoicexml.implementation.SystemOutput
 * @see org.jvoicexml.implementation.SpokenInput
 */
public interface Platform
        extends ExternalResource {
    /**
     * Retrieves the system output implemetation of this platform.
     * @return The system output of this platform.
     */
    SystemOutput getSystemOutput();

    /**
     * Retrieves the spoken input implementation of this platform.
     * @return The user input of this platform.
     *
     * @since 0.5
     */
    SpokenInput getSpokenInput();

    /**
     * Retrieves a unique type of this implementation platform.
     * @return Type of this implementation platform.
     * @since 0.5
     */
    String getType();

    /**
     * Activates this plateform, when it is retrieved from the pool.
     *
     * @since 0.6
     */
    void activate();

    /**
     * Passivates this platform, when it is returned to the pool.
     * @since 0.6
     */
    void passivate();
}
