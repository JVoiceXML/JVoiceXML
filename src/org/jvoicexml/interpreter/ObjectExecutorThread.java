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

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

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

    /**
     * Constructs a new object.
     * @param ctx
     *                the current VoiceXML interpreter context.
     * @param item
     *                the object node to execute.
     * @param evt
     *                the event handler too propagate events.
     */
    ObjectExecutorThread(final VoiceXmlInterpreterContext ctx,
            final ObjectFormItem item, final EventHandler evt) {
        setDaemon(true);
        setName("ObjectExecutor");

        context = ctx;
        object = item;
        handler = evt;
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
     *                 <code>ObjectTag.ATTRIBUTE_CLASSID</code> not specified.
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

        final ScriptingEngine scripting = context.getScriptingEngine();
        final DocumentServer server = context.getDocumentServer();
        final ParamParser parser = new ParamParser(tag, scripting, server);
        final Map<String, Object> parameter = parser.getParameters();

        final Iterator<String> names = parameter.keySet().iterator();
        while (names.hasNext()) {
            final String name = names.next();
            final Object value = parameter.get(name);
            setInvocationTargetParameter(invocationTarget, name, value);
        }

        return targetExecute(invocationTarget);
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
        final Object invocationTarget;
        try {
            final Class<?> cls = Class.forName(className);
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
     * Set the given parameter to the specified value.
     *
     * @param invocationTarget
     *                The object to execute.
     * @param paramName
     *                name of the parameter
     * @param paramValue
     *                value of the parameter
     * @exception NoauthorizationError
     *                    Error accessing or executing a method.
     */
    @SuppressWarnings("unchecked")
    private void setInvocationTargetParameter(final Object invocationTarget,
            final String paramName, final Object paramValue)
            throws NoauthorizationError {
        if ((paramName == null) || (paramValue == null)) {
            return;
        }

        final String setterName = "set"
                + paramName.substring(0, 1).toUpperCase()
                + paramName.substring(1);
        final Class ivocationTargteClass = invocationTarget.getClass();

        try {
            final Method method = ivocationTargteClass.getMethod(setterName,
                    new Class[] {String.class});
            method.invoke(invocationTarget, new Object[] {paramValue});

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Set '" + paramName + "' to '" + paramValue + "'");
            }
        } catch (Throwable err) {
            /** @todo resolve all exceptions. */
            throw new NoauthorizationError("Can't set parmeter '" + paramName
                    + "'", err);
        }
    }

    /**
     * Executes the <code>invoke</code> method of the given object with the
     * given parameters.
     *
     * @param invocationTarget
     *                The object to call.
     * @return invocation result.
     * @exception NoauthorizationError
     *                    Error accessing or executing a method.
     */
    private Object targetExecute(final Object invocationTarget)
            throws NoauthorizationError {
        if (invocationTarget == null) {
            return null;
        }

        try {
            final Method method = invocationTarget.getClass().getMethod(
                    "invoke", new Class[] {});
            final Object result = method.invoke(invocationTarget,
                    new Object[] {});
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("result of call is '" + result + "'");
            }

            return result;
        } catch (Throwable err) {
            /** @todo resolve all exceptions. */
            throw new NoauthorizationError("Object tag invokation error", err);
        }
    }
}
