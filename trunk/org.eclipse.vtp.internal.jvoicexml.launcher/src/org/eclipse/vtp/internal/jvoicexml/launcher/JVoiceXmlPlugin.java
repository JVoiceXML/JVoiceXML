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

package org.eclipse.vtp.internal.jvoicexml.launcher;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.Platform;
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
 * @see org.eclipse.vtp.internal.jvoicexml.launcher.JVoiceXmlBrowser
 * @see org.eclipse.vtp.internal.jvoicexml.launcher.JVoiceXmlBrowserUI
 * @see org.eclipse.vtp.internal.jvoicexml.launcher.LoggingReceiver
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
            url = new URL("platform:/plugin/org.eclipse.vtp.internal.jvoicexml.launcher/"
                    + name);
        } catch (java.net.MalformedURLException mue) {
            mue.printStackTrace();

            return null;
        }

        try {
            final URL localUrl = Platform.asLocalURL(url);
            final File file = new File(localUrl.getFile());

            return file.getCanonicalPath();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();

            return null;
        }
    }

    private URLClassLoader setJVoiceXmlClassLoader(final String jvxmlHome,
            final ClassLoader eclipse) {
        URLClassLoader loader = null;

        try {
            URL[] urls = {
                    new URL("file:///" + jvxmlHome + "/lib/jvxml-client.jar"),
                    new URL("file:///" + jvxmlHome + "/lib/log4j1.2.13.jar") };

            loader = new URLClassLoader(urls, eclipse);

            Thread.currentThread().setContextClassLoader(loader);
        } catch (MalformedURLException murle) {
            murle.printStackTrace();
        }

        return loader;
    }

    private void restoreClassLoader(final ClassLoader eclipse) {
        Thread.currentThread().setContextClassLoader(eclipse);
    }
}
