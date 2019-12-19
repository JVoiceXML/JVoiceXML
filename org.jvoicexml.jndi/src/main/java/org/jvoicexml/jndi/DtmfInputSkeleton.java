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

package org.jvoicexml.jndi;

import java.rmi.RemoteException;

import org.jvoicexml.DtmfInput;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.client.jndi.RemoteDtmfInput;

/**
 * Skeleton for the {@link org.jvoicexml.DtmfInput}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.5
 */
public final class DtmfInputSkeleton
        implements RemoteDtmfInput {
    /** The character input device. */
    private final DtmfInput input;

    /** The session ID. */
    private SessionIdentifier sessionIdentifier;

    /**
     * Constructs a new object.
     * 
     * @throws RemoteException
     *             Error creating the skeleton.
     */
    public DtmfInputSkeleton() throws RemoteException {
        input = null;
    }

    /**
     * Constructs a new object.
     * 
     * @param id
     *            The session ID.
     * @param characterInput
     *            The character input device.
     * @throws RemoteException
     *             Error creating the skeleton.
     */
    public DtmfInputSkeleton(final SessionIdentifier id,
            final DtmfInput characterInput) throws RemoteException {
        sessionIdentifier = id;
        input = characterInput;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCharacter(final char dtmf) throws RemoteException {
        if (input == null) {
            throw new RemoteException("No input! Cannot process dtmf: " + dtmf);
        }
        input.addDtmf(dtmf);
    }

    /**
     * Retrieves the name of this skeleton.
     * @return name of the skeleton
     */
    public String getSkeletonName() {
        return RemoteDtmfInput.class.getSimpleName() + "." 
                + sessionIdentifier.getId();
    }
}
