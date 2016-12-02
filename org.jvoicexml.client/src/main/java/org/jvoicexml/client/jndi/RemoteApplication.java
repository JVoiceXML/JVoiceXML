/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 4211 $
 * Date:    $Date: 2014-08-15 15:43:25 +0200 (Fri, 15 Aug 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.jvoicexml.LastResult;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Remote interface to enable remote method calls to the
 * {@link org.jvoicexml.Application}.
 * 
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision: 4211 $
 * @since 0.7.7
 */
public interface RemoteApplication extends Remote {
    /**
     * Adds the given document to the application.
     * 
     * @param uri
     *            the URI of the document.
     * @param doc
     *            the document to add.
     * @exception BadFetchError
     *                error in the document.
     * @exception RemoteException
     *            Error in remote method call.
     */
    void addDocument(final URI uri, final VoiceXmlDocument doc)
            throws RemoteException, BadFetchError;

    /**
     * Retrieves the current document.
     * 
     * @return the current document.
     * 
     * @exception RemoteException
     *            Error in remote method call.
     */
    VoiceXmlDocument getCurrentDocument() throws RemoteException;

    /**
     * Retrieves the URI of the application.
     * 
     * @return URI of the application.
     * 
     * @exception RemoteException
     *            Error in remote method call.
     */
    URI getApplication() throws RemoteException;

    /**
     * Sets the new root document.
     * 
     * @param document
     *            the new root document.
     * @exception BadFetchError
     *                error in the document.
     * @exception RemoteException
     *            Error in remote method call.
     */
    void setRootDocument(final VoiceXmlDocument document)
            throws RemoteException, BadFetchError;

    /**
     * Checks, if the document with the given <code>uri</code> is loaded.
     * 
     * @param uri
     *            the URI to check.
     * @return <code>true</code> if the document is loaded.
     * @exception RemoteException
     *            Error in remote method call.
     */
    boolean isLoaded(final URI uri) throws RemoteException;

    /**
     * Retrieves the base URI.
     * 
     * @return the base URI.
     * @exception RemoteException
     *            Error in remote method call.
     */
    URI getXmlBase() throws RemoteException;

    /**
     * Converts the given {@link URI} into a hierarchical URI. If the given
     * {@link URI} is a relative URI, it is expanded using the application URI.
     * 
     * @param uri
     *            the URI to resolve.
     * @return Hierarchical URI.
     * @exception RemoteException
     *            Error in remote method call.
     */
    URI resolve(final URI uri) throws RemoteException;

    /**
     * Converts the given {@link URI} into a hierarchical URI. If the given
     * {@link URI} is a relative URI, it is expanded using the
     * <code>baseUri</code>.
     * 
     * @param baseUri
     *            the base URI.
     * @param uri
     *            the URI to resolve.
     * @return Hierarchical URI.
     * @exception RemoteException
     *            Error in remote method call.
     */
    URI resolve(final URI baseUri, final URI uri) throws RemoteException;

    /**
     * Sets the last result.
     * @param lastresult the last result to set.
     * @exception RemoteException
     *            Error in remote method call.
     */
    void setLastResult(final List<LastResult> lastresult)
            throws RemoteException;

    /**
     * Retrieves information about the last recognition to occur within this
     * application. It is an array of elements where each element, represents a
     * possible result.
     * @return last recognition result information, maybe {@code null}.
     * @exception RemoteException
     *            Error in remote method call.
     */
    List<LastResult> getLastResult() throws RemoteException;
}

