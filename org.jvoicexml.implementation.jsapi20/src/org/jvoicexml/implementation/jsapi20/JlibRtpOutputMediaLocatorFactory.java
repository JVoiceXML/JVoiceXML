/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.implementation.jsapi20;

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * A media locator factory for the <a href="http://www.jlibrtp.org">jlibrtp</a>
 * RTP implementation.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class JlibRtpOutputMediaLocatorFactory
        implements OutputMediaLocatorFactory {
    /** Host of the audio stream. */
    private String host = "localhost";

    /** Port number. */
    private long port = 30000;

    /** Packets per second. */
    private long pps;

    /** <code>true</code> if a keep alive message should be sent. */
    private boolean keepAlive;

    /** Name of the participant. */
    private String participant = "localhost";

    /** <code>true</code> if signed audio data should be used. */
    private boolean signed;

    /** Encoding to use. */
    private String encoding;

    /** Bit rate of the audio signal. */
    private long rate;

    /** Number of bits per second. */
    private long bits;

    /** Number of channels. */
    private long channels;

    /** Endian format. */
    private String endian;

    /** Nmber of instances created so far. */
    private long count;

    /**
     * Sets the host of the audio stream.
     * @param name name o the host
     */
    public void setHost(final String name) {
        host = name;
    }

    /**
     * Sets the port number.
     * @param value port number.
     */
    public void setPort(final long value) {
        port = value;
    }

    /**
     * Sets the number of packets per second.
     * @param packets number of packets per second.
     */
    public void setPacketsPerSecond(final long packets) {
        pps = packets;
    }

    /**
     * Sets the keep alive status.
     * @param on <code>true</code> if a keep alive signal should be issued.
     */
    public void setKeepAlive(final boolean on) {
        keepAlive = on;
    }

    /**
     * Sets the name of the participant.
     * @param name name of the participant
     */
    public void setParticipant(final String name) {
        participant = name;
    }

    /**
     * Sets if signed data should be used.
     * @param on <code>true</code> if signed data should be used.
     */
    public void setSigned(final boolean on) {
        signed = on;
    }

    /**
     * Sets the encoding.
     * @param enc encoding to use.
     */
    public void setEncoding(final String enc) {
        encoding = enc;
    }

    /**
     * Sets the data rate.
     * @param value rate.
     */
    public void setRate(final long value) {
        rate = value;
    }

    /**
     * Sets the number of bits.
     * @param value number of bits
     */
    public void setBits(final long value) {
        bits = value;
    }

    /**
     * Sets the number of audio channels.
     * @param number number of audio channels.
     */
    public void setChannels(final long number) {
        channels = number;
    }

    /**
     * Sets the endianess.
     * @param value endianess.
     */
    public void setEndian(final String value) {
        endian = value;
    }

    /**
     * {@inheritDoc}
     */
    public URI getSourceMediaLocator(final SynthesizedOutput output)
        throws URISyntaxException {
        final StringBuilder str = new StringBuilder();
        str.append("rtp://");
        str.append(host);
        str.append(':');
        str.append(port + count * 2);
        ++count;
        str.append("/audio?participant=");
        str.append(participant);
        maybeAppendParameter(str, "bits", bits);
        maybeAppendParameter(str, "channels", channels);
        maybeAppendParameter(str, "encoding", encoding);
        maybeAppendParameter(str, "endian", endian);
        appendParameter(str, "keepAlive", keepAlive);
        maybeAppendParameter(str, "pps", pps);
        maybeAppendParameter(str, "rate", rate);
        appendParameter(str, "signed", signed);
        final String locator = str.toString();
        return new URI(locator);
    }


    /**
     * Adds the given parameter to string.
     * @param str string to append to
     * @param name name of the parameter
     * @param value value of the parameter
     */
    private void appendParameter(final StringBuilder str,
            final String name, final boolean value) {
        str.append('&');
        str.append(name);
        str.append("=");
        str.append(value);
    }

    /**
     * Adds the given parameter to string if its value is not 0.
     * @param str string to append to
     * @param name name of the parameter
     * @param value value of the parameter
     */
    private void maybeAppendParameter(final StringBuilder str,
            final String name, final long value) {
        if (value <= 0) {
            return;
        }
        str.append('&');
        str.append(name);
        str.append("=");
        str.append(value);
    }

    /**
     * Adds the given parameter to string if its value is not <code>null</code>.
     * @param str string to append to
     * @param name name of the parameter
     * @param value value of the parameter
     */
    private void maybeAppendParameter(final StringBuilder str,
            final String name, final String value) {
        if (value == null) {
            return;
        }
        str.append('&');
        str.append(name);
        str.append("=");
        str.append(value);
    }

    /**
     * {@inheritDoc}
     */
    public URI getSinkMediaLocator(final SynthesizedOutput output,
            final URI sourceLocator) throws URISyntaxException {
        if (sourceLocator.getQuery() == null) {
            return sourceLocator;
        }
        String[] parametersString = sourceLocator.getQuery().split("\\&");
        StringBuilder newParameters = new StringBuilder();
        StringBuilder participantUri = new StringBuilder();
        for (String part : parametersString) {
            String[] queryElement = part.split("\\=");
            if (queryElement[0].equals("participant")) {
                participantUri.append(sourceLocator.getScheme());
                participantUri.append("://");
                participantUri.append(queryElement[1]);
                participantUri.append("/audio");
            } else {
                if (newParameters.length() == 0) {
                    newParameters.append("?");
                } else {
                    newParameters.append("&");
                }
                newParameters.append(queryElement[0]);
                newParameters.append("=");
                newParameters.append(queryElement[1]);
            }
        }
        if (participantUri.length() > 0) {
            participantUri.append(newParameters);
        }

        final String locator = participantUri.toString();
        return new URI(locator);
    }
}
