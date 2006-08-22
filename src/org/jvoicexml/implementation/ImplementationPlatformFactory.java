/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
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

package org.jvoicexml.implementation;

import java.util.List;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Factory, which manages a pool of implementation platforms that can be used by
 * an application.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @see org.jvoicexml.implementation.ImplementationPlatform
 *
 * @todo Implement the pool.
 */
public final class ImplementationPlatformFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ImplementationPlatformFactory.class);

    /** Configuration key. */
    public static final String CONFIG_KEY = "implementationplatform";

    /** A mapping of platform types to <code>ImplementationPlatform</code>s. */
    private final KeyedPlatformPool platforms;

    /** The default type, if no call control is given. */
    private String defaultType;

    /**
     * Constructs a new object.
     *
     * <p>
     * This method should not be called by any application. This resouces is
     * controlled by the <code>JvoiceXml</code> object.
     * </p>
     *
     * @see org.jvoicexml.JVoiceXml
     */
    public ImplementationPlatformFactory() {
        platforms = new KeyedPlatformPool();
    }

    /**
     * Adds the given list of platforms.
     * @param factories List with platforms to add.
     *
     * @since 0.5
     */
    public void setPlatforms(final List<PlatformFactory> factories) {
        for (PlatformFactory factory : factories) {
            final String type = factory.getType();
            if (defaultType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default platform");
                }

                defaultType = type;
            }
            platforms.addPlatformFactory(factory);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added platform factory" + factory.getClass()
                            + " for type '" + type + "'");
            }
        }

    }

    /**
     * Factory method to retrieve an implementation platform for the given
     * calling device.
     *
     * @param call
     *        The calling device.
     * @return <code>ImplementationPlatform</code> to use.
     * @exception NoresourceError
     *            Error assigning the calling device to TTS or recognizer.
     */
    public synchronized ImplementationPlatform getImplementationPlatform(
            final CallControl call)
            throws NoresourceError {

        final String type;
        if (call == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("no type given. using default platform");
            }

            type = defaultType;
        } else {
            final String callType = call.getPlatformType();
            if (callType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("platform does not provide a type. "
                                + "using default platform");
                }

                type = defaultType;
            } else {
                type = callType;
            }
        }

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("retrieving platform of type '" + type + "'...");
        }

        final Platform platform;

        try {
            platform = (Platform) platforms.borrowObject(type);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        final ImplementationPlatform impl =
                new ImplementationPlatform(platforms);
        impl.setPlatform(platform);
        impl.setCallControl(call);

        return impl;
    }

    /**
     * Closes all implementation platforms.
     *
     * @since 0.4
     */
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing implementation platforms...");
        }

        try {
            platforms.close();
        } catch (Exception ex) {
            LOGGER.error("error closing platforms", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...implementation platforms closed");
        }
    }

}
