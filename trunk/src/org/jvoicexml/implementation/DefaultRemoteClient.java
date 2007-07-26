/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RemoteClient;

/**
 * Basic implementation of a {@link RemoteClient} that works with the default
 * resources.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5.5
 */
@SuppressWarnings("serial")
final class DefaultRemoteClient implements RemoteClient {
    /** Type of the {@link org.jvoicexml.CallControl} resource. */
    private final String call;

    /** Type of the {@link org.jvoicexml.SystemOutput} resource. */
    private final String output;

    /** Type of the {@link org.jvoicexml.UserInput} resource. */
    private final String input;

    /**
     * Constructs a new object.
     * @param callType type of the {@link org.jvoicexml.CallControl} resource
     * @param outputType type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType type of the {@link org.jvoicexml.UserInput} resource
     */
    public DefaultRemoteClient(final String callType, final String outputType,
            final String inputType) {
        call = callType;
        output = outputType;
        input = inputType;
    }

    /**
     * {@inheritDoc}
     */
    public String getCallControl() {
        return call;
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemOutput() {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    public String getUserInput() {
        return input;
    }

}
