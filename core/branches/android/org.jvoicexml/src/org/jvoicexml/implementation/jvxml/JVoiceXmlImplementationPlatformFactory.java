/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/implementation/jvxml/JVoiceXmlImplementationPlatformFactory.java $
 * Version: $LastChangedRevision: 3009 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jvxml;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.ConnectionInformation;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ExternalRecognitionListener;
import org.jvoicexml.implementation.ExternalResource;
import org.jvoicexml.implementation.ExternalSynthesisListener;
import org.jvoicexml.implementation.PlatformFactory;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;
import org.jvoicexml.implementation.Telephony;
import org.jvoicexml.implementation.pool.KeyedResourcePool;

/**
 * Basic implementation of an {@link ImplementationPlatformFactory}.
 *
 * <p>
 * This implementation manages a pool of resource factories which are
 * delivered to each created {@link ImplementationPlatform}.
 * </p>
 *
 * <p>
 * In {@link #init(Configuration)} the resources are acquired as
 * {@link PlatformFactory}s and {@link ResourceFactory}s.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3009 $
 */
public final class JVoiceXmlImplementationPlatformFactory
    implements ImplementationPlatformFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlImplementationPlatformFactory.class);

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> spokenInputPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<Telephony> telephonyPool;

    /** An external recognition listener. */
    private ExternalRecognitionListener externalRecognitionListener;

    /** An external synthesis listener. */
    private ExternalSynthesisListener externalSynthesisListener;

    /** The JVoiceXML configuration. */
    private Configuration configuration;

    /**
     * Constructs a new object.
     *
     * <p>
     * This method should not be called by any application. This resource is
     * controlled by the <code>JvoiceXml</code> object.
     * </p>
     *
     * @see org.jvoicexml.JVoiceXml
     */
    private JVoiceXmlImplementationPlatformFactory() {
        synthesizerPool = new KeyedResourcePool<SynthesizedOutput>();
        spokenInputPool = new KeyedResourcePool<SpokenInput>();
        telephonyPool = new KeyedResourcePool<Telephony>();
    }

    /**
     * {@inheritDoc}
     * This implementation loads all {@link PlatformFactory}s and
     * {@link ResourceFactory}s. They can also be set manually by
     * {@link #addPlatform(PlatformFactory)},
     * {@link #addSpokenInputFactory(ResourceFactory)},
     * {@link #addSynthesizedOutputFactory(ResourceFactory)} and
     * {@link #addTelephonyFactory(ResourceFactory)}.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void init(final Configuration config)
        throws ConfigurationException {
        final Collection<PlatformFactory> factories =
            config.loadObjects(PlatformFactory.class, "implementation");
        for (PlatformFactory factory : factories) {
            try {
                addPlatform(factory);
            } catch (Exception e) {
                throw new ConfigurationException(e.getMessage(), e);
            }
        }
        final Collection<ResourceFactory> resourceFactories =
            config.loadObjects(ResourceFactory.class, "implementation");
        try {
            for (ResourceFactory resourceFactory : resourceFactories) {
                final Class<ExternalResource> clazz =
                    resourceFactory.getResourceType();
                if (clazz.equals(SpokenInput.class)) {
                    addSpokenInputFactory(resourceFactory);
                } else if (clazz.equals(SynthesizedOutput.class)) {
                    addSynthesizedOutputFactory(resourceFactory);
                } else if (clazz.equals(Telephony.class)) {
                    addTelephonyFactory(resourceFactory);
                }
            }
        } catch (Exception e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

        // Give a short summary of what is available
        reportPlatforms();

        // Keep a reference to the configuration
        configuration = config;
    }

    /**
     * Log a report of currently available platforms.
     * 
     * @since 0.7.4
     */
    private void reportPlatforms() {
        final Collection<String> synthesizers = synthesizerPool.getKeys();
        if (synthesizers.isEmpty()) {
            LOGGER.warn("no synthesizers available");
        } else {
            LOGGER.info("available synthesizers:");
            for (String key : synthesizers) {
                final int avail = synthesizerPool.getNumIdle(key);
                LOGGER.info("- " + avail + " instance(s) of type '" + key
                        + "'");
            }
        }
        final Collection<String> recognizers = spokenInputPool.getKeys();
        if (recognizers.isEmpty()) {
            LOGGER.warn("no recognizers available");
        } else {
            LOGGER.info("available recognizers:");
            for (String key : recognizers) {
                final int avail = spokenInputPool.getNumIdle(key);
                LOGGER.info("- " + avail + " instance(s) of type '" + key
                        + "'");
            }
        }
        final Collection<String> telephones = telephonyPool.getKeys();
        if (telephones.isEmpty()) {
            LOGGER.warn("no telephones available");
        } else {
            LOGGER.info("available telephones:");
            for (String key : telephones) {
                final int avail = telephonyPool.getNumIdle(key);
                LOGGER.info("- " + avail + " instance(s) of type '" + key
                        + "'");
            }
        }
    }

    /**
     * Sets an external recognition listener.
     * @param listener the external recognition listener.
     * @since 0.6
     */
    public void setExternalRecognitionListener(
            final ExternalRecognitionListener listener) {
        externalRecognitionListener = listener;
    }

    /**
     * Sets an external synthesis listener.
     * @param listener the external synthesis listener.
     * @since 0.6
     */
    public void setExternalSynthesisListener(
            final ExternalSynthesisListener listener) {
        externalSynthesisListener = listener;
    }


    /**
     * Adds the given platform factory to the list of known factories.
     * @param platform the platform factory to add.
     * @exception Exception
     *            error adding the platform
     * @since 0.7
     */
    public void addPlatform(final PlatformFactory platform) throws Exception {
        final ResourceFactory<SynthesizedOutput> synthesizedOutputFactory =
            platform.getSynthesizedoutput();
        if (synthesizedOutputFactory != null) {
            addSynthesizedOutputFactory(synthesizedOutputFactory);
        }
        final ResourceFactory<SpokenInput> spokenInputFactory =
            platform.getSpokeninput();
        if (spokenInputFactory != null) {
            addSpokenInputFactory(spokenInputFactory);
        }
        final ResourceFactory<Telephony> telephonyFactory =
            platform.getTelephony();
        if (telephonyFactory != null) {
            addTelephonyFactory(telephonyFactory);
        }
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link SynthesizedOutput}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @exception Exception
     *            error creating the pool
     * @since 0.6
     */
    private void addSynthesizedOutputFactory(
            final ResourceFactory<SynthesizedOutput> factory) throws Exception {
        final String type = factory.getType();
        synthesizerPool.addResourceFactory(factory);

        LOGGER.info("added synthesized output factory "
                + factory.getClass() + " for type '" + type + "'");
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link SpokenInput}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @exception Exception
     *            error adding the factory
     * @since 0.6
     */
    public void addSpokenInputFactory(
            final ResourceFactory<SpokenInput> factory) throws Exception {
        final String type = factory.getType();
        spokenInputPool.addResourceFactory(factory);

        LOGGER.info("added user input factory " + factory.getClass()
                + " for type '" + type + "'");
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link Telephony}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @exception Exception
     *            error adding the factory
     * @since 0.6
     */
    public void addTelephonyFactory(final ResourceFactory<Telephony> factory)
        throws Exception {
        final String type = factory.getType();
        telephonyPool.addResourceFactory(factory);
        LOGGER.info("added telephony factory " + factory.getClass()
                + " for type '" + type + "'");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized ImplementationPlatform getImplementationPlatform(
            final ConnectionInformation info)
        throws NoresourceError {
        if (info == null) {
            throw new NoresourceError("No connection information given!");
        }

        final JVoiceXmlImplementationPlatform platform =
            new JVoiceXmlImplementationPlatform(telephonyPool, synthesizerPool,
                spokenInputPool, info);
        platform.setExternalRecognitionListener(externalRecognitionListener);
        platform.setExternalSynthesisListener(externalSynthesisListener);
        try {
            platform.init(configuration);
        } catch (ConfigurationException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        return platform;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing implementation platforms...");
        }

        // Start a brute force termination in case the shutdown does not
        // terminate.
        final TerminationThread termination = new TerminationThread();
        termination.start();

        /** @todo Wait until all objects are returned to the pool. */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("synthesizer pool has "
                    + synthesizerPool.getNumActive() + " active/"
                    + synthesizerPool.getNumIdle() + " idle objects");
        }
        try {
            synthesizerPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing synthesizer output pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("spoken input pool has "
                    + spokenInputPool.getNumActive() + " active/"
                    + spokenInputPool.getNumIdle() + " idle objects");
        }
        try {
            spokenInputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing spoken input output pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("telephony pool has "
                    + telephonyPool.getNumActive() + " active/"
                    + telephonyPool.getNumIdle() + " idle objects");
        }
        try {
            telephonyPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing call control pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...implementation platforms closed");
        }
    }
}
