/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.callmanager.mmi;

import org.jvoicexml.Session;
import org.jvoicexml.interpreter.DetailedSessionListener;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.SessionEvent;

/**
 * A detailed session listener that sends out extension notifications.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.7
 */
public class MmiDetailedSessionListener implements DetailedSessionListener {
    /** The ETL protocol adapter to send MMI events. */
    private final ETLProtocolAdapter adapter;

    /** The MMI context */
    private final MMIContext context;

    /**
     * Constructs a new object.
     * 
     * @param protocolAdapter
     *            the adapter to send events
     */
    public MmiDetailedSessionListener(final ETLProtocolAdapter protocolAdapter,
            final MMIContext ctx) {
        adapter = protocolAdapter;
        context = ctx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionStarted(final Session session, final SessionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionOutput(final Session session, final SessionEvent event) {
        // TODO send extension notification
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionInput(final Session session, final SessionEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionEnded(final Session session, final SessionEvent event) {
        final DetailedSessionListener listener = this;
        final Thread thread = new Thread() {
            @Override
            public void run() {
                final JVoiceXmlSession jvxmlSession = (JVoiceXmlSession) session;
                jvxmlSession.removeSessionListener(listener);
            }
        };
        thread.start();
    }
}
