/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.RemoteClient;

/**
 * This class provides a dummy implementation for {@link RemoteClient}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.6
 */
@SuppressWarnings("serial")
public final class DummyRemoteClient implements RemoteClient {
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
