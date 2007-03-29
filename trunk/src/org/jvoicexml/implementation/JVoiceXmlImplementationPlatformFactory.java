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

import org.jvoicexml.AudioFileOutput;
import org.jvoicexml.CallControl;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.SynthesizedOuput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * Basic implementation of an {@link ImplementationPlatformFactory}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class JVoiceXmlImplementationPlatformFactory
implements ImplementationPlatformFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(
                JVoiceXmlImplementationPlatformFactory.class);

    /** Pool of synthesizer output resource factories. */
    private final KeyedResourcePool<SynthesizedOuput> synthesizerPool;

    /** Pool of audio file output resource factories. */
    private final KeyedResourcePool<AudioFileOutput> fileOutputPool;

    /** Pool of user input resource factories. */
    private final KeyedResourcePool<SpokenInput> spokenInputPool;

    /** Pool of user calling resource factories. */
    private final KeyedResourcePool<CallControl> callPool;

    /** The default output type, if the remote client did not dpecify a type. */
    private String defaultOutputType;

    /** The default output type, if the remote client did not dpecify a type. */
    private String defaultSpokeninputType;

    /** The default output type, if the remote client did not dpecify a type. */
    private String defaultCallControlType;

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
    public JVoiceXmlImplementationPlatformFactory() {
        synthesizerPool = new KeyedResourcePool<SynthesizedOuput>();
        fileOutputPool = new KeyedResourcePool<AudioFileOutput>();
        spokenInputPool = new KeyedResourcePool<SpokenInput>();
        callPool = new KeyedResourcePool<CallControl>();
    }

    /**
     * Adds the given list of factories for {@link SystemOutput}.
     * @param factories List with system putput factories.
     *
     * @since 0.5.5
     */
    public void setSynthesizedoutput(
            final List<ResourceFactory<SynthesizedOuput>> factories) {
        for (ResourceFactory<SynthesizedOuput> factory : factories) {
            final String type = factory.getType();
            if (defaultOutputType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default output");
                }

                defaultOutputType = type;
            }

            synthesizerPool.addResourceFactory(factory);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added synthesized output factory "
                        + factory.getClass() + " for type '" + type + "'");
            }
        }
    }

    /**
     * Adds the given list of factories for {@link SystemOutput}.
     * @param factories List with system putput factories.
     *
     * @since 0.5.5
     */
    public void setFileoutput(
            final List<ResourceFactory<AudioFileOutput>> factories) {
        for (ResourceFactory<AudioFileOutput> factory : factories) {
            final String type = factory.getType();
            if (defaultOutputType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default output");
                }

                defaultOutputType = type;
            }

            fileOutputPool.addResourceFactory(factory);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added file output factory "
                        + factory.getClass() + " for type '" + type + "'");
            }
        }
    }

    /**
     * Adds the given list of factories for {@link SpokenInput}.
     * @param factories List with system putput factories.
     *
     * @since 0.5.5
     */
    public void setSpokeninput(
            final List<ResourceFactory<SpokenInput>> factories) {
        for (ResourceFactory<SpokenInput> factory : factories) {
            final String type = factory.getType();
            if (defaultSpokeninputType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default spoken input");
                }

                defaultSpokeninputType = type;
            }

            spokenInputPool.addResourceFactory(factory);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added user input factory " + factory.getClass()
                        + " for type '" + type + "'");
            }
        }
    }

    /**
     * Adds the given list of factories for {@link SpokenInput}.
     * @param factories List with system putput factories.
     *
     * @since 0.5.5
     */
    public void setCallcontrol(
            final List<ResourceFactory<CallControl>> factories) {
        for (ResourceFactory<CallControl> factory : factories) {
            final String type = factory.getType();
            if (defaultCallControlType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default call control");
                }

                defaultCallControlType = type;
            }

            callPool.addResourceFactory(factory);

            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added call control factory " + factory.getClass()
                        + " for type '" + type + "'");
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    public synchronized ImplementationPlatform getImplementationPlatform(
            final RemoteClient client)
    throws NoresourceError {

        final RemoteClient remoteClient;
        if (client == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("no client given. using default platform");
            }

            remoteClient = new DefaultRemoteClient(defaultCallControlType,
                    defaultOutputType, defaultSpokeninputType);
        } else {
            remoteClient = client;
        }

        return new JVoiceXmlImplementationPlatform(callPool, synthesizerPool,
                fileOutputPool, spokenInputPool, remoteClient);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing implementation platforms...");
        }

        /** @todo Wait until all objects are returned to the pool. */
        try {
            synthesizerPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing synthesizer output pool", ex);
        }

        try {
            fileOutputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing file output pool", ex);
        }

        try {
            spokenInputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error spoken input output pool", ex);
        }

        try {
            callPool.close();
        } catch (Exception ex) {
            LOGGER.error("error call control pool", ex);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("...implementation platforms closed");
        }
    }
}
