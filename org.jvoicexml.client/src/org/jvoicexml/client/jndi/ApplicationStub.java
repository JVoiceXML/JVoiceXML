package org.jvoicexml.client.jndi;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.naming.Context;

import org.jvoicexml.Application;
import org.jvoicexml.LastResult;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Stub for the {@link Application}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3956 $
 * @since 0.7.7
 */
public class ApplicationStub extends AbstractStub<RemoteApplication>
        implements Application, Stub, Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 6891917167049375298L;

    /** The session ID. */
    private String sessionID;

    /**
     * Constructs a new object.
     */
    public ApplicationStub() {
    }

    /**
     * Constructs a new object.
     * @param id the session id
     */
    public ApplicationStub(final String id) {
        sessionID = id;
    }

    /**
     * Constructs a new object.
     * 
     * @param context
     *            The context to use.
     * @since 0.6
     */
    public ApplicationStub(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStubName() {
        return Application.class.getSimpleName() + "." + sessionID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addDocument(URI uri, VoiceXmlDocument doc) throws BadFetchError {
        final RemoteApplication application = getSkeleton();
        try {
            application.addDocument(uri, doc);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlDocument getCurrentDocument() {
        final RemoteApplication application = getSkeleton();
        try {
            return application.getCurrentDocument();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getApplication() {
        final RemoteApplication application = getSkeleton();
        try {
            return application.getApplication();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootDocument(VoiceXmlDocument document) throws BadFetchError {
        final RemoteApplication application = getSkeleton();
        try {
            application.setRootDocument(document);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLoaded(URI uri) {
        final RemoteApplication application = getSkeleton();
        try {
            return application.isLoaded(uri);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getXmlBase() {
        final RemoteApplication application = getSkeleton();
        try {
            return application.getXmlBase();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(URI uri) {
        final RemoteApplication application = getSkeleton();
        try {
            return application.resolve(uri);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI resolve(URI baseUri, URI uri) {
        final RemoteApplication application = getSkeleton();
        try {
            return application.resolve(baseUri, uri);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLastResult(List<LastResult> lastresult) {
        final RemoteApplication application = getSkeleton();
        try {
            application.setLastResult(lastresult);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LastResult> getLastResult() {
        final RemoteApplication application = getSkeleton();
        try {
            return application.getLastResult();
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();
            re.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteApplication> getRemoteClass() {
        return RemoteApplication.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> getLocalClass() {
        return Application.class;
    }

}
