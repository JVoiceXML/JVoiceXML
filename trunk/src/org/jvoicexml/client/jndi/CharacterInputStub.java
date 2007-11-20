/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/jndi/client/CharacterInputStub.java $
 * Version: $LastChangedRevision: 407 $
 * Date:    $Date: 2007-08-16 14:50:46 +0200 (Do, 16 Aug 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.Serializable;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;

/**
 * Stub for the <code>CharacterInput</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 407 $
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 * @see org.jvoicexml.CharacterInput
 */
public final class CharacterInputStub
        extends AbstractStub<RemoteCharacterInput>
        implements CharacterInput, Stub, Serializable {
    /** The serial version UID. */
    static final long serialVersionUID = 3991238538089789758L;

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

    /**
     * {@inheritDoc}
     */
    public void startRecognition()
            throws NoresourceError, BadFetchError {
    }

    /**
     * {@inheritDoc}
     */
    public void stopRecognition() {
    }

    /**
     * {@inheritDoc}
     *
     * @todo implement this method.
     */
    public void connect(final RemoteClient client)
        throws IOException {
    }

    /**
     * {@inheritDoc}
     *
     * @todo implement this method.
     */
    public void disconnect(final RemoteClient client) {
    }
}
