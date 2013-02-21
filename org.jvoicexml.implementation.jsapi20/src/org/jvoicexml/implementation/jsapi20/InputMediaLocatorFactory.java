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

import org.jvoicexml.implementation.SpokenInput;

/**
 * A factory to create a media locators for the spoken input.
 * <p>
 * The audio is streamed from a data source to a data sink. The result
 * of the call to {@link #getSourceMediaLocator(SpokenInput)} is
 * used to retrieve the URI of the JSAPI 2.0 compliant speech recognizer.
 * The result of the call to
 * {@link #getSinkMediaLocator(SpokenInput, URI)} is used to
 * determine the sink of the data stream.
 * </p>
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public interface InputMediaLocatorFactory {
    /**
     * Factory method to create a media locator for the given recognizer
     * input.
     * <p>
     * This defines the data source of the audio stream. This is the media
     * locator which is used as the media locator for the
     * {@link javax.speech.AudioManager}.
     * </p>
     * @param input the input for which to create a media locator.
     * @return media locator.
     * @exception URISyntaxException
     *            error creating the URI
     */
    URI getSourceMediaLocator(final SpokenInput input)
        throws URISyntaxException;

    /**
     * Factory method to create a media locator for the given
     * recognizer input.
     * <p>
     * This defines the data sink of the audio stream.
     * </p>
     * @param input the input for which to create the media locator
     * @param sourceLocator the source media locator
     * @return media locator for the sink
     * @exception URISyntaxException
     *            error creating the URI
     */
    URI getSinkMediaLocator(final SpokenInput input,
            final URI sourceLocator) throws URISyntaxException;
}
