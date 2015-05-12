package org.jvoicexml.jndi;

import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import org.jvoicexml.Application;
import org.jvoicexml.LastResult;
import org.jvoicexml.client.jndi.RemoteApplication;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

public class ApplicationSkeleton extends UnicastRemoteObject
        implements RemoteApplication, Skeleton {

    /** The serial version UID. */
    private static final long serialVersionUID = 6976197627560263588L;

    /** The session ID. */
    private String sessionID;

    /** The encapsulated application object. */
    private Application application;

    /**
     * Constructs a new object.
     * 
     * @throws RemoteException
     *             Error creating the remote object.
     */
    protected ApplicationSkeleton() throws RemoteException {
    }

    /**
     * Constructs a new object.
     * 
     * @param id
     *            The session ID.
     * @param app
     *            teh application
     * @throws RemoteException
     *             Error creating the skeleton.
     */
    public ApplicationSkeleton(final String id, final Application app)
            throws RemoteException {
        sessionID = id;
        application = app;
    }

    @Override
    public String getSkeletonName() throws RemoteException {
        return RemoteApplication.class.getSimpleName()+ "." + sessionID;
    }

    @Override
    public void addDocument(URI uri, VoiceXmlDocument doc)
            throws RemoteException, BadFetchError {
        if (application == null) {
            return;
        }
        application.addDocument(uri, doc);
    }

    @Override
    public VoiceXmlDocument getCurrentDocument() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getCurrentDocument();
    }

    @Override
    public URI getApplication() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getApplication();
    }

    @Override
    public void setRootDocument(VoiceXmlDocument document)
            throws RemoteException, BadFetchError {
        if (application == null) {
            return;
        }
        application.setRootDocument(document);
    }

    @Override
    public boolean isLoaded(URI uri) throws RemoteException {
        if (application == null) {
            return false;
        }
        return application.isLoaded(uri);
    }

    @Override
    public URI getXmlBase() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getXmlBase();
    }

    @Override
    public URI resolve(URI uri) throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.resolve(uri);
    }

    @Override
    public URI resolve(URI baseUri, URI uri) throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.resolve(baseUri, uri);
    }

    @Override
    public void setLastResult(List<LastResult> lastresult)
            throws RemoteException {
        if (application == null) {
            return;
        }
        application.setLastResult(lastresult);
    }

    @Override
    public List<LastResult> getLastResult() throws RemoteException {
        if (application == null) {
            return null;
        }
        return application.getLastResult();
    }

}
