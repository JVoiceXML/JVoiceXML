/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.error.NoresourceError;

/**
 * Factory for {@link ImplementationPlatform}s.
 *
 * @author Dirk Schnelle-Walka
 *
 * @see org.jvoicexml.ImplementationPlatform
 *
 * @since 0.5.5
 */
public interface ImplementationPlatformFactory extends Configurable {
    /**
     * Factory method to retrieve an implementation platform for the given
     * connection information.
     *
     * @param info
     *        the connection information to use
     * @return <code>ImplementationPlatform</code> to use.
     * @exception NoresourceError
     *            Error assigning the calling device to TTS or recognizer.
     */
    ImplementationPlatform getImplementationPlatform(
            final ConnectionInformation info) throws NoresourceError;

    /**
     * Closes all implementation platforms.
     *
     * @since 0.4
     */
    void close();
}
