/*
 * File:    $RCSfile: JndiSupport.java,v $
 * Version: $Revision: 1.6 $
 * Date:    $Date: 2006/06/07 07:40:49 $
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

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.jvoicexml.ApplicationRegistry;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * JNDI support for remote client access to the VoiceXML interpreter.
 *
 * <p>
 * This JNDI implementation uses RMI underneath. Clients should work with
 * the original interface, which is implemented by a <code>Stub</code>.
 * The <code>Stub</code> uses RMI to call methods of the <code>Skeleton</code>.
 * This requires the existance of a <code>Remote</code> interface, which
 * mirrors all methods of the original interface for remote method calling.
 * The <code>Skeleton</code> forwards all calls to the original implementation.
 * </p>
 *
 * <p>
 * <b>Note:</b> <code>Stub</code> and <code>Skeleton</code> in this sense
 * must not be confused with RMI stubs and skeletons.
 * </p>
 *
 * <p>
 * Unfortunately there is no automatism to create the remote interface from
 * the original interface.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.6 $
 *
 * @see Skeleton
 * @see Stub
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.4
 */
public final class JndiSupport {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JndiSupport.class);

    /** Reference to the interpreter. */
    private final JVoiceXmlCore jvxml;

    /** The registry. */
    private Registry registry;

    /**
     * Constructs a new object.
     * @param jvoicexml Referenceto the intrpreter.
     */
    public JndiSupport(final JVoiceXmlCore jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * Starts the service.
     */
    public void startup() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("starting jndi support...");
        }

        try {
            registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        } catch (RemoteException re) {
            LOGGER.error("error starting the registry", re);

            return;
        }

        final Context context = getInitialContext();
        if (context == null) {
            return;
        }

        final boolean success = bindObjects(context);

        if (!success) {
            LOGGER.warn("not all object are bound");
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...jndi support started");
        }
    }

    /**
     * Retrieves the initial context.
     * @return The context to use or <code>null</code> in case of an error.
     * @since 0.5
     */
    Context getInitialContext() {
        try {
            return new InitialContext();
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtainint the initial context", ne);

            return null;
        }
    }

    /**
     * Binds all JVoiceXML's remote obejcts.
     * @param context The context to use.
     * @return <code>true</code> if all objects are successfully bound.
     */
    private boolean bindObjects(final Context context) {
        final ApplicationRegistry applicationRegistry =
                jvxml.getApplicationRegistry();
        try {
            final Skeleton skeleton =
                    new ApplicationRegistrySkeleton(applicationRegistry);
            final Stub stub = new ApplicationRegistryStub();
            bind(context, skeleton, stub);
        } catch (java.rmi.RemoteException re) {
            LOGGER.error("error creating the skeleton", re);

            return false;
        }

        final DocumentMap map = DocumentMap.getInstance();
        try {
            final Skeleton skeleton =
                    new MappedDocumentRepositorySkeleton(map);
            final Stub stub = new MappedDocumentRepositoryStub();
            bind(context, skeleton, stub);
        } catch (java.rmi.RemoteException re) {
            LOGGER.error("error creating the skeleton", re);

            return false;
        }

        try {
            final Skeleton skeleton = new JVoiceXmlSkeleton(jvxml);
            final Stub stub = new JVoiceXmlStub();
            bind(context, skeleton, stub);
        } catch (java.rmi.RemoteException re) {
            LOGGER.error("error creating the skeleton", re);

            return false;
        }

        return true;
    }

    /**
     * Binds the given stub and skeleton.
     *
     * <p>
     * Both have to be exported. The skeleton has to be accessed from the
     * stub via RMI and the stub has to be exported to be accessable via
     * JNDI.
     * </p>
     *
     * @param context The context to bind skeleton and stub.
     * @param skeleton The skeleton to bind.
     * @param stub The stub to bind.
     */
    static void bind(final Context context, final Skeleton skeleton,
                     final Stub stub) {
        final String skeletonName;
        try {
            skeletonName = skeleton.getSkeletonName();
        } catch (RemoteException re) {
            LOGGER.error("error retrieving the skelton name", re);
            return;
        }

        final String stubName = stub.getStubName();

        try {
            context.rebind(skeletonName, skeleton);
            context.rebind(stubName, stub);
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("naming exception while exporting '" + skeletonName
                         + "'", ne);

            return;
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("bound '" + stubName + "' to '"
                        + stub.getClass().getName() + "'");
        }
    }

    /**
     * Stops the service.
     *
     * @todo The service is not properly stopped. Unexporting the registry
     * seems to be not sufficient.
     */
    public void shutdown() {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("stopping jndi support...");
            }

            UnicastRemoteObject.unexportObject(registry, true);
        } catch (java.rmi.NoSuchObjectException nsoe) {
            LOGGER.error("error in shutdown", nsoe);
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("...jndisupport  stopped");
        }
    }
}
