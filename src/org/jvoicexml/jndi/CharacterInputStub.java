package org.jvoicexml.jndi;

import java.io.Serializable;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CharacterInput;
import org.jvoicexml.implementation.UserInputListener;

/**
 * Stub for the <code>CharacterInput</code>.
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
public final class CharacterInputStub
        extends AbstractStub<RemoteCharacterInput>
        implements CharacterInput, Stub, Serializable {
    /** The serial version UID. */
    static final long serialVersionUID = 3991238538089789758L;

    /** Thes session ID. */
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
    protected Class getLocalClass() {
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
     */
    public void setUserInputListener(final UserInputListener listener) {
    }
}
