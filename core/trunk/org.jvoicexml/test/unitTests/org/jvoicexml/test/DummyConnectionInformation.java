/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
  * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.test;

import java.net.URI;

import org.jvoicexml.ConnectionInformation;

/**
 * This class provides a dummy implementation for {@link ConnectionInformation}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class DummyConnectionInformation implements ConnectionInformation {
    /** The serial version UID. */
    private static final long serialVersionUID = -3795809583703263932L;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallControl() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemOutput() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserInput() {
        return "dummy";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getCalledDevice() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getCallingDevice() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolVersion() {
        return null;
    }

}
