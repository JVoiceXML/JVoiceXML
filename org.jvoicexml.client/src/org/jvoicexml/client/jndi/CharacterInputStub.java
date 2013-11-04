/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/client/jndi/CharacterInputStub.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 12:36:01 +0100 (Do, 23 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
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

import org.jvoicexml.CharacterInput;

/**
 * Stub for the {@link CharacterInput}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 * @since 0.5
 */
public final class CharacterInputStub
        extends AbstractStub<RemoteCharacterInput>
        implements CharacterInput, Stub, Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = -7040136029975498559L;

    /** The session ID. */
    private String sessionID;

    /**
     * Constructs a new object.
     */
    public CharacterInputStub() {
    }

    /**
     * Constructs a new object.
     * @param id The session ID.
     */
    public CharacterInputStub(final String id) {
        sessionID = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return CharacterInput.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteCharacterInput> getRemoteClass() {
        return RemoteCharacterInput.class;
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return CharacterInput.class.getSimpleName() + "." + sessionID;
    }

    /**
     * {@inheritDoc}
     */
    public void addCharacter(final char dtmf) {
        final RemoteCharacterInput input = getSkeleton(sessionID);
        try {
            input.addCharacter(dtmf);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }
}
