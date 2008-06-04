/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 UCM Technologies, Inc.
 *              - Released under the terms of LGPL License
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.log4j.Logger;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoauthorizationError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedObjectnameError;
import org.jvoicexml.event.plain.jvxml.ObjectTagResultEvent;
import org.jvoicexml.interpreter.formitem.ObjectFormItem;
import org.jvoicexml.xml.vxml.ObjectTag;

/**
 * Execute an <code>&lt;object&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.ObjectTag
 *
 * @author Andrew Nick (ucmtech@sourceforge.net)
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 UCM Technologies, Inc. Released under the terms of
 * LGPL license
 * </p>
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class ObjectExecutorThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ObjectExecutorThread.class);

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /** The object form item to process. */
    private final ObjectFormItem object;

    /** The event handler to propagate events. */
    private final EventHandler handler;

    /** The parameters to pass to the method. */
    private final Collection<Object> parameter;

    /**
     * Constructs a new object.
     * @param ctx
     *                the current VoiceXML interpreter context.
     * @param item
     *                the object node to execute.
     * @param evt
     *                the event handler too propagate events.
     * @throws BadFetchError
     *                 Nested param tag does not specify all attributes.
     * @throws SemanticError
     *                 Not all attribues specified.
     */
    ObjectExecutorThread(final VoiceXmlInterpreterContext ctx,
            final ObjectFormItem item, final EventHandler evt)
            throws SemanticError, BadFetchError {
        setDaemon(true);
        setName("ObjectExecutor");

        context = ctx;
        object = item;
        handler = evt;

        // The parameter parsing has to be done here, since the thread
        // will not know about the original scripting context.
        final ObjectTag tag = (ObjectTag) object.getNode();
        final ScriptingEngine scripting = context.getScriptingEngine();
        final DocumentServer server = context.getDocumentServer();
        final ParamParser parser = new ParamParser(tag, scripting, server);
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
            handler.notifyEvent(event);
        } catch (JVoiceXMLEvent e) {
            handler.notifyEvent(e);
        }
    }

    /**
     * Sets all parameters in the specified object and executes its
     * <code>invoke</code> method.
     *
     * @return invocation result.
     * @throws SemanticError
     *                 Not all attribues specified.
     * @throws NoresourceError
     *                 Error instantiating the object.
     * @exception NoauthorizationError
     *                    Error accessing or executing a method.
     * @throws BadFetchError
     *                 Nested param tag does not specify all attributes.
     * @throws UnsupportedObjectnameError
     *         scheme is not supported.
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
     *                The object tag.
     * @return The object to call.
     * @throws SemanticError
     *                 <code>ObjectTag.ATTRIBUTE_CLASSID</code> not specified.
     * @throws NoresourceError
     *                 Error instantiating the object.
     * @throws UnsupportedObjectnameError
     *         scheme is not supported.
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
        URI data;
        try {
            data = tag.getDataUri();
        } catch (URISyntaxException e) {
            throw new SemanticError("Must specify attribute a valid URI for: "
                    + ObjectTag.ATTRIBUTE_DATA);
        }
        final ClassLoader loader;
        if (data == null) {
            loader = ClassLoader.getSystemClassLoader();
        } else {
            try {
                final URL[] urls = new URL[] {data.toURL()};
                loader = new URLClassLoader(urls);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("adding '" + data + "' to CLASSPATH");
                }
            } catch (MalformedURLException e) {
                throw new SemanticError(
                        "Must specify attribute a valid URI for: "
                        + ObjectTag.ATTRIBUTE_DATA);
            }
        }
        final Object invocationTarget;
        try {
            final Class<?> cls = loader.loadClass(className);
            invocationTarget = cls.newInstance();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Created instance of '" + cls.getName() + "'");
            }
        } catch (ClassNotFoundException cnfe) {
            throw new NoresourceError("class not found '" + classid + "'",
                    cnfe);
        } catch (IllegalAccessException iae) {
            throw new NoresourceError("unable to access '" + classid + "'",
                    iae);
        } catch (InstantiationException ie) {
            throw new NoresourceError(
                    "unable to instantiate '" + classid + "'", ie);
        }

        return invocationTarget;
    }

    /**
     * Retrieves the method to execute.
     * @param tag the current tag.
     * @return the method name to execute, <code>invoke</code> if no
     *         method name is specified.
     * @throws URISyntaxException
     *         classid does not denote a valid URI.
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
     *                The object to call.
     * @param methodName name of the method to call.
     * @return invocation result.
     * @exception NoauthorizationError
     *                    Error accessing or executing a method.
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
                str.append(value);
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

            return result;
        } catch (SecurityException e) {
            throw new NoauthorizationError("Object tag invokation error", e);
        } catch (NoSuchMethodException e) {
            throw new NoauthorizationError("Object tag invokation error", e);
        } catch (IllegalAccessException e) {
            throw new NoauthorizationError("Object tag invokation error", e);
        } catch (InvocationTargetException e) {
            throw new NoauthorizationError("Object tag invokation error", e);
        }
    }
}
