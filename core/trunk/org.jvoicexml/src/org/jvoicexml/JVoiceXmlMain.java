/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jvoicexml.callmanager.CallManager;
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

    /**
     * Construct a new object.
     */
    public JVoiceXmlMain() {
        shutdownSemaphore = new Object();
        setName(JVoiceXmlMain.class.getSimpleName());
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
        final String buildNumber = props.getProperty("jvxml.buildnumber");
        if (!buildNumber.startsWith("${")) {
            str.append(" (Build ");
            str.append(buildNumber);
            str.append(')');
        }
        return str.toString();
    }

    /**
     * {@inheritDoc}
     */
    public Session createSession(final RemoteClient client)
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
    public DocumentServer getDocumentServer() {
        return documentServer;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.3
     */
    public GrammarProcessor getGrammarProcessor() {
        return grammarProcessor;
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
        final JVoiceXmlConfiguration configuration =
            JVoiceXmlConfiguration.getInstance();

        LOGGER.info("----------------------------------------------------");
        LOGGER.info("starting VoiceXML interpreter " + getVersion()
                + "...");

        addShhutdownHook();

        documentServer = configuration.loadObject(DocumentServer.class);

        implementationPlatformFactory = configuration.loadObject(
                ImplementationPlatformFactory.class);
        try {
            implementationPlatformFactory.init(configuration);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return;
        }

        grammarProcessor = configuration.loadObject(GrammarProcessor.class);
        grammarProcessor.init(configuration);

        initCallManager(configuration);
        initJndi(configuration);

        shutdownWaiter = new ShutdownWaiter(this);
        shutdownWaiter.start();

        LOGGER.info("VoiceXML interpreter " + getVersion() + " started.");
    }

    /**
     * Initialization of the JNDI hook.
     * @param configuration current configuration.
     */
    private void initJndi(final JVoiceXmlConfiguration configuration) {
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
     */
    private void initCallManager(final JVoiceXmlConfiguration configuration) {
        callManagers =
            configuration.loadObjects(CallManager.class, "callmanager");
        for (CallManager manager : callManagers) {
            manager.setJVoiceXml(this);
            try {
                manager.start();
            } catch (NoresourceError e) {
                LOGGER.error("error starting call manager", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void shutdown() {
        if (shutdownWaiter == null) {
            return;
        }
        shutdownWaiter.triggerShutdown();
        shutdownWaiter = null;
    }

    /**
     * The shudown sequence.
     */
    void shutdownSequence() {
        try {
            Thread.sleep(POST_SHUTDOWN_DELAY);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
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
            ie.printStackTrace();
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
    private void addShhutdownHook() {
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
