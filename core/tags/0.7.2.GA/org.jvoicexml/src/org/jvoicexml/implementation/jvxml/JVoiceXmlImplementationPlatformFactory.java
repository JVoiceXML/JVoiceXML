/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.client.BasicRemoteClient;
import org.jvoicexml.config.JVoiceXmlConfiguration;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.AudioFileOutput;
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
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlImplementationPlatformFactory
    implements ImplementationPlatformFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(JVoiceXmlImplementationPlatformFactory.class);

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOutput> synthesizerPool;

    /** Pool of audio file output resource factories. */
    private final KeyedResourcePool<AudioFileOutput> fileOutputPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> spokenInputPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<Telephony> telephonyPool;

    /** The default output type, if the remote client did not specify a type. */
    private String defaultOutputType;

    /** The default output type, if the remote client did not specify a type. */
    private String defaultSpokeninputType;

    /**
     * The default telephony type, if the remote client did not specify a type.
     */
    private String defaultTelephonyType;

    /** An external recognition listener. */
    private ExternalRecognitionListener externalRecognitionListener;

    /** An external synthesis listener. */
    private ExternalSynthesisListener externalSynthesisListener;

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
        fileOutputPool = new KeyedResourcePool<AudioFileOutput>();
        spokenInputPool = new KeyedResourcePool<SpokenInput>();
        telephonyPool = new KeyedResourcePool<Telephony>();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void init(final JVoiceXmlConfiguration configuration) {
        final Collection<PlatformFactory> factories =
            configuration.loadObjects(PlatformFactory.class, "implementation");
        for (PlatformFactory factory : factories) {
            addPlatform(factory);
        }
        final Collection<ResourceFactory> resourceFactories =
            configuration.loadObjects(ResourceFactory.class, "implementation");
        for (ResourceFactory resourceFactory : resourceFactories) {
            final Class<ExternalResource> clazz =
                resourceFactory.getResourceType();
            if (clazz.equals(SpokenInput.class)) {
                addSpokenInputFactory(resourceFactory);
            } else if (clazz.equals(AudioFileOutput.class)) {
                addFileOutputFactory(resourceFactory);
            } else if (clazz.equals(SynthesizedOutput.class)) {
                addSynthesizedOutputFactory(resourceFactory);
            } else if (clazz.equals(Telephony.class)) {
                addTelephonyFactory(resourceFactory);
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
     * @since 0.7
     */
    private void addPlatform(final PlatformFactory platform) {
        final ResourceFactory<SynthesizedOutput> synthesizedOutputFactory =
            platform.getSynthesizedoutput();
        if (synthesizedOutputFactory != null) {
            addSynthesizedOutputFactory(synthesizedOutputFactory);
        }
        final ResourceFactory<AudioFileOutput> fileOutputFactory =
            platform.getAudiofileoutput();
        if (fileOutputFactory != null) {
            addFileOutputFactory(fileOutputFactory);
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
     * @since 0.6
     */
    private void addSynthesizedOutputFactory(
            final ResourceFactory<SynthesizedOutput> factory) {
        final String type = factory.getType();
        if (defaultOutputType == null) {
            LOGGER.info("using '" + type + "' as default output");

            defaultOutputType = type;
        }

        synthesizerPool.addResourceFactory(factory);

        LOGGER.info("added synthesized output factory "
                + factory.getClass() + " for type '" + type + "'");
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link AudioFileOutput}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @since 0.6
     */
    private void addFileOutputFactory(
            final ResourceFactory<AudioFileOutput> factory) {
        final String type = factory.getType();
        if (defaultOutputType == null) {
            LOGGER.info("using '" + type + "' as default output");

            defaultOutputType = type;
        }

        fileOutputPool.addResourceFactory(factory);

        LOGGER.info("added file output factory "
                + factory.getClass() + " for type '" + type + "'");
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link SpokenInput}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @since 0.6
     */
    private void addSpokenInputFactory(
            final ResourceFactory<SpokenInput> factory) {
        final String type = factory.getType();
        if (defaultSpokeninputType == null) {
            LOGGER.info("using '" + type + "' as default spoken input");

            defaultSpokeninputType = type;
        }

        spokenInputPool.addResourceFactory(factory);

        LOGGER.info("added user input factory " + factory.getClass()
                + " for type '" + type + "'");
    }

    /**
     * Adds the given {@link ResourceFactory} for {@link Telephony}
     * to the list of know factories.
     * @param factory
     *        the factory to add.
     * @since 0.6
     */
    private void addTelephonyFactory(final ResourceFactory<Telephony> factory) {
        final String type = factory.getType();
        if (defaultTelephonyType == null) {
            LOGGER.info("using '" + type
                    + "' as default telephony support");

            defaultTelephonyType = type;
        }

        telephonyPool.addResourceFactory(factory);

        LOGGER.info("added telephony factory " + factory.getClass()
                + " for type '" + type + "'");
    }

    /**
     * {@inheritDoc}
     */
    public synchronized ImplementationPlatform getImplementationPlatform(
            final RemoteClient client)
    throws NoresourceError {

        final RemoteClient remoteClient;
        if (client == null) {
            LOGGER.info("no client given. using default platform");

            remoteClient = new BasicRemoteClient(defaultTelephonyType,
                    defaultOutputType, defaultSpokeninputType);
        } else {
            remoteClient = client;
        }

        final JVoiceXmlImplementationPlatform platform =
            new JVoiceXmlImplementationPlatform(telephonyPool, synthesizerPool,
                fileOutputPool, spokenInputPool, remoteClient);
        platform.setExternalRecognitionListener(externalRecognitionListener);
        platform.setExternalSynthesisListener(externalSynthesisListener);
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
                    + synthesizerPool.getMinIdle() + " idle objects");
        }
        try {
            synthesizerPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing synthesizer output pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("file output pool has "
                    + fileOutputPool.getNumActive() + " active/"
                    + fileOutputPool.getMinIdle() + " idle objects");
        }
        try {
            fileOutputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing file output pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("spoken input pool has "
                    + spokenInputPool.getNumActive() + " active/"
                    + spokenInputPool.getMinIdle() + " idle objects");
        }
        try {
            spokenInputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing spoken input output pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("telephony pool has "
                    + telephonyPool.getNumActive() + " active/"
                    + telephonyPool.getMinIdle() + " idle objects");
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
