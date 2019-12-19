/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 UCM Technologies, Inc.
 *              - Released under the terms of LGPL License
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.Session;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoauthorizationError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedObjectnameError;
import org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.xml.vxml.ObjectTag;

/**
 * Execute an <code>&lt;object&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.ObjectTag
 *
 * @author Andrew Nick (ucmtech@sourceforge.net)
 * @author Dirk Schnelle-Walka
 */
final class ObjectExecutorThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(ObjectExecutorThread.class);

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The object form item to process. */
    private final ObjectFormItem object;

    /** The event bus to propagate events. */
    private final EventBus eventbus;

    /** The parameters to pass to the method. */
    private final Collection<Object> parameter;

    /** Reference to the current application. */
    private final Application application;

    /** The application base URL. */
    private final URL applicationBase;

    /** The class loader to use. */
    private static final ClassLoader LOADER;

    /** Loader cache. */
    private static final Map<Collection<URI>, ClassLoader> LOADERS;

    static {
        LOADER = ObjectExecutorThread.class.getClassLoader();
        LOADERS = new java.util.HashMap<Collection<URI>, ClassLoader>();
    }

    /**
     * Constructs a new object.
     * 
     * @param ctx
     *            the current VoiceXML interpreter context.
     * @param item
     *            the object node to execute.
     * @throws BadFetchError
     *             Nested param tag does not specify all attributes.
     * @throws SemanticError
     *             Not all attributes specified.
     */
    ObjectExecutorThread(final VoiceXmlInterpreterContext ctx,
            final ObjectFormItem item) throws SemanticError, BadFetchError {
        setDaemon(true);
        setName("ObjectExecutor");

        context = ctx;
        object = item;
        eventbus = context.getEventBus();

        // Determine the application base for the classpath
        application = context.getApplication();
        if (application == null) {
            applicationBase = null;
        } else {
            final URI baseUri = application.getXmlBase();
            if (baseUri == null) {
                applicationBase = null;
            } else {
                try {
                    applicationBase = baseUri.toURL();
                } catch (MalformedURLException e) {
                    throw new SemanticError(e.getMessage(), e);
                }
            }
        }
        // The parameter parsing has to be done here, since the thread
        // will not know about the original scripting context.
        final ObjectTag tag = (ObjectTag) object.getNode();
        final DataModel model = context.getDataModel();
        final DocumentServer server = context.getDocumentServer();
        final Session session = context.getSession();
        final ParamParser parser = new ParamParser(tag, model, server, session);
        parameter = parser.getParameterValues();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            final Object result = execute();
            final ObjectTagResultEvent event = new ObjectTagResultEvent(result);
            eventbus.publish(event);
        } catch (JVoiceXMLEvent e) {
            eventbus.publish(e);
        }
    }

    /**
     * Sets all parameters in the specified object and executes its
     * <code>invoke</code> method.
     *
     * @return invocation result.
     * @throws SemanticError
     *             Not all attributes specified.
     * @throws NoresourceError
     *             Error instantiating the object.
     * @exception NoauthorizationError
     *                Error accessing or executing a method.
     * @throws BadFetchError
     *             Nested param tag does not specify all attributes.
     * @throws UnsupportedObjectnameError
     *             scheme is not supported.
     */
    private Object execute() throws SemanticError, NoresourceError,
            NoauthorizationError, BadFetchError, UnsupportedObjectnameError {

        final ObjectTag tag = (ObjectTag) object.getNode();
        final Object invocationTarget = getInvocationTarget(tag);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("try to execute: '"
                    + invocationTarget.getClass().getName() + "'");
        }

        final String method;
        try {
            method = getMethodName(tag);
        } catch (URISyntaxException e) {
            throw new SemanticError("Must specify attribute a valid URI for: "
                    + ObjectTag.ATTRIBUTE_CLASSID);
        }

        return targetExecute(invocationTarget, method);
    }

    /**
     * Retrieves the object to call, identified by
     * <code>ObjectTag.ATTRIBUTE_CLASSID</code>.
     *
     * @param tag
     *            The object tag.
     * @return The object to call.
     * @throws SemanticError
     *             <code>ObjectTag.ATTRIBUTE_CLASSID</code> not specified.
     * @throws NoresourceError
     *             Error instantiating the object.
     * @throws UnsupportedObjectnameError
     *             scheme is not supported.
     */
    private Object getInvocationTarget(final ObjectTag tag)
            throws SemanticError, NoresourceError, UnsupportedObjectnameError {
        URI classid;
        try {
            classid = tag.getClassidUri();
        } catch (URISyntaxException e) {
            throw new SemanticError("Must specify attribute a valid URI for: "
                    + ObjectTag.ATTRIBUTE_CLASSID);
        }
        if (classid == null) {
            throw new SemanticError("Must specify attribute: "
                    + ObjectTag.ATTRIBUTE_CLASSID);
        }

        final String scheme = classid.getScheme();
        if (scheme == null) {
            throw new SemanticError("Must specify a scheme for the classid '"
                    + classid + "'");
        }
        if (!scheme.equals("method")) {
            throw new UnsupportedObjectnameError("scheme '" + scheme
                    + "' is not supported by this implementation.");
        }
        final String className = classid.getAuthority();
        Collection<URI> uris;
        try {
            uris = tag.getArchiveUris();
            if (uris == null) {
                uris = new java.util.ArrayList<URI>();
            }
        } catch (URISyntaxException e) {
            throw new SemanticError(
                    "Must specify a comma separated list of valid URIs for: "
                            + ObjectTag.ATTRIBUTE_ARCHIVE);
        }
        try {
            final URI data = tag.getDataUri();
            if (data != null) {
                uris.add(data);
            }
        } catch (URISyntaxException e) {
            throw new SemanticError("Must specify a valid URI for: "
                    + ObjectTag.ATTRIBUTE_DATA);
        }
        final ClassLoader loader = getClassLoader(uris);
        final Object invocationTarget;
        try {
            LOGGER.info("loading '" + className + "'");
            final Class<?> cls = loader.loadClass(className);
            invocationTarget = cls.newInstance();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Created instance of '" + cls.getName() + "'");
            }
        } catch (ClassNotFoundException e) {
            throw new NoresourceError("class not found '" + className + "'", e);
        } catch (IllegalAccessException e) {
            throw new NoresourceError("unable to access '" + className + "'",
                    e);
        } catch (InstantiationException e) {
            throw new NoresourceError("unable to instantiate '" + className
                    + "'", e);
        }

        return invocationTarget;
    }

    /**
     * Retrieves the class loader to use.
     * 
     * @param uris
     *            URIs to be added to the classpath.
     * @return class loader to use.
     * @throws SemanticError
     *             if the URI is not valid
     * @since 0.7.2
     */
    private synchronized ClassLoader getClassLoader(final Collection<URI> uris)
            throws SemanticError {
        if (uris == null || uris.isEmpty()) {
            if (applicationBase == null) {
                return LOADER;
            } else {
                LOGGER.info("adding '" + applicationBase + "' to CLASSPATH");
                final URL[] urls = new URL[1];
                urls[0] = applicationBase;
                return new URLClassLoader(urls, LOADER);
            }
        }
        ClassLoader loader = LOADERS.get(uris);
        if (loader != null) {
            return loader;
        }
        LOGGER.info("adding '" + uris + "' to CLASSPATH");
        final URL[] urls = new URL[uris.size()];
        int i = 0;
        for (URI uri : uris) {
            try {
                final URI resolved = application.resolve(uri);
                urls[i] = resolved.toURL();
                i++;
            } catch (MalformedURLException e) {
                throw new SemanticError("Must specify a valid URI for: "
                        + ObjectTag.ATTRIBUTE_DATA + " (" + uri + ")");
            } catch (BadFetchError e) {
                throw new SemanticError(e.getMessage(), e);
            }
        }
        loader = new URLClassLoader(urls, LOADER);
        LOADERS.put(uris, loader);
        return loader;
    }

    /**
     * Retrieves the method to execute.
     * 
     * @param tag
     *            the current tag.
     * @return the method name to execute, <code>invoke</code> if no method name
     *         is specified.
     * @throws URISyntaxException
     *             classid does not denote a valid URI.
     */
    private String getMethodName(final ObjectTag tag)
            throws URISyntaxException {
        final URI classid = tag.getClassidUri();
        final String fragment = classid.getFragment();
        if (fragment == null) {
            return "invoke";
        }

        return fragment;
    }

    /**
     * Executes the <code>invoke</code> method of the given object with the
     * given parameters.
     *
     * @param invocationTarget
     *            The object to call.
     * @param methodName
     *            name of the method to call.
     * @return invocation result.
     * @exception NoauthorizationError
     *                Error accessing or executing a method.
     */
    private Object targetExecute(final Object invocationTarget,
            final String methodName) throws NoauthorizationError {
        if (invocationTarget == null) {
            return null;
        }

        // Create the signature and arguments for the method.
        final Class<?>[] sig = new Class<?>[parameter.size()];
        final Object[] args = new Object[parameter.size()];
        int i = 0;
        for (Object value : parameter) {
            sig[i] = value.getClass();
            args[i] = value;
            ++i;
        }

        final Class<?> clazz = invocationTarget.getClass();
        if (LOGGER.isDebugEnabled()) {
            final StringBuilder str = new StringBuilder();
            str.append(clazz.getName());
            str.append('.');
            str.append(methodName);
            str.append('(');
            Iterator<Object> iterator = parameter.iterator();
            while (iterator.hasNext()) {
                final Object value = iterator.next();
                if (value instanceof String) {
                    str.append('\'');
                }
                str.append(value);
                if (value instanceof String) {
                    str.append('\'');
                }
                if (iterator.hasNext()) {
                    str.append(", ");
                }
            }
            str.append(")");
            LOGGER.debug("calling " + str);
        }

        // Call the method.
        try {
            final Method method = clazz.getMethod(methodName, sig);
            final Object result = method.invoke(invocationTarget, args);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result of call is '" + result + "'");
            }
            // Return at least something if the method is of return type void
            if (result == null) {
                return new Object();
            }
            return result;
        } catch (SecurityException e) {
            throw new NoauthorizationError("Object tag invocation error "
                    + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new NoauthorizationError("Object tag invocation error "
                    + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new NoauthorizationError("Object tag invocation error "
                    + e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new NoauthorizationError("Object tag invocation error "
                    + e.getMessage(), e);
        }
    }
}
