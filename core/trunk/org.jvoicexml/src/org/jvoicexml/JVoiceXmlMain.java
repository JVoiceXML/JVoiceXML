/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.interpreter.GrammarProcessor;

/**
 * Main class of the JVoiceXML VoiceXML interpreter.
 *
 * <p>
 * This class manages all central resources and serves as a Session
 * factory. It is implemented as a singleton and cannot be instantiated
 * from outside. On startup, it acquires all needed resources and serves
 * in turn as a source to retrieve references to the {@link DocumentServer}
 * and the {@link ImplementationPlatform}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlMain
        extends Thread
        implements JVoiceXmlCore {
    /** Delay after a shutdown request. */
    private static final int POST_SHUTDOWN_DELAY = 1000;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(JVoiceXmlMain.class);

    /** Semaphore to handle the shutdown notification. */
    private final Object shutdownSemaphore;

    /** Reference to the implementation platform. */
    private ImplementationPlatformFactory implementationPlatformFactory;

    /** The document server. */
    private DocumentServer documentServer;

    /** The grammar processor. */
    private GrammarProcessor grammarProcessor;

    /** JNDI support. */
    private JndiSupport jndi;

    /** The call managers. */
    private Collection<CallManager> callManagers;

    /** The shutdown hook. */
    private Thread shutdownHook;

    /** Waiter for a shutdown request. */
    private ShutdownWaiter shutdownWaiter;

    /** The used configuration object. */
    private Configuration configuration;

    /**
     * Construct a new object.
     */
    public JVoiceXmlMain() {
        this(null);
    }

    /**
     * Construct a new object.
     * @param config location of the config folder.
     */
    public JVoiceXmlMain(final File config) {
        LOGGER.info("----------------------------------------------------");
        LOGGER.info("starting VoiceXML interpreter " + getVersion()
                + "...");

        shutdownSemaphore = new Object();
        setName(JVoiceXmlMain.class.getSimpleName());
        if (config != null) {
            JVoiceXmlConfiguration.createInstance(config);
        }
    }

    /**
     * {@inheritDoc}
     *
     * The version information is created by
     * <code>
     * &lt;VERSION_MAJOR&gt>.&lt;VERSION_MINOR&gt;.&lt;VERSION_BUGFIX_LEVEL&gt;
     * .&lt;EA|GA&gt;[.&lt;BUILD_NUMBER&gt;]
     * </code>.
     */
    public String getVersion() {
        InputStream in =
            JVoiceXml.class.getResourceAsStream("/jvoicexml.version");
        if (in == null) {
            return "unmanaged version";
        }
        final Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            return "unmanaged version";
        }

        final StringBuilder str = new StringBuilder();
        final String version = props.getProperty("jvxml.version");
        str.append(version);
        final String buildNumber = props.getProperty("jvxml.revision");
        if (!buildNumber.startsWith("${")) {
            str.append(" (Revision ");
            str.append(buildNumber);
            str.append(')');
        }
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(final ConnectionInformation client)
            throws ErrorEvent {
        if (shutdownWaiter == null) {
            throw new NoresourceError("VoiceXML interpreter shut down!");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new session...");
        }

        final ImplementationPlatform platform =
            implementationPlatformFactory.getImplementationPlatform(client);
        final Session session =
                new org.jvoicexml.interpreter.JVoiceXmlSession(platform, this,
                        client);
        platform.setSession(session);

        LOGGER.info("created session " + session.getSessionID());

        return session;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        if (configuration == null) {
            configuration = JVoiceXmlConfiguration.getInstance();
        }
        return configuration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DocumentServer getDocumentServer() {
        return documentServer;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.3
     */
    @Override
    public GrammarProcessor getGrammarProcessor() {
        return grammarProcessor;
    }

    /**
     * Sets the document server.
     * @param server the document server
     * @since 0.7.4
     */
    public void setDocumentServer(final DocumentServer server) {
        documentServer = server;
    }

    /**
     * Sets the implementation platform factory.
     * <p>
     * The factory may need further configuration. See
     * {@link ImplementationPlatformFactory#init(Configuration)}.
     * </p>
     * @param factory the implementation platform factory
     * @since 0.7.4
     */
    public void setImplementationPlatformFactory(
            final ImplementationPlatformFactory factory) {
        implementationPlatformFactory = factory;
    }

    /**
     * Sets the grammar processor.
     * <p>
     * The factory may need further configuration. See
     * {@link GrammarProcessor#init(Configuration)}.
     * </p>
     * @param processor the grammar processor.
     * @since 0.7.4
     */
    public void setGrammarProcessor(final GrammarProcessor processor) {
        grammarProcessor = processor;
    }

    /**
     * Set the call managers to us.
     * @param managers the call managers.
     * @throws IOException
     *         error starting a call manager.
     * @throws NoresourceError 
     *         error starting a call manager.
     * @since 0.7.4
     */
    public void setCallManager(final Collection<CallManager> managers)
        throws IOException, NoresourceError {
        callManagers = managers;
        for (CallManager manager : callManagers) {
            manager.setJVoiceXml(this);
            manager.start();
            LOGGER.info("started call manager '" + manager + "'");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Starts the VoiceXML interpreter.
     *
     * @since 0.4
     */
    @Override
    public void run() {
        // Initialize the configuration object.
        getConfiguration();

        // Add the shutdown hook
        shutdownWaiter = new ShutdownWaiter(this);
        addShutdownHook();

        // Load configuration
        documentServer = configuration.loadObject(DocumentServer.class);

        implementationPlatformFactory = configuration.loadObject(
                ImplementationPlatformFactory.class);
        try {
            implementationPlatformFactory.init(configuration);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            synchronized (shutdownSemaphore) {
                shutdownSemaphore.notifyAll();
            }
            return;
        }

        grammarProcessor = configuration.loadObject(GrammarProcessor.class);
        try {
            grammarProcessor.init(configuration);
            initCallManager(configuration);
            initJndi(configuration);
        } catch (NoresourceError e) {
            LOGGER.fatal(e.getMessage(), e);
            synchronized (shutdownSemaphore) {
                shutdownSemaphore.notifyAll();
            }
            return;
        } catch (IOException e) {
            LOGGER.fatal(e.getMessage(), e);
            synchronized (shutdownSemaphore) {
                shutdownSemaphore.notifyAll();
            }
            return;
        } catch (ConfigurationException e) {
            LOGGER.fatal(e.getMessage(), e);
            synchronized (shutdownSemaphore) {
                shutdownSemaphore.notifyAll();
            }
            return;
        }
        shutdownWaiter.start();

        LOGGER.info("VoiceXML interpreter " + getVersion() + " started.");
    }

    /**
     * Initialization of the JNDI hook.
     * @param configuration current configuration.
     * @exception IOException error starting the JNDI support
     */
    private void initJndi(final Configuration configuration)
        throws IOException {
        final Collection<JndiSupport> jndis =
            configuration.loadObjects(JndiSupport.class, "jndi");
        if (jndis.size() > 0) {
            final Iterator<JndiSupport> iterator = jndis.iterator();
            jndi = iterator.next();
            jndi.setJVoiceXml(this);
            jndi.startup();
        }
    }

    /**
     * Initializes the call manager.
     * @param configuration current configuration.
     * @exception NoresourceError
     *            error starting the call manager
     * @exception IOException
     *            unable to start a terminal in the call manager
     */
    private void initCallManager(final Configuration configuration)
        throws NoresourceError, IOException {
        callManagers =
            configuration.loadObjects(CallManager.class, "callmanager");
        for (CallManager manager : callManagers) {
            manager.setJVoiceXml(this);
            manager.start();
            LOGGER.info("started call manager '" + manager + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void shutdown() {
        if (shutdownWaiter == null) {
            return;
        }
        LOGGER.info("received shutdown request");
        shutdownWaiter.triggerShutdown();
        shutdownWaiter = null;
    }

    /**
     * The shutdown sequence.
     */
    void shutdownSequence() {
        try {
            Thread.sleep(POST_SHUTDOWN_DELAY);
        } catch (InterruptedException ie) {
            LOGGER.warn(ie.getMessage(), ie);
            return;
        }

        LOGGER.info("shutting down JVoiceXml...");
        // Remove the shutdown hook.
        removeShutdownHook();

        // Release all references to the allocated resources.
        grammarProcessor = null;
        documentServer = null;

        if (implementationPlatformFactory != null) {
            implementationPlatformFactory.close();
            implementationPlatformFactory = null;
        }

        // Delay a bit, to let a remote client disconnect.
        try {
            Thread.sleep(POST_SHUTDOWN_DELAY);
        } catch (InterruptedException ie) {
            LOGGER.warn(ie.getMessage(), ie);
            return;
        }

        // Shutdown JNDI support.
        if (jndi != null) {
            jndi.shutdown();
            jndi = null;
        }

        LOGGER.info("shutdown of JVoiceXML complete!");
        synchronized (shutdownSemaphore) {
            shutdownSemaphore.notifyAll();
        }
    }

    /**
     * Adds the shutdown hook.
     *
     * @since 0.4
     */
    private void addShutdownHook() {
        final JVoiceXmlShutdownHook hook =
                new JVoiceXmlShutdownHook(this);
        shutdownHook = new Thread(hook);
        shutdownHook.setName("ShutdownHook");

        final Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(shutdownHook);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added shutdown hook");
        }
    }

    /**
     * Removes the shutdown hook.
     *
     * @since 0.4
     */
    private void removeShutdownHook() {
        if (shutdownHook == null) {
            return;
        }

        final Runtime runtime = Runtime.getRuntime();
        runtime.removeShutdownHook(shutdownHook);

        shutdownHook = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removed shutdown hook");
        }
    }

    /**
     * Waits until the VoiceXML interpreter has been shutdown.
     *
     * @since 0.4
     */
    public void waitShutdownComplete() {
        try {
            synchronized (shutdownSemaphore) {
                shutdownSemaphore.wait();
            }
        } catch (InterruptedException ie) {
            LOGGER.error("wait event was interrupted", ie);
        }
    }
}
