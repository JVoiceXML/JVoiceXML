/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.io.IOException;
import java.net.URI;

import org.jvoicexml.event.error.NoresourceError;


/**
 * Call control.
 *
 * <p>
 * Objects that implement this interface are able to support making a third
 * party connection through a communications network, such as the telephone.
 * </p>
 *
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface CallControl
        extends ExternalResource, RemoteConnectable {
    /**
     * Plays a stream from the given URI.
     * @param uri URI with audio data.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void play(final URI uri) throws NoresourceError, IOException;

    /**
     * Starts recording to the given URI.
     * @param uri destination URI for recording.
     * @exception NoresourceError
     *            Error accessing the terminal
     * @exception IOException
     *            Error accessing the given URI.
     * @since 0.6
     */
    void record(final URI uri) throws NoresourceError, IOException;
}
