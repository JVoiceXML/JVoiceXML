/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.rmi.server.Skeleton;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.JndiSupport;
import org.jvoicexml.client.jndi.RemoteJVoiceXml;
import org.jvoicexml.client.jndi.RemoteMappedDocumentRepository;
import org.jvoicexml.client.jndi.Stub;
import org.jvoicexml.documentserver.schemestrategy.DocumentMap;
import org.jvoicexml.jndi.classserver.ClassServer;
import org.jvoicexml.jndi.classserver.ClassloaderServer;

/**
 * JNDI support for remote access to the VoiceXML interpreter.
 *
 * <p>
 * Unfortunately there is no automatism to create the remote interface from
 * the original interface, hence custom skeletons are used to simply the
 * remote access.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @see Skeleton
 * @see Stub
 * @since 0.4
 */
public final class JVoiceXmlJndiSupport implements JndiSupport, Runnable {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(JVoiceXmlJndiSupport.class);

    /** Reference to the interpreter. */
    private JVoiceXml jvxml;

    /** The registry. */
    private JVoiceXmlRegistry registry;

    /** JNDI properties. */
    private final Hashtable<String, String> environment;

    /** Starting notification lock. */
    private final Object lock;
    
    /** A possibly thrown exception when starting the JNDI support. */
    private Exception startException;
   
    /** The codebase server. */
    private ClassServer server;

    /** The port of the class server. */
    private int classServerPort;
    
    /**
     * Constructs a new object.
     */
    public JVoiceXmlJndiSupport() {
        environment = new Hashtable<String, String>();
        lock = new Object();
        classServerPort = 9698;
    }

    /**
     * Sets the class server port.
     * @param port port of the class server
     * @since 0.7.9
     */
    public void setClassServerPort(final int port) {
        classServerPort = port;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
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
     * Sets the JNDI environment.
     * @param env the JNDI environment
     * @since 0.7.9
     */
    public void setEnvironment(final Map<String, String> env) {
        environment.putAll(env);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startup() throws IOException {
        server = new ClassloaderServer(classServerPort);
        if (registry == null) {
            throw new IOException("no registry configured");
        }
        // Ensure that the registry is using the correct class loader
        final ClassLoader loader = getClass().getClassLoader();
        final Thread thread = new Thread(this);
        thread.setContextClassLoader(loader);
        thread.start();
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                return;
            }
        }
        if (startException != null) {
            LOGGER.error("error starting the JNDI support", startException);
            throw new IOException("error starting the JNDI support",
                    startException);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        LOGGER.info("starting JNDI support...");
        try {
            registry.start();
            final Context context = getInitialContext();
            if (context == null) {
                LOGGER.warn("unable to create initial context");
                synchronized (lock) {
                    lock.notifyAll();
                }
                return;
            }
    
            // Bind all JVoiceXML objects to the context
            final boolean success = bindObjects(context);
            if (!success) {
                LOGGER.warn("not all object are bound");
            }
            LOGGER.info("...JNDI support started");
        } catch (Exception e) {
            startException = e;
        } finally {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }
    
    /**
     * Retrieves the initial context.
     * @return The context to use or <code>null</code> in case of an error.
     * @since 0.5
     * @exception NamingException
     *                  error obtaining the initial context
     */
    Context getInitialContext() throws NamingException {
        // We take the values from jndi.properties but override the port
        environment.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.rmi.registry.RegistryContextFactory");
        final int port = registry.getPort();
        environment.put(Context.PROVIDER_URL, "rmi://localhost:" + port);
        if (LOGGER.isDebugEnabled()) {
            for (String key : environment.keySet()) {
                final String value = environment.get(key);
                LOGGER.debug("JNDI environment: " + key + " = " + value);
            }
        }
        return new InitialContext(environment);
    }

    /**
     * Binds all JVoiceXML's remote objects.
     * @param context The context to use.
     * @return <code>true</code> if all objects are successfully bound.
     */
    private boolean bindObjects(final Context context) {
        final DocumentMap map = DocumentMap.getInstance();
        try {
            final RemoteMappedDocumentRepository repository =
                    new MappedDocumentRepositorySkeleton(map);
            final RemoteMappedDocumentRepository stub = 
                    (RemoteMappedDocumentRepository) 
                        UnicastRemoteObject.exportObject(repository, 0);
            final String name = 
                    RemoteMappedDocumentRepository.class.getSimpleName();
            context.rebind(name, stub);
            LOGGER.info("bound '" + name + "' to '" 
                    + stub.getClass().getCanonicalName() + "(" 
                    + MappedDocumentRepositorySkeleton.class.getCanonicalName()
                    + ")'");
        } catch (java.rmi.RemoteException | NamingException re) {
            LOGGER.error("error creating the skeleton", re);
            return false;
        }

        try {
            final RemoteJVoiceXml skeleton = new JVoiceXmlSkeleton(context, jvxml);
            final RemoteJVoiceXml stub = 
                    (RemoteJVoiceXml) 
                        UnicastRemoteObject.exportObject(skeleton, 0);
            final String name = 
                    RemoteJVoiceXml.class.getSimpleName();
            context.rebind(name, stub);
            LOGGER.info("bound '" + name + "' to '" 
                    + stub.getClass().getCanonicalName() + "("
                    + JVoiceXmlSkeleton.class.getCanonicalName()
                    + ")'");
        } catch (java.rmi.RemoteException | NamingException re) {
            LOGGER.error("error creating the skeleton", re);
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        LOGGER.info("stopping JNDI support...");
        if (registry != null) {
            registry.shutdown();
        }
        LOGGER.info("...JNDI support stopped");
    }
}
