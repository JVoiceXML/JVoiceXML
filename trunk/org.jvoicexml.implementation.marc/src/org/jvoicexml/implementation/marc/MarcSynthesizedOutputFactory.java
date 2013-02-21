/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.marc;

import java.net.UnknownHostException;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link org.jvoicexml.implementation.SynthesizedOutput} for MARC.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 */
public final class MarcSynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Default port of MARC. */
    private static final int MARC_DEFAULT_PORT = 4010;

    /** Default port of MARC. */
    private static final int MARC_DEFAULT_FEEDBACK_PORT = 4011;

    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;

    /** MARC host. */
    private String host;

    /** MARC port number. */
    private int port;

    /** Port number for feedback from MARC. */
    private int feedbackPort;

    /** An external MARC publisher. */
    private ExternalMarcPublisher external;

    /** The voice to use for MARC. */
    private String voice;

    /** The default locale for text to be synthesized. */
    private String defaultLocale;

    /**
     * Constructs a new object.
     */
    public MarcSynthesizedOutputFactory() {
        type = "marc";
        port = MARC_DEFAULT_PORT;
        feedbackPort = MARC_DEFAULT_FEEDBACK_PORT;
    }

    /**
     * Sets the external publisher.
     * @param publisher
     *        the external publisher
     */
    public void setExternalMarcPublisher(
            final ExternalMarcPublisher publisher) {
        external = publisher;
    }

    /**
     * Sets the host name of MARC.
     *
     * @param value
     *            the host to set
     */
    public void setHost(final String value) {
        host = value;
    }

    /**
     * Sets the port number of MARC.
     *
     * @param portNumber
     *            the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Sets the feedback port number of MARC.
     *
     * @param portNumber
     *            the port to set
     */
    public void setFeedbackPort(final int portNumber) {
        feedbackPort = portNumber;
    }

    /**
     * Sets the name of the voice to use.
     * @param name name of the voice
     * @since 0.7.6
     */
    public void setVoice(final String name) {
        voice = name;
    }

    /**
     * Sets the default locale.
     * @param locale the default locale
     * @since 0.7.6
     */
    public void setDefaultLocale(final String locale) {
        defaultLocale = locale;
    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput createResource() throws NoresourceError {
        final MarcSynthesizedOutput output = new MarcSynthesizedOutput();
        output.setType(type);
        try {
            output.setHost(host);
        } catch (UnknownHostException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        output.setPort(port);
        output.setFeedbackPort(feedbackPort);
        output.setExternalMarcPublisher(external);
        output.setVoice(voice);
        output.setDefaultLocale(defaultLocale);
        return output;
    }

    /**
     * Sets the number of instances that this factory will create.
     *
     * @param number
     *                Number of instances to create.
     */
    public void setInstances(final int number) {
        instances = number;
    }

    /**
     * {@inheritDoc}
     */
    public int getInstances() {
        return instances;
    }


    /**
     * {@inheritDoc}
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the created resource.
     * @param typeName name of the resource
     */
    void setType(final String typeName) {
        type = typeName;
    }

    /**
     * {@inheritDoc}
     */
    public Class<SynthesizedOutput> getResourceType() {
        return SynthesizedOutput.class;
    }
}
