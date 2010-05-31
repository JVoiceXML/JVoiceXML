/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: $, Dirk Schnelle-Walka, project lead
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

import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.ErrorEvent;

/**
 * Asynchronous execution of a subdialog.
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.4
 */
final class SubdialogExecutorThread extends Thread {
    /** The URI of the subdialog. */
    private final URI uri;

    /** The context of the subdialog. */
    private final VoiceXmlInterpreterContext context;

    /** The current application. */
    private final Application application;

    /** The event handler to propagate errors and results. */
    private final EventHandler handler;

    /**
     * Constructs a new object.
     * @param subdialogUri the URI of the subdialog
     * @param subdialogContext the context of the subdialog
     * @param appl the current application
     * @param eventHandler the event handler to propagate errors and results
     */
    public SubdialogExecutorThread(final URI subdialogUri,
            final VoiceXmlInterpreterContext subdialogContext,
            final Application appl, final EventHandler eventHandler) {
        uri = subdialogUri;
        context = subdialogContext;
        application = appl;
        handler = eventHandler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            final DocumentDescriptor descriptor =
                new DocumentDescriptor(uri);
            context.processSubdialog(application, descriptor);
        } catch (ErrorEvent e) {
            handler.notifyEvent(e);
        }
    }
}
