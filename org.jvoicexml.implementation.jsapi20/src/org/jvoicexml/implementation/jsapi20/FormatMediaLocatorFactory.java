/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SynthesizedOutput;

/**
 * A media locator factory to convert audio format.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.7
 */
public final class FormatMediaLocatorFactory
        implements OutputMediaLocatorFactory, InputMediaLocatorFactory {

    /** Either capture or playback. */
    private String protocol;

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

    /**
     * Sets the protocol to either capture or playback.
     * 
     * @param name
     *            name of the protocol
     */
    public void setProtocol(final String name) {
        if (!name.equals("capture") && !name.equals("playback")) {
            throw new IllegalArgumentException(
                    "protocol must be either 'capture' or 'playback'");
        }
        protocol = name;
    }

    /**
     * Sets if signed data should be used.
     * 
     * @param on
     *            <code>true</code> if signed data should be used.
     */
    public void setSigned(final boolean on) {
        signed = on;
    }

    /**
     * Sets the encoding.
     * 
     * @param enc
     *            encoding to use.
     */
    public void setEncoding(final String enc) {
        encoding = enc;
    }

    /**
     * Sets the data rate.
     * 
     * @param value
     *            rate.
     */
    public void setRate(final long value) {
        rate = value;
    }

    /**
     * Sets the number of bits.
     * 
     * @param value
     *            number of bits
     */
    public void setBits(final long value) {
        bits = value;
    }

    /**
     * Sets the number of audio channels.
     * 
     * @param number
     *            number of audio channels.
     */
    public void setChannels(final long number) {
        channels = number;
    }

    /**
     * Sets the endianess.
     * 
     * @param value
     *            endianess.
     */
    public void setEndian(final String value) {
        endian = value;
    }

    /**
     * {@inheritDoc}
     */
    public URI getSourceMediaLocator(final SynthesizedOutput output)
            throws URISyntaxException {
        return getSourceMediaLocator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getSourceMediaLocator(final SpokenInput input)
            throws URISyntaxException {
        return getSourceMediaLocator();
    }

    /**
     * Retrieves the source media locator.
     * 
     * @return the source media locator
     * @throws URISyntaxException
     *             error creating the media locator
     */
    private URI getSourceMediaLocator() throws URISyntaxException {
        final StringBuilder str = new StringBuilder();
        str.append(protocol);
        str.append("://audio?");
        maybeAppendParameter(str, "bits", bits);
        maybeAppendParameter(str, "channels", channels);
        maybeAppendParameter(str, "encoding", encoding);
        maybeAppendParameter(str, "endian", endian);
        maybeAppendParameter(str, "rate", rate);
        appendParameter(str, "signed", signed);
        final String locator = str.toString();
        return new URI(locator);
    }

    /**
     * Adds the given parameter to string.
     * 
     * @param str
     *            string to append to
     * @param name
     *            name of the parameter
     * @param value
     *            value of the parameter
     */
    private void appendParameter(final StringBuilder str, final String name,
            final boolean value) {
        if (str.charAt(str.length() - 1) != '?') {
            str.append('&');
        }
        str.append(name);
        str.append("=");
        str.append(value);
    }

    /**
     * Adds the given parameter to string if its value is not 0.
     * 
     * @param str
     *            string to append to
     * @param name
     *            name of the parameter
     * @param value
     *            value of the parameter
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
     * 
     * @param str
     *            string to append to
     * @param name
     *            name of the parameter
     * @param value
     *            value of the parameter
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
        return getSinkMediaLoactor(sourceLocator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getSinkMediaLocator(final SpokenInput input,
            final URI sourceLocator) throws URISyntaxException {
        return getSinkMediaLoactor(sourceLocator);
    }

    /**
     * Retrieves the source media locator.
     * 
     * @param sourceLocator
     *            the media locator for the source.
     * @return media locator.
     * @throws URISyntaxException
     *             error creating the media locator.
     */
    private URI getSinkMediaLoactor(final URI sourceLocator)
            throws URISyntaxException {
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
