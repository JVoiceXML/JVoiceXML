/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.lightweightbml;

import java.net.UnknownHostException;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.ResourceFactory;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * Demo implementation of a {@link org.jvoicexml.implementation.ResourceFactory}
 * for the {@link org.jvoicexml.implementation.SynthesizedOutput} for LightweightBML and TalkingHead.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.7
 */
public final class BMLSynthesizedOutputFactory
        implements ResourceFactory<SynthesizedOutput> {
    /** Default port of Avatar. */
    private static final int AVATAR_DEFAULT_PORT = 4010;

    /** Default port of Avatar. */
    private static final int AVATAR_DEFAULT_FEEDBACK_PORT = 4011;

    /** Number of instances that this factory will create. */
    private int instances;

    /** Type of the created resources. */
    private String type;

    /** Avatar host. */
    private String host;

    /** Avatar port number. */
    private int port;

    /** Port number for feedback from Avatar. */
    private int feedbackPort;

    /** An external BML publisher. */
    private ExternalBMLPublisher external;

//    /** The voice to use for Avatar. */
//    private String voice;
//
//    /** The default locale for text to be synthesized. */
//    private String defaultLocale;

    /**
     * Constructs a new object.
     */
    public BMLSynthesizedOutputFactory() {
        type = "bml";
        port = AVATAR_DEFAULT_PORT;
        feedbackPort = AVATAR_DEFAULT_FEEDBACK_PORT;
    }

    /**
     * Sets the external publisher.
     * @param publisher
     *        the external publisher
     */
    public void setExternalBMLPublisher(
            final ExternalBMLPublisher publisher) {
        external = publisher;
    }

    /**
     * Sets the host name of Avatar.
     *
     * @param value
     *            the host to set
     */
    public void setHost(final String value) {
        host = value;
    }

    /**
     * Sets the port number of Avatar.
     *
     * @param portNumber
     *            the port to set
     */
    public void setPort(final int portNumber) {
        port = portNumber;
    }

    /**
     * Sets the feedback port number of Avatar.
     *
     * @param portNumber
     *            the port to set
     */
    public void setFeedbackPort(final int portNumber) {
        feedbackPort = portNumber;
    }

//    /**
//     * Sets the name of the voice to use.
//     * @param name name of the voice
//     * @since 0.7.6
//     */
//    public void setVoice(final String name) {
//        voice = name;
//    }
//
//    /**
//     * Sets the default locale.
//     * @param locale the default locale
//     * @since 0.7.6
//     */
//    public void setDefaultLocale(final String locale) {
//        defaultLocale = locale;
//    }

    /**
     * {@inheritDoc}
     */
    public SynthesizedOutput createResource() throws NoresourceError {
        final BMLSynthesizedOutput output = new BMLSynthesizedOutput();
        output.setType(type);
        try {
            output.setHost(host);
        } catch (UnknownHostException e) {
            throw new NoresourceError(e.getMessage(), e);
        }
        output.setPort(port);
        output.setFeedbackPort(feedbackPort);
        output.setExternalBMLPublisher(external);
//        output.setVoice(voice);
//        output.setDefaultLocale(defaultLocale);
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
