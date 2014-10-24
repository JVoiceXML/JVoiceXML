/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.plain.jvxml.ReturnEvent;
import org.jvoicexml.event.plain.jvxml.SubdialogResultEvent;

/**
 * Asynchronous execution of a subdialog.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
final class SubdialogExecutorThread extends Thread {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(SubdialogExecutorThread.class);

    /** The URI of the subdialog. */
    private final URI uri;

    /** The context of the subdialog. */
    private final VoiceXmlInterpreterContext context;

    /** The current application. */
    private final Application application;

    /** The event bus to propagate errors and results. */
    private final EventBus eventbus;

    /** Parameters of the subdialog call. */
    private final Map<String, Object> parameters;

    /**
     * Constructs a new object.
     * 
     * @param subdialogUri
     *            the URI of the subdialog
     * @param subdialogContext
     *            the context of the subdialog
     * @param appl
     *            the current application
     * @param params
     *            parameters of the subdialog call
     * @param bus
     *            the event bus of the calling context to correctly propagate
     *            messages
     */
    public SubdialogExecutorThread(final URI subdialogUri,
            final VoiceXmlInterpreterContext subdialogContext,
            final Application appl, final Map<String, Object> params,
            final EventBus bus) {
        uri = subdialogUri;
        context = subdialogContext;
        application = appl;
        eventbus = bus;
        parameters = params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        final ScriptingEngine scripting = context.getScriptingEngine();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing parameters...");
        }
        for (String name : parameters.keySet()) {
            final Object value = parameters.get(name);
            scripting.setVariable(name, value);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...initialized parameters");
        }
        try {
            final DocumentDescriptor descriptor = new DocumentDescriptor(uri);
            context.processSubdialog(application, descriptor);
        } catch (ReturnEvent e) {
            final Object result;
            try {
                result = getReturnObject(e);
            } catch (SemanticError sematicerror) {
                eventbus.publish(sematicerror);
                return;
            }
            final SubdialogResultEvent event = new SubdialogResultEvent(result);
            eventbus.publish(event);
            return;
        } catch (JVoiceXMLEvent e) {
            eventbus.publish(e);
            return;
        }
        // The VoiceXML spec leaves it open what should happen if there was no
        // return or exit and the dialog terminated because all forms were
        // processed. So we return TRUE in this case.
        final SubdialogResultEvent event = new SubdialogResultEvent(
                Boolean.TRUE);
        eventbus.publish(event);
    }

    /**
     * Creates the value for the returned result.
     * 
     * @param event
     *            caught event.
     * @return return result.
     * @throws SemanticError
     *             if a variable could not be evaluated
     */
    private Object getReturnObject(final ReturnEvent event)
            throws SemanticError {
        final StringBuilder str = new StringBuilder();
        str.append("var out = new Object();");
        final Map<String, Object> variables = event.getVariables();
        for (String name : variables.keySet()) {
            str.append("out.");
            str.append(name);
            str.append(" = ");
            final Object value = variables.get(name);
            if (value instanceof String) {
                str.append("\"");
                str.append(value);
                str.append("\"");
            } else {
                str.append(value);
            }
            str.append(";");
        }
        final ScriptingEngine scripting = context.getScriptingEngine();
        final String expr = str.toString();
        scripting.eval(expr);
        return scripting.getVariable("out");
    }
}
