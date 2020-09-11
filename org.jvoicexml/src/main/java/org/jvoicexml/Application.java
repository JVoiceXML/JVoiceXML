/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml;

import java.net.URI;
import java.util.List;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * An <code>Application</code> is a set of documents sharing the same
 * application root document.
 * 
 * <p>
 * Whenever the user interacts with a document in an application, its
 * application root document is also loaded. The application root document
 * remains loaded while the user is transitioning between other documents in the
 * same application, and it is unloaded when the user transitions to a document
 * that is not in the application. While it is loaded, the application root
 * document's variables are available to the other documents as application
 * variables, and its grammars remain active for the duration of the
 * application.
 * </p>
 * 
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision$
 * @since 0.4
 */
public interface Application {
    /**
     * Adds the given document to the application.
     * 
     * @param uri
     *            the URI of the document.
     * @param doc
     *            the document to add.
     * @exception BadFetchError
     *                error in the document.
     * @since 0.6
     */
    void addDocument(final URI uri, final VoiceXmlDocument doc)
            throws BadFetchError;

    /**
     * Retrieves the current document.
     * 
     * @return the current document.
     * 
     * @since 0.6
     */
    VoiceXmlDocument getCurrentDocument();

    /**
     * Retrieves the URI of the application.
     * 
     * @return URI of the application.
     * 
     * @since 0.6
     * @exception BadFetchError error obtaining the application
     */
    URI getApplication() throws BadFetchError ;

    /**
     * Sets the new root document.
     * 
     * @param document
     *            the new root document.
     * @exception BadFetchError
     *                error in the document.
     */
    void setRootDocument(final VoiceXmlDocument document) throws BadFetchError;

    /**
     * Checks, if the document with the given <code>uri</code> is loaded.
     * 
     * @param uri
     *            the URI to check.
     * @return <code>true</code> if the document is loaded.
     */
    boolean isLoaded(final URI uri);

    /**
     * Retrieves the base URI.
     * 
     * @return the base URI.
     */
    URI getXmlBase();

    /**
     * Converts the given {@link URI} into a hierarchical URI. If the given
     * {@link URI} is a relative URI, it is expanded using the application URI.
     * 
     * @param uri
     *            the URI to resolve.
     * @return Hierarchical URI.
     * @exception error resolving the URI
     */
    URI resolve(final URI uri) throws BadFetchError;

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
     * @throws BadFetchError 
     */
    URI resolve(final URI baseUri, final URI uri) throws BadFetchError;

    /**
     * Sets the last result.
     * @param lastresult the last result to set.
     * @since 0.7.7
     */
    void setLastResult(final List<LastResult> lastresult);

    /**
     * Retrieves information about the last recognition to occur within this
     * application. It is an array of elements where each element, represents a
     * possible result.
     * @return last recognition result information, maybe {@code null}.
     * @since 0.7.7
     */
    List<LastResult> getLastResult();
}
