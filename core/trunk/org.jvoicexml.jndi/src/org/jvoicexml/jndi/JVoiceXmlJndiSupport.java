/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/jndi/JVoiceXmlJndiSupport.java $
 * Version: $LastChangedRevision: 1496 $
 * Date:    $LastChangedDate: 2009-03-11 09:39:46 +0100 (Mi, 11 Mrz 2009) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JndiSupport;
import org.jvoicexml.client.jndi.JVoiceXmlStub;
import org.jvoicexml.client.jndi.MappedDocumentRepositoryStub;
import org.jvoicexml.client.jndi.Stub;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;

/**
 * JNDI support for remote access to the VoiceXML interpreter.
 *
 * <p>
 * This JNDI implementation uses RMI underneath. Clients should work with
 * the original interface, which is implemented by a {@link Stub}
 * The {@link Stub} uses RMI to call methods of the {@link Skeleton}
 * This requires the existence of a {@link java.rmi.Remote} interface, which
 * mirrors all methods of the original interface for remote method calling.
 * The {@link Skeleton} forwards all calls to the original implementation.
 * </p>
 *
 * <p>
 * <b>Note:</b> {@link Stub} and {@link Skeleton} in this sense
 * must not be confused with RMI stubs and skeletons.
 * </p>
 *
 * <p>
 * Unfortunately there is no automatism to create the remote interface from
 * the original interface.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $LastChangedRevision: 1496 $
 *
 * @see Skeleton
 * @see Stub
 *
 * @since 0.4
 */
public final class JVoiceXmlJndiSupport implements JndiSupport {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlJndiSupport.class);

    /** Reference to the interpreter. */
    private JVoiceXml jvxml;

    /** The registry. */
    private JVoiceXmlRegistry registry;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlJndiSupport() {
    }

    /**
     * {@inheritDoc}
     */
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
        jvxml = jvoicexml;
    }

    /**
     * Sets the registry to use.
     * @param reg the registry.
     *
     * @since 0.5.5
     */
    public void setRegistry(final JVoiceXmlRegistry reg) {
        registry = reg;
    }

    /**
     * {@inheritDoc}
     */
    public void startup() throws IOException {
        LOGGER.info("starting JNDI support...");
        if (registry != null) {
            registry.start();
        }

        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager == null) {
            securityManager = new RMISecurityManager();
            System.setSecurityManager(securityManager);
            LOGGER.info("security manager set to '" + securityManager + "'");
        }
        final Context context = getInitialContext();
        if (context == null) {
            LOGGER.warn("unable to create initial context");
            return;
        }

        final boolean success = bindObjects(context);
        if (!success) {
            LOGGER.warn("not all object are bound");
        }

        LOGGER.info("...JNDI support started");
    }

    /**
     * Retrieves the initial context.
     * @return The context to use or <code>null</code> in case of an error.
     * @since 0.5
     */
    Context getInitialContext() {
        final Hashtable<String, String> environment =
            new Hashtable<String, String>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory");
        final int port = registry.getPort();
        environment.put(Context.PROVIDER_URL, "rmi://localhost:" + port);
        try {
            return new InitialContext(environment);
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the initial context", ne);

            return null;
        }
    }

    /**
     * Binds all JVoiceXML's remote objects.
     * @param context The context to use.
     * @return <code>true</code> if all objects are successfully bound.
     */
    private boolean bindObjects(final Context context) {
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
            final Skeleton skeleton = new JVoiceXmlSkeleton(context, jvxml);
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
     * stub via RMI and the stub has to be exported to be accessible via
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
            LOGGER.error("error retrieving the skeleton name", re);
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

        LOGGER.info("bound '" + stubName + "' to '"
                + stub.getClass().getName() + "'");
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        LOGGER.info("stopping JNDI support...");

        if (registry != null) {
            registry.shutdown();
        }

        LOGGER.info("...JNDI support stopped");
    }
}
