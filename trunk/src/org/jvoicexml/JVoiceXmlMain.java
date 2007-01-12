/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.jndi.JndiSupport;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Main class of the JVoiceXML VoiceXML interpreter.
 *
 * <p>
 * This class manages all central resources and serves as a Session
 * factory. It is implemented as a singleton and cannot be instantiated
 * from outside. On startup, it acquires all needed resources and serves
 * in turn as a source to retriebe references to the {@link DocumentServer},
 * {@link ImplementationPlatform} and  the {@link ApplicationRegistry}.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.Session
 * @see org.jvoicexml.ApplicationRegistry
 * @see org.jvoicexml.DocumentServer
 * @see org.jvoicexml.ImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlMain
        extends Thread
        implements JVoiceXmlCore, Runnable {
    /** Delay after a shutdown request. */
    private static final int POST_SHUTDOWN_DELAY = 1000;

    /** Logger for this class. */
    private static Logger logger;

    /** Major version number.*/
    private static final int VERSION_MAJOR = 0;

    /** Minor version number. */
    private static final int VERSION_MINOR = 5;

    /** Bugfix level. */
    private static final int VERSION_BUGFIX_LEVEL = 1;

    /** Flag, if the VoiceXML interpreter is alive. */
    private boolean shutdown;

    /** Semphore to handle the shutdown notification. */
    private final Object shutdownSemaphore;

    /** Reference to the implementation platform. */
    private ImplementationPlatformFactory implementationPlatformFactory;

    /** The document server. */
    private DocumentServer documentServer;

    /** The grammar procesor. */
    private GrammarProcessor grammarProcessor;

    /** JNDI support. */
    private JndiSupport jndi;

    /** The shutdown hook. */
    private Thread shutdownHook;

    /**
     * Construct a new object.
     */
    private JVoiceXmlMain() {
        logger = LoggerFactory.getLogger(JVoiceXmlMain.class);

        shutdownSemaphore = new Object();
    }

    /**
     * {@inheritDoc}
     *
     * The version information is created by
     * <code>
     * &lt;VERSION_MAJOR&gt>.&lt;VERSION_MINOR&gt;.&lt;VERSION_BUGFIX_LEVEL&gt;
     * </code>.
     */
    public String getVersion() {
        return VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_BUGFIX_LEVEL;
    }

    /**
     * {@inheritDoc}
     */
    public Session createSession(final RemoteClient client)
            throws ErrorEvent {
        if (shutdown) {
            logger.warn("VoiceXML interpreter already shut down!");

            return null;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("creating new session...");
        }

        final ImplementationPlatform platform =
            implementationPlatformFactory.getImplementationPlatform(client);

        final Session session =
                new JVoiceXmlSession(platform, this);

        if (logger.isDebugEnabled()) {
            logger.debug("created session " + session.getSessionID());
        }

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
    public void run() {
        if (logger.isInfoEnabled()) {
            logger.info("----------------------------------------------------");
            logger.info("starting VoiceXML interpreter " + getVersion()
                        + "...");
        }

        addShhutdownHook();

        final JVoiceXmlConfiguration configuration =
                JVoiceXmlConfiguration.getInstance();

        documentServer = configuration.loadObject(DocumentServer.class,
                                                  DocumentServer.CONFIG_KEY);

        implementationPlatformFactory = configuration.loadObject(
                ImplementationPlatformFactory.class,
                ImplementationPlatformFactory.CONFIG_KEY);

        grammarProcessor = configuration.loadObject(GrammarProcessor.class,
                GrammarProcessor.CONFIG_KEY);

        jndi = new JndiSupport(this);
        jndi.startup();

        if (logger.isInfoEnabled()) {
            logger.info("VoiceXML interpreter started.");
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized void shutdown() {
        if (shutdown) {
            return;
        }

        if (logger.isInfoEnabled()) {
            logger.info("shutting down JVoiceXml...");
        }

        // Release all references to the allocated resources.
        shutdown = true;

        grammarProcessor = null;
        documentServer = null;

        if (implementationPlatformFactory != null) {
            implementationPlatformFactory.close();
            implementationPlatformFactory = null;
        }

        if (logger.isInfoEnabled()) {
            logger.info("shutdown of JVoiceXML complete!");
        }

        synchronized (shutdownSemaphore) {
            shutdownSemaphore.notify();
        }
    }

    /**
     * Performs some cleanup after a shutdown has been calles.
     *
     * <p>
     * This is necessary,since some functionality, like JNDI support might
     * be needed until a shutdown is terminated.
     * </p>
     *
     * @since 0.4
     */
    public void postShutdown() {
        // Delay a bit, to let a remote client disconnet.
        try {
            Thread.sleep(POST_SHUTDOWN_DELAY);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        // Remove the shutdown hook.
        removeShutdownHook();

        // Shutdown JNDI support.
        if (jndi != null) {
            jndi.shutdown();
            jndi = null;
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

        if (logger.isInfoEnabled()) {
            logger.info("added shutdown hook");
        }
    }

    /**
     * Removes the shutdwon hook.
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

        if (logger.isInfoEnabled()) {
            logger.info("removed shutdown hook");
        }
    }

    /**
     * Waits until the VoiceXML inpreter has been shutdown.
     *
     * @since 0.4
     */
    public void waitShutdownComplete() {
        while (!shutdown) {
            try {
                synchronized (shutdownSemaphore) {
                    shutdownSemaphore.wait();
                }
            } catch (InterruptedException ie) {
                logger.error("wait event was interrupted", ie);
            }
        }
    }

    /**
     * The main method, which starts the interpreter.
     *
     * @param args Command line arguments. None expected.
     *
     * @since 0.4
     */
    public static void main(final String[] args) {
        final JVoiceXmlMain jvxml = new JVoiceXmlMain();

        // Start the interpreter as a thread.
        jvxml.setName("JVoiceXMLMain");
        jvxml.start();

        // Wait until the interpreter thread terminates.
        jvxml.waitShutdownComplete();

        jvxml.postShutdown();

        /** @todo There are some threads running. Stop them. */
        System.exit(0);
    }
}
