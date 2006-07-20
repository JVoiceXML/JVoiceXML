/*
 * File:    $RCSfile: CharacterInputSkeleton.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/07/17 14:15:47 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
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

package org.jvoicexml.jndi;

import java.rmi.RemoteException;
import org.jvoicexml.implementation.CharacterInput;
import java.rmi.server.UnicastRemoteObject;

/**
 * Skeleton for the <code>CharacterInput</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.3 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 * @see org.jvoicexml.implementation.CharacterInput
 */
public class CharacterInputSkeleton
        extends UnicastRemoteObject implements RemoteCharacterInput, Skeleton {
    /** The character input device. */
    private final CharacterInput input;

    /** Thes session ID. */
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
        input.addCharacter(dtmf);
    }

    /**
     * {@inheritDoc}
     */
    public void startRecognition()
            throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition()
            throws RemoteException {
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName()
            throws RemoteException {
        return RemoteCharacterInput.class.getSimpleName() + "." + sessionID;
    }
}
