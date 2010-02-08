/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.text;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.CallManager;
import org.jvoicexml.JVoiceXml;
import org.jvoicexml.event.error.NoresourceError;

/**
 * A {@link org.jvoicexml.CallManager} for text based clients.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TextCallManager implements CallManager {
    /** Logger instance. */
    private static final Logger LOGGER =
        Logger.getLogger(TextCallManager.class);

    /** Know applications. */
    private final Collection<TextApplication> applications;

    /** Reference to JVoiceXML. */
    private JVoiceXml jvxml;

    /**
     * Constructs a new object.
     */
    public TextCallManager() {
        applications = new java.util.ArrayList<TextApplication>();
    }

    /**
     * Adds the given list of applications.
     *
     * @param apps
     *            list of application
     */
    public void setApplications(
            final Collection<TextApplication> apps) {
        for (TextApplication application : apps) {
            applications.add(application);
            LOGGER.info("added application '" + application.getUri() + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJVoiceXml(final JVoiceXml jvoicexml) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() throws NoresourceError, IOException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
    }
}
