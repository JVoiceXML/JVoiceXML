/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.profile.Profile;

/**
 * Main class of the JVoiceXML VoiceXML interpreter.
 *
 * <p>
 * This class manages all central resources and serves as a Session factory. It
 * is implemented as a singleton and cannot be instantiated from outside. On
 * startup, it acquires all needed resources and serves in turn as a source to
 * retrieve references to the {@link DocumentServer} and the
 * {@link ImplementationPlatform}.
 * </p>
 *
 * <p>
 * During its life, the interpreter passes several states as shown in the
 * following figure. JVoiceXML can only process calls while it is in the running
 * state.<br>
 * <img src="doc-files/interpreter-lifecycle.jpg" alt="interpreter lifecyle">
 * </p>
 * 
 *
 * @author Dirk Schnelle-Walka
 */
public final class JVoiceXmlMain extends Thread implements JVoiceXmlCore {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(JVoiceXmlMain.class);

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

    /** Known profiles. */
    private final Map<String, Profile> profiles;

    /** The shutdown hook. */
    private Thread shutdownHook;

    /** Waiter for a shutdown request. */
    private ShutdownWaiter shutdownWaiter;

    /** The used configuration object. */
    private Configuration configuration;

    /** Registered listeners to JVoiceXml. */
    private final Collection<JVoiceXmlMainListener> listeners;

    /** The state of the interpreter. */
    private InterpreterState state;

    /**
     * Construct a new object.
     */
    public JVoiceXmlMain() {
        this(null);
    }

    /**
     * Construct a new object with the given configuration object.
     * 
     * @param config
     *            the initial configuration
     */
    public JVoiceXmlMain(final Configuration config) {
        LOGGER.info("----------------------------------------------------");
        LOGGER.info("starting VoiceXML interpreter " + getVersion() + "...");
        reportEnvironmentInformation();
        
        shutdownSemaphore = new Object();
        setName(JVoiceXmlMain.class.getSimpleName());
        configuration = config;
        listeners = new java.util.ArrayList<JVoiceXmlMainListener>();
        profiles = new java.util.HashMap<String, Profile>();
        state = InterpreterState.STARTED;
        LOGGER.info("interpreter state " + state);
    }

    /**
     * {@inheritDoc}
     *
     * The version information is created by <code>
     * &lt;VERSION_MAJOR&gt;.&lt;VERSION_MINOR&gt;.&lt;VERSION_BUGFIX_LEVEL&gt;.&lt;EA|GA&gt;
     * </code>.
     */
    public String getVersion() {
        InputStream in = JVoiceXml.class
                .getResourceAsStream("/jvoicexml.version");
        if (in == null) {
            return "unmanaged version";
        }
        final Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            return "unmanaged version";
        }

        return props.getProperty("jvxml.version");
    }

    /**
     * Provides logging information about the used environment like host system
     * and sued Java version
     * 
     * @since 0.7.9
     */
    private void reportEnvironmentInformation() {
        final Package pkg = Runtime.class.getPackage();
        final String version = pkg.getImplementationVersion();
        final String vendor = pkg.getImplementationVendor();
        final String title = pkg.getImplementationTitle();
        LOGGER.info("Java:\t\t\t" + title + " " + version);
        LOGGER.info("Java vendor:\t\t" + vendor);
        final String os = System.getProperty("os.name", "generic");
        LOGGER.info("Operating system:\t" + os);
        if (LOGGER.isDebugEnabled()) {
            final ClassLoader loader = getClass().getClassLoader();
            LOGGER.debug("Class loader: " + loader);
            if (loader instanceof URLClassLoader) {
                @SuppressWarnings("resource")
                final URLClassLoader urlloader = (URLClassLoader) loader;
                final URL[] urls = urlloader.getURLs();
                for (URL url : urls) {
                    LOGGER.debug("- " + url);
                }
            }
        }
    }
    
    /**
     * Adds the given listener.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(final JVoiceXmlMainListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * removes the given listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(final JVoiceXmlMainListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public Session createSession(ConnectionInformation info,
            SessionIdentifier id) throws ErrorEvent {
        if (state != InterpreterState.RUNNING) {
            throw new NoresourceError(
                    "JVoiceXML not running. Can't create a session!");
        }

        final ImplementationPlatform platform = implementationPlatformFactory
                .getImplementationPlatform(info);
        return createSession(info, platform, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Session createSession(final ConnectionInformation info,
            final ImplementationPlatform platform, final SessionIdentifier id)
                    throws ErrorEvent {
        if (state != InterpreterState.RUNNING) {
            throw new NoresourceError(
                    "JVoiceXML not running. Can't create a session!");
        }
        LOGGER.info("creating new session...");

        // Create the session and link it with the implementation platform
        final String profileName = info.getProfile();
        final Profile profile = profiles.get(profileName);
        if (profile == null) {
            throw new NoresourceError(
                    "Unable to find a profile named '" + profileName + "'");
        }
        final Session session = new org.jvoicexml.interpreter.JVoiceXmlSession(
                platform, this, info, profile, id);
        platform.setSession(session);
        LOGGER.info("created session " + session.getSessionId().getId());

        return session;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Configuration getConfiguration() {
        // A configuration might be known by a previous call to this method
        // or since it has been passed as an argument when creating this
        // object.
        if (configuration == null) {
            final ServiceLoader<Configuration> services = ServiceLoader
                    .load(Configuration.class);
            try {
                for (Configuration config : services) {
                    configuration = config;
                    break;
                }
            } catch (ServiceConfigurationError e) {
                LOGGER.error("unable to load configuration", e);
                return null;
            }
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
     * {@inheritDoc}
     */
    @Override
    public ImplementationPlatformFactory getImplementationPlatformFactory() {
        return implementationPlatformFactory;
    }
    
    /**
     * Sets the implementation platform factory.
     * <p>
     * The factory may need further configuration. See
     * {@link ImplementationPlatformFactory#init(Configuration)}.
     * </p>
     * 
     * @param factory
     *            the implementation platform factory
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
     * 
     * @param processor
     *            the grammar processor.
     * @since 0.7.4
     */
    public void setGrammarProcessor(final GrammarProcessor processor) {
        grammarProcessor = processor;
    }

    /**
     * Set the call managers to use.
     * 
     * @param managers
     *            the call managers.
     * @throws IOException
     *             error starting a call manager.
     * @throws NoresourceError
     *             error starting a call manager.
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
        state = InterpreterState.ALLOCATING_RESOURCES;
        LOGGER.info("interpreter state " + state);

        // Initialize the configuration object.
        final Configuration config = getConfiguration();
        if (config == null) {
            final Exception exception = new ConfigurationException(
                    "no configuration available");
            abortStartup(exception);
            return;
        }
        LOGGER.info("using configuration '"
                + config.getClass().getCanonicalName() + "'");

        // Add the shutdown hook
        shutdownWaiter = new ShutdownWaiter(this);
        addShutdownHook();

        try {
            // Load configuration
            initDocumentServer(config);
            implementationPlatformFactory = configuration
                    .loadObject(ImplementationPlatformFactory.class);
            if (implementationPlatformFactory == null) {
                final Exception exception = new ConfigurationException(
                        "no implementation factory available");
                abortStartup(exception);
            }
            implementationPlatformFactory.init(config);
            grammarProcessor = config.loadObject(GrammarProcessor.class);
            if (grammarProcessor == null) {
                final Exception exception = new ConfigurationException(
                        "no grammar processor available");
                abortStartup(exception);
            }
            grammarProcessor.init(config);
            initCallManager(config);
            initProfiles(config);
            initJndi(config);
        } catch (Exception | NoresourceError e) {
            abortStartup(e);
            return;
        }

        shutdownWaiter.start();
        state = InterpreterState.RUNNING;
        LOGGER.info("interpreter state " + state);
        LOGGER.info("VoiceXML interpreter " + getVersion() + " started.");
        fireJVoiceXmlStarted();
    }

    /**
     * Aborts a startup of JVoiceXML, e.g., because of configuration errors.
     * 
     * @param exception
     *            the exception causing all that trouble
     * @since 0.7.8
     */
    private void abortStartup(final Throwable exception) {
        LOGGER.fatal(exception.getMessage(), exception);
        fireJVoiceXmlStartupError(exception);
        synchronized (shutdownSemaphore) {
            shutdownSemaphore.notifyAll();
        }
    }

    /**
     * Initializes the call manager.
     * 
     * @param config
     *            current configuration.
     * @exception NoresourceError
     *                error starting the call manager
     * @throws Exception 
     *          error starting the document server
     */
    private void initDocumentServer(final Configuration config)
            throws NoresourceError, Exception {
        documentServer = config.loadObject(DocumentServer.class);
        if (documentServer == null) {
            final Exception exception = new ConfigurationException(
                    "no document server available");
            abortStartup(exception);
        }
        if (documentServer instanceof Configurable) {
            final Configurable configurable = (Configurable) documentServer;
            configurable.init(config);
        }
        documentServer.start();
    }
    
    /**
     * Initialization of the JNDI hook.
     * 
     * @param config
     *            current configuration.
     * @exception IOException
     *                error starting the JNDI support
     * @exception ConfigurationException
     *                error loading the configuration
     */
    private void initJndi(final Configuration config)
            throws IOException, ConfigurationException {
        final Collection<JndiSupport> jndis = config
                .loadObjects(JndiSupport.class, "jndi");
        if (jndis == null) {
            return;
        }
        if (jndis.size() > 0) {
            final Iterator<JndiSupport> iterator = jndis.iterator();
            jndi = iterator.next();
            jndi.setJVoiceXml(this);
            jndi.startup();
        }
    }

    /**
     * Initializes the call manager.
     * 
     * @param config
     *            current configuration.
     * @exception NoresourceError
     *                error starting the call manager
     * @exception IOException
     *                unable to start a terminal in the call manager
     * @exception ConfigurationException
     *                error loading the configuration
     */
    private void initCallManager(final Configuration config)
            throws NoresourceError, IOException, ConfigurationException {
        callManagers = config.loadObjects(CallManager.class, "callmanager");
        if (callManagers == null) {
            return;
        }
        for (CallManager manager : callManagers) {
            manager.setJVoiceXml(this);
            manager.start();
            LOGGER.info("started call manager '" + manager + "'");
        }
    }

    /**
     * Initializes the call manager.
     * 
     * @param config
     *            current configuration.
     * @exception NoresourceError
     *                error starting the call manager
     * @exception IOException
     *                unable to start a terminal in the call manager
     * @exception ConfigurationException
     *                error loading the configuration
     */
    private void initProfiles(final Configuration config)
            throws NoresourceError, IOException, ConfigurationException {
        final Collection<Profile> loadedProfiles = config
                .loadObjects(Profile.class, "profile");
        if (loadedProfiles != null) {
            for (Profile profile : loadedProfiles) {
                final String name = profile.getName();
                profiles.put(name, profile);
                LOGGER.info("added profile '" + name + "'");
            }
        }

        // Report available profiles
        if (profiles.isEmpty()) {
            LOGGER.warn("no profiles available");
        } else {
            LOGGER.info("available profiles:");
            for (String name : profiles.keySet()) {
                final Profile profile = profiles.get(name);
                LOGGER.info("- '" + profile.getName() + "'");
            }
        }
    }

    /**
     * Shutdown of all registered call managers.
     * 
     * @since 0.7.5
     */
    private void shutdownCallManager() {
        for (CallManager manager : callManagers) {
            manager.stop();
            LOGGER.info("stopped call manager '" + manager + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void shutdown() {
        if ((state == InterpreterState.DEALLOCATING_RESOURCES)
                || (state == InterpreterState.STOPPED)) {
            return;
        }
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
        LOGGER.info("shutting down JVoiceXml...");
        state = InterpreterState.DEALLOCATING_RESOURCES;
        LOGGER.info("interpreter state " + state);

        // Remove the shutdown hook.
        removeShutdownHook();

        // Stop all call managers to stop further calls.
        shutdownCallManager();

        // Release all references to the allocated resources.
        grammarProcessor = null;
        documentServer = null;
        if (implementationPlatformFactory != null) {
            implementationPlatformFactory.close();
            implementationPlatformFactory = null;
        }

      
        // Adapt the interpreter state
        state = InterpreterState.STOPPED;
        LOGGER.info("interpreter state " + state);
        LOGGER.info("shutdown of JVoiceXML complete!");
        synchronized (shutdownSemaphore) {
            shutdownSemaphore.notifyAll();
        }
        // Notify that we are done
        fireJVoiceXmlTerminated();
        
        // Finally terminate JNDI 
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
    private void addShutdownHook() {
        final JVoiceXmlShutdownHook hook = new JVoiceXmlShutdownHook(this);
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
        try {
            runtime.removeShutdownHook(shutdownHook);
        } catch (IllegalStateException e) {
            LOGGER.debug("shutdown already in process");
        }
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
            LOGGER.error("wait shutdown event was interrupted", ie);
        }
    }

    /**
     * Notifies all registered listener about an error when trying to startup
     * JVoiceXML.
     * 
     * @param exception
     *            the causing error
     * 
     * @since 0.7.6
     */
    private void fireJVoiceXmlStartupError(final Throwable exception) {
        synchronized (listeners) {
            for (JVoiceXmlMainListener listener : listeners) {
                listener.jvxmlStartupError(exception);
            }
        }
    }

    /**
     * Notifies all registered listener about the start of JVoiceXML.
     * 
     * @since 0.7.5
     */
    private void fireJVoiceXmlStarted() {
        synchronized (listeners) {
            for (JVoiceXmlMainListener listener : listeners) {
                listener.jvxmlStarted();
            }
        }
    }

    /**
     * Notifies all registered listener about the start of JVoiceXML.
     * 
     * @since 0.7.5
     */
    private void fireJVoiceXmlTerminated() {
        synchronized (listeners) {
            for (JVoiceXmlMainListener listener : listeners) {
                listener.jvxmlTerminated();
            }
        }
    }
}
