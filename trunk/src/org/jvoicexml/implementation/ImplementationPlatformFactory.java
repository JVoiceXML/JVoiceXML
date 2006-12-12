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

import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SystemOutput;
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
 * @see org.jvoicexml.implementation.JVoiceXmlImplementationPlatform
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

    /** Pool of system output resource factories. */
    private final KeyedResourcePool<SystemOutput> outputPool;

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
        outputPool = new KeyedResourcePool<SystemOutput>();
    }

    /**
     * Adds the given list of platforms.
     * @param factories List with platforms to add.
     *
     * @since 0.5.5
     */
    public void setOutput(final List<ResourceFactory<SystemOutput>> factories) {
        for (ResourceFactory<SystemOutput> factory : factories) {
            final String type = factory.getType();
            if (defaultType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default platform");
                }

                defaultType = type;
            }
            outputPool.addResourceFactory(factory);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added system output factory " + factory.getClass()
                            + " for type '" + type + "'");
            }
        }

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
     * @param client
     *        The remote client.
     * @return <code>ImplementationPlatform</code> to use.
     * @exception NoresourceError
     *            Error assigning the calling device to TTS or recognizer.
     */
    public synchronized ImplementationPlatform getImplementationPlatform(
            final RemoteClient client)
            throws NoresourceError {

        if (client == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("no client given. using default platform");
            }
        }

        final SystemOutput output;
        try {
            final String outputKey;
            if (client == null) {
                outputKey = defaultType;
            } else {
                outputKey = client.getSystemOutput();
            }
            output = (SystemOutput) outputPool.borrowObject(outputKey);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        final JVoiceXmlImplementationPlatform impl =
                new JVoiceXmlImplementationPlatform(this, null, output, null);

        return impl;
    }

    /**
     * Returns the resources that were used by the given implementation
     * platform.
     * @param platform the platform to return.
     */
    synchronized void returnImplementationPlatform(
            final ImplementationPlatform platform) {

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
