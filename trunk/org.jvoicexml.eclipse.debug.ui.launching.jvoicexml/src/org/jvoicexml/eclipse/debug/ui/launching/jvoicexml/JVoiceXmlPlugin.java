/*
 * JVoiceXML VTP Plugin
 *
 * Copyright (C) 2006 Dirk Schnelle
 *
 * Copyright (c) 2006 Dirk Schnelle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jvoicexml.eclipse.debug.ui.launching.jvoicexml;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 * 
 * <p>
 * The plugin consists of three main parts:
 * <ol>
 * <li>JVoiceXmlBrowser</li>
 * <li>JVoiceXmlBrowserUI</li>
 * <li>LoggingReceiver</li>
 * </ol>
 * </p>
 * 
 * @author Dirk Schnelle
 * 
 * @see org.jvoicexml.eclipse.debug.ui.launching.jvoicexml.JVoiceXmlBrowser
 * @see org.jvoicexml.eclipse.debug.ui.launching.jvoicexml.JVoiceXmlBrowserUI
 * @see org.jvoicexml.eclipse.debug.ui.launching.jvoicexml.LoggingReceiver
 */
public final class JVoiceXmlPlugin
        extends AbstractUIPlugin {

    /** Name of the plugin. */
    private static final String PLUGIN_NAME = "JVoiceXml VTP Plugin";

    /** The shared instance. */
    private static JVoiceXmlPlugin plugin;

    /** The logging receiver. */
    private LoggingReceiver receiver;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlPlugin() {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation.
     * 
     * @param context
     *        The context.
     * @exception Exception
     *            Error starting the plugin.
     */
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        Logger root = Logger.getRootLogger();
        PatternLayout layout = new PatternLayout("%6r [%-20.20t] %-5p %30.30c (%6L) %x %m%n");
        Appender console = new ConsoleAppender(layout);
        root.addAppender(console);
    }

    /**
     * This method is called when the plug-in is stopped.
     * 
     * @param context
     *        The context.
     * @exception Exception
     *            Error stopping the plugin.
     */
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);

        if (receiver != null) {
            receiver.close();
            receiver = null;
        }

        plugin = null;
    }

    /**
     * Returns the shared instance.
     * 
     * @return The shared instance.
     */
    public static JVoiceXmlPlugin getDefault() {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in
     * relative path.
     * 
     * @param path
     *        the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(final String path) {
        return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_NAME, path);
    }

    /**
     * Retrieves the logging receiver.
     * 
     * @return The logging receiver.
     */
    public LoggingReceiver getReceiver() {
        if (receiver == null) {
            receiver = new LoggingReceiver();
        }

        return receiver;
    }

    /**
     * Retrieves the given file name from the plugin directory.
     * 
     * @param name
     *        Name of the file.
     * @return Full path to the given file.
     */
    public String getFile(final String name) {
        final URL url;
        try {
            url = new URL("platform:/plugin/org.eclipse.vtp.launching.jvoicexml/"
                    + name);
        } catch (java.net.MalformedURLException mue) {
            mue.printStackTrace();

            return null;
        }

        try {
            final URL localUrl = FileLocator.resolve(url);
            final File file = new File(localUrl.getFile());

            return file.getCanonicalPath();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();

            return null;
        }
    }

}
