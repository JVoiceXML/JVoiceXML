/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.mock;

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.ConnectionInformation;

/**
 * This class provides a dummy implementation for {@link ConnectionInformation}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class MockConnectionInformation implements ConnectionInformation {
    /** The serial version UID. */
    private static final long serialVersionUID = -3795809583703263932L;

    private String callControl = "not set";

    public MockConnectionInformation(final String callControl) {
        this.callControl = callControl;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfile() {
        return "VoiceXML21";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCallControl() {
        return callControl;
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
        try {
            return new URI("http://called/nowhere");
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getCallingDevice() {
        try {
            return new URI("http://calling/nowhere");
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolName() {
        return "http";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProtocolVersion() {
        return "1/1";
    }

}
