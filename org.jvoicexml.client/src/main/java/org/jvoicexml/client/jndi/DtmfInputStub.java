/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.naming.NamingException;

import org.jvoicexml.DtmfInput;
import org.jvoicexml.SessionIdentifier;

/**
 * Stub for the {@link DtmfInput}.
 *
 * @author Dirk Schnelle-Walka
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
    private SessionIdentifier sessionIentifier;

    /**
     * Constructs a new object.
     */
    public DtmfInputStub() {
    }

    /**
     * Constructs a new object.
     * @param id The session ID.
     */
    public DtmfInputStub(final SessionIdentifier id) {
        sessionIentifier = id;
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
        return DtmfInput.class.getSimpleName() + "." + sessionIentifier.getId();
    }

    /**
     * {@inheritDoc}
     */
    public void addDtmf(final char dtmf) {
        try {
            final RemoteDtmfInput input = getSkeleton(sessionIentifier.getId());
            input.addCharacter(dtmf);
        } catch (java.rmi.RemoteException | NamingException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }
}
