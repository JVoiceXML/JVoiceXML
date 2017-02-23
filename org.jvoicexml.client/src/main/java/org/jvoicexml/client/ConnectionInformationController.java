/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client;

import org.jvoicexml.ConnectionInformation;

/**
 * Provides extended functionality to a {@link ConnectionInformation} object,
 * such as lifecycle management through the {@link #cleanup()} method.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.6
 */
public interface ConnectionInformationController {

    /**
     * Retrieves the encapsulated connection info.
     * @return the connection info object
     */
    ConnectionInformation getConnectionInformation();

    /**
     * Performs some additional cleanup.
     */
    void cleanup();
}
