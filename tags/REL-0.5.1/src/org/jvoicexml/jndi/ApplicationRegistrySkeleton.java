/*
 * File:    $RCSfile: ApplicationRegistrySkeleton.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/03/10 05:07:28 $
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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;

/**
 * Skeleton for the <code>ApplicationRegistry</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 * @see org.jvoicexml.ApplicationRegistry
 */
class ApplicationRegistrySkeleton
        extends UnicastRemoteObject implements RemoteApplicationRegistry,
        Skeleton {
    /** The serial version UID. */
    static final long serialVersionUID = -4876054740667570993L;

    /** The encapsulataed <code>ApplicationRegistry</code>. */
    private ApplicationRegistry registry;

    /**
     * Constructs a new object.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public ApplicationRegistrySkeleton()
            throws RemoteException {
    }

    /**
     * Constructs a new object with the given registry.
     * @param reg The registry.
     * @throws RemoteException
     *         Error creating the remote object.
     */
    public ApplicationRegistrySkeleton(final ApplicationRegistry reg)
            throws RemoteException {
        registry = reg;
    }

    /**
     * {@inheritDoc}
     */
    public String getSkeletonName() {
        return RemoteApplicationRegistry.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    public Application createApplication(final String applicationId,
                                               final URI rootUri) {
        if (registry == null) {
            return null;
        }

        return registry.createApplication(applicationId, rootUri);
    }

    /**
     * {@inheritDoc}
     */
    public void register(final Application application) {
        if (registry == null) {
            return;
        }

        registry.register(application);
    }
}
