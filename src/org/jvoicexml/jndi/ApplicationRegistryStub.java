/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
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

import javax.naming.Context;

import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;

/**
 * Stub for the <code>ApplicationRegistry</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
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
public final class ApplicationRegistryStub
        extends AbstractStub<RemoteApplicationRegistry>
        implements ApplicationRegistry {
    /**
     * Constructs a new object.
     */
    public ApplicationRegistryStub() {
    }

    /**
     * Constructs a new object.
     * @param context The context to use.
     * @since 0.6
     */
    public ApplicationRegistryStub(final Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public String getStubName() {
        return ApplicationRegistry.class.getSimpleName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<RemoteApplicationRegistry> getRemoteClass() {
        return RemoteApplicationRegistry.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class getLocalClass() {
        return ApplicationRegistry.class;
    }


    /**
     * {@inheritDoc}
     */
    public Application createApplication(final String applicationId,
                                         final URI rootUri) {
        final RemoteApplicationRegistry registry = getSkeleton();
        Application application;

        try {
            application = registry.createApplication(applicationId, rootUri);
        } catch (java.rmi.RemoteException re) {
            application = null;
            clearSkeleton();

            re.printStackTrace();
        }

        return application;
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this method.
     */
    public Application getApplication(final String id) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void register(final Application application) {
        final RemoteApplicationRegistry registry = getSkeleton();

        try {
            registry.register(application);
        } catch (java.rmi.RemoteException re) {
            clearSkeleton();

            re.printStackTrace();
        }
    }
}
