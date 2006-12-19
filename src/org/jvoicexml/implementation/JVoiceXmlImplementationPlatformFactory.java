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

import org.jvoicexml.CallControl;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.ImplementationPlatformFactory;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.SpokenInput;
import org.jvoicexml.SystemOutput;
import org.jvoicexml.UserInput;
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

    /** Pool of system output resource factories. */
    private final KeyedResourcePool<SystemOutput> outputPool;

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
        outputPool = new KeyedResourcePool<SystemOutput>();
        spokenInputPool = new KeyedResourcePool<SpokenInput>();
        callPool = new KeyedResourcePool<CallControl>();
    }

    /**
     * Adds the given list of factories for {@link SystemOutput}.
     * @param factories List with system putput factories.
     *
     * @since 0.5.5
     */
    public void setOutput(final List<ResourceFactory<SystemOutput>> factories) {
        for (ResourceFactory<SystemOutput> factory : factories) {
            final String type = factory.getType();
            if (defaultOutputType == null) {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("using '" + type + "' as default output");
                }

                defaultOutputType = type;
            }
            outputPool.addResourceFactory(factory);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("added system output factory " + factory.getClass()
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

        if (client == null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("no client given. using default platform");
            }
        }

        final SystemOutput output = getSystemOutput(client);
        final SpokenInput spokenInput = getSpokenInput(client);
        final CallControl call = getCallControl(client);

        output.connect(client);
        spokenInput.connect(client);
        call.connect(client);

        final UserInput input = new JVoiceXmlUserInput(spokenInput);

        return  new JVoiceXmlImplementationPlatform(this, call, output, input);
    }

    /**
     * Retrieves the <code>SpokenInput</code>, that is defined by the
     * <code>RemoteClient</code>.
     * @param client the remote client.
     * @return spoken input to use.
     * @throws NoresourceError
     *         error obtaing the resource from the pool.
     *
     * @since 0.5.5
     */
    private SpokenInput getSpokenInput(final RemoteClient client)
        throws NoresourceError {
        final SpokenInput spokenInput;
        try {
            final String inputKey;
            if (client == null) {
                inputKey = defaultSpokeninputType;
            } else {
                inputKey = client.getUserInput();
            }
            spokenInput = (SpokenInput) spokenInputPool.borrowObject(inputKey);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }
        return spokenInput;
    }

    /**
     * Retrieves the <code>SystemOutput</code>, that is defined by the
     * <code>RemoteClient</code>.
     * @param client the remote client.
     * @return system output to use.
     * @throws NoresourceError
     *         error obtaing the resource from the pool.
     *
     * @since 0.5.5
     */
    private SystemOutput getSystemOutput(final RemoteClient client)
        throws NoresourceError {
        final SystemOutput output;
        try {
            final String outputKey;
            if (client == null) {
                outputKey = defaultOutputType;
            } else {
                outputKey = client.getSystemOutput();
            }
            output = (SystemOutput) outputPool.borrowObject(outputKey);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }
        return output;
    }

    /**
     * Retrieves the <code>SystemOutput</code>, that is defined by the
     * <code>RemoteClient</code>.
     * @param client the remote client.
     * @return system output to use.
     * @throws NoresourceError
     *         error obtaing the resource from the pool.
     *
     * @since 0.5.5
     */
    private CallControl getCallControl(final RemoteClient client)
        throws NoresourceError {
        final CallControl call;

        try {
            final String callKey;
            if (client == null) {
                callKey = defaultCallControlType;
            } else {
                callKey = client.getCallControl();
            }
            call = (CallControl) callPool.borrowObject(callKey);
        } catch (Exception ex) {
            throw new NoresourceError(ex);
        }

        return call;
    }

    /**
     * Returns the resources that were used by the given implementation
     * platform.
     * @param platform the platform to return.
     */
    synchronized void returnImplementationPlatform(
            final ImplementationPlatform platform) {
        try {
            final SystemOutput output = platform.getSystemOutput();
            if (output != null) {
                final String type = output.getType();
                outputPool.returnObject(type, output);
            }
        } catch (NoresourceError e) {
            LOGGER.error(
                    "error obtaining the system output when returning to pool",
                    e);
        } catch (Exception e) {
            LOGGER.error("error returning system output to pool", e);
        }

        try {
            final JVoiceXmlUserInput input =
                (JVoiceXmlUserInput) platform.getUserInput();
            if (input != null) {
                SpokenInput spokeninput = input.getSpokenInput();
                final String type = spokeninput.getType();
                spokenInputPool.returnObject(type, spokeninput);
            }
        } catch (NoresourceError e) {
            LOGGER.error(
                    "error obtaining the spoken input when returning to pool",
                    e);
        } catch (Exception e) {
            LOGGER.error("error returning spoken input to pool", e);
        }

        try {
            final CallControl call = platform.getCallControl();
            if (call != null) {
                final String type = call.getType();
                callPool.returnObject(type, call);
            }
        } catch (NoresourceError e) {
            LOGGER.error(
                    "error obtaining the call control when returning to pool",
                    e);
        } catch (Exception e) {
            LOGGER.error("error returning call control to pool", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("closing implementation platforms...");
        }

        try {
            outputPool.close();
        } catch (Exception ex) {
            LOGGER.error("error closing output pool", ex);
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
