/*
 * File:    $RCSfile: ApplicationRegistry.java,v $
 * Version: $Revision: 1.1 $
 * Date:    $Date: 2006/03/09 04:56:57 $
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

package org.jvoicexml;

import java.net.URI;

/**
 * Registry for all known applications. All applications are identified
 * uniquely by their id.
 *
 * <p>
 * The application reqistry allows for easy access to an application,
 * if only the id of the application is known. This may be used e.g. to
 * create a simple mapping from a a telephone line to the URI of the
 * application's root document.
 * </p>
 *
 * @see Application
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.1 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public interface ApplicationRegistry {
    /**
     * Factory method to create a new <code>Application</code>.
     * @param applicationId
     *        System wide unique identifier of this application.
     * @param rootUri
     *        URI to retrieve the root document from the document server.
     * @return Created <code>Application</code>.
     */
    Application createApplication(final String applicationId,
                                  final URI rootUri);

    /**
     * Adds the given application to the registry.
     *
     * <p>
     * If there is a registered application with the same id, as the given
     * application, the other application will be overwritten with this
     * application. A value of <code>null</code> will be ignored.
     * </p>
     *
     * @param application
     *        The application to add.
     *
     * @see Application#getId()
     */
    void register(final Application application);

    /**
     * Retrieves the application with the given id.
     *
     * @param id
     *        Id of the application to get.
     * @return Application with the given id, or <code>null</code> if there is
     *         no application with this id.
     */
    Application getApplication(final String id);

}
