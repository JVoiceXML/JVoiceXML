/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.client.jndi;

import java.io.Serializable;

import org.jvoicexml.DtmfInput;

/**
 * Stub for the {@link DtmfInput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.5
 */
public final class DtmfInputStub
        extends AbstractStub<RemoteDtmfInput>
        implements DtmfInput, Stub, Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -7040136029975498559L;

    /** The session ID. */
    private String sessionID;

    /**
     * Constructs a new object.
     */
    public DtmfInputStub() {
    }

    /**
     * Constructs a new object.
     * @param id The session ID.
     */
    public DtmfInputStub(final String id) {
        sessionID = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return DtmfInput.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteDtmfInput> getRemoteClass() {
        return RemoteDtmfInput.class;
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return DtmfInput.class.getSimpleName() + "." + sessionID;
    }

    /**
     * {@inheritDoc}
     */
    public void addDtmf(final char dtmf) {
        final RemoteDtmfInput input = getSkeleton(sessionID);
        try {
            input.addCharacter(dtmf);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }
}
