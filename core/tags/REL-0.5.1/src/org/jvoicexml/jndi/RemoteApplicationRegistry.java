/*
 * File:    $RCSfile: RemoteApplicationRegistry.java,v $
 * Version: $Revision: 1.3 $
 * Date:    $Date: 2006/05/16 07:26:21 $
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

import java.net.URI;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.jvoicexml.Application;

/**
 * Remote interface to enable remote method calls betwenn
 * <code>ApplicationRegistrySkeleton</code> and
 * <code>RemoteApplicationStub</code>.
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
 * @since 0.4
 * @see org.jvoicexml.ApplicationRegistry
 * @see org.jvoicexml.jndi.ApplicationRegistrySkeleton
 * @see org.jvoicexml.jndi.ApplicationRegistryStub
 */
public interface RemoteApplicationRegistry
        extends Remote {
    /**
     * Remote implemetation of
     * <code>ApplicationRegistry.createApplication()</code>.
     * @param applicationId
     *        System wide unique identifier of this application.
     * @param rootUri
     *        URI to retrieve the root document from the document server.
     * @return Created <code>Application</code>.
     * @exception RemoteException
     *            Error in remote method call.
     */
    Application createApplication(final String applicationId,
                                  final URI rootUri)
            throws RemoteException;

    /**
     * Remote implemetation of
     * <code>ApplicationRegistry.cregister()</code>.
     *
     * @param application
     *        The application to add.
     *
     * @exception RemoteException
     *            Error in remote method call.
     */
    void register(final Application application)
            throws RemoteException;

}
