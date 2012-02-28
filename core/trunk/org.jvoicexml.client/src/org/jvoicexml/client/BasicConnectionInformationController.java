/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Basic functionality of a {@link ConnectionInformationController}.
 * Maintains a single {@link BasicConnectionInformation} object with
 * an empty implementation of the
 * {@link ConnectionInformationController#cleanup()} method.
 * @author dirk
 * @version $Revision: $
 * @since 0.7.6
 */
public class BasicConnectionInformationController
        implements ConnectionInformationController {
    /** The encapsualated connection information. */
    private final ConnectionInformation  info;

    /**
     * Constructs a new object.
     * @param call unique identifier for the {@link org.jvoicexml.CallControl}.
     * @param output unique identifier for the
     *  {@link org.jvoicexml.SystemOutput}.
     * @param input unique identifier for the {@link org.jvoicexml.UserInput}.
     */
    public BasicConnectionInformationController(final String call,
            final String output, final String input) {
        info = new BasicConnectionInformation(call, output, input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConnectionInformation getConnectionInformation() {
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
    }
}
