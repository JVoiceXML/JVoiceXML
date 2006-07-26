/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java $
 * Version: $LastChangedRevision: 23 $
 * Date:    $LastChangedDate: $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.application;

import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.ApplicationRegistry;

/**
 * Implementation of the <code>ApplicationRegistry</code>.
 *
 * @see org.jvoicexml.ApplicationRegistry
 *
 * @author Dirk Schnelle
 * @version $LastChangedRevision: 23 $
 *
 * <p>
 * Copyright &copy; 2005 -2006JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlApplicationRegistry
        implements ApplicationRegistry {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlApplicationRegistry.class);

    /** The registry map. Each application is mapped to a unique name. */
    private final Map<String, Application> applications;

    /**
     * Creates a new object.
     *
     * <p>
     * This method should not be called by any application. Use
     * <code>JVoiceXml.getApplicationRegistry()</code> to obtain a reference
     * to the application registry.
     * </p>
     *
     * @see org.jvoicexml.JVoiceXmlCore#getApplicationRegistry()
     */
    public JVoiceXmlApplicationRegistry() {
        applications = new java.util.HashMap<String, Application>();
    }


    /**
     * {@inheritDoc}
     */
    public Application createApplication(final String applicationId,
                                         final URI rootUri) {
        return new JVoiceXmlApplication(applicationId, rootUri);
    }

    /**
     * {@inheritDoc}
     */
    public void register(final Application application) {
        if (application == null) {
            LOGGER.warn("cannot not add null to registry");

            return;
        }

        synchronized (applications) {
            final String id = application.getId();

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("adding '" + id + "' to registry");
            }

            applications.put(id, application);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Application getApplication(final String id) {
        if (id == null) {
            LOGGER.warn("cannot get application with id null");

            return null;
        }

        synchronized (applications) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("getting application with id '" + id + "'");
            }

            final Application application = applications.get(id);

            if (application == null) {
                LOGGER.warn("no application with id '" + id + "'");
            }

            return application;
        }
    }
}
