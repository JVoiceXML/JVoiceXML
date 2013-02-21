/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/jndi/CharacterInputSkeleton.java $
 * Version: $LastChangedRevision: 1874 $
 * Date:    $Date: 2009-10-20 09:07:58 +0200 (Di, 20 Okt 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.rmi.server.UnicastRemoteObject;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.client.jndi.RemoteCharacterInput;

/**
 * Skeleton for the {@link org.jvoicexml.CharacterInput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 1874 $
 * @since 0.5
 */
public final class CharacterInputSkeleton
        extends UnicastRemoteObject implements RemoteCharacterInput, Skeleton {
    /** The serial version UID. */
    private static final long serialVersionUID = -5497137347016070409L;

    /** The character input device. */
    private final CharacterInput input;

    /** The session ID. */
    private String sessionID;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the skeleton.
     */
    public CharacterInputSkeleton()
            throws RemoteException {
        input = null;
    }

    /**
     * Constructs a new object.
     * @param id The session ID.
     * @param characterInput The character input device.
     * @throws RemoteException
     *         Error creating the skeleton.
     */
    public CharacterInputSkeleton(final String id,
                                  final CharacterInput characterInput)
            throws RemoteException {
        sessionID = id;
        input = characterInput;
    }

    /**
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf)
            throws RemoteException {
        if (input == null) {
            throw new RemoteException("No input! Cannot process dtmf: "
                    + dtmf);
        }
        input.addCharacter(dtmf);
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName()
            throws RemoteException {
        return RemoteCharacterInput.class.getSimpleName() + "." + sessionID;
    }
}
