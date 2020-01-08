/*
 * File:    $RCSfile: FetchAttributes.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
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

package org.jvoicexml;

import java.net.URI;

/**
 * A VoiceXML interpreter context needs to fetch VoiceXML documents, and other
 * resources, such as audio files, grammars, scripts, and objects. Each fetch
 * of the content associated with a URI is governed by  one of the attributes
 * in this container.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class FetchAttributes implements Cloneable {
    /**
     * Prefetch hint that indicates a file may be downloaded when the page is
     * loaded.
     * @see #fetchHint
     */
    public static final String HINT_PREFETCH = "prefetch";

    /**
     * Prefetch hint that indicates a file that should only be downloaded when
     * actually needed.
     * @see #fetchHint
     */
    public static final String HINT_SAFE = "safe";

    /**
     * The interval to wait for the content to be returned before throwing an
     * <code>error.badfetch</code> event.
     */
    private long fetchTimeout;

    /**
     * Defines when the interpreter context should retrieve content from the
     * server. If not specified, a value derived from the innermost relevant
     * <code>fetchhint</code> property is used.
     */
    private String fetchHint;

    /**
     * Indicates that the document is willing to use content whose age is no
     * greater than the specified time in seconds. The document is not willing
     * to use stale content, unless <code>maxstale</code> is also provided.
     * If not specified, a value derived from the innermost relevant
     * <code>maxage</code> property, if present, is used.
     */
    private long maxage;

    /**
     * Indicates that the document is willing to use content that has exceeded
     * its expiration time. If <code>maxstale</code> is assigned a value, then
     * the document is willing to accept content that has exceeded its
     * expiration time by no more than the specified number of seconds.
     * If not specified, a value derived from the innermost relevant
     * <code>maxstale</code> property, if present, is used.
     */
    private long maxstale;

    /**
     * The URI of the audio clip to play while the fetch is being done. If not
     * specified, the <code>fetchaudio</code> property is used, and if that
     * property is not set, no audio is played during the fetch. The fetching
     * of the audio clip is governed by the <code>audiofetchhint</code>,
     * <code>audiomaxage</code>, <code>audiomaxstale</code>, and
     * <code>fetchtimeout</code> properties in effect at the time of the fetch.
     * The playing of the audio clip is governed by the
     * <code>fetchaudiodelay</code>, and <code>fetchaudiominimum</code>
     * properties in effect at the time of the fetch.
     */
    private URI fetchAudio;

    /**
     * Construct a new object.
     */
    public FetchAttributes() {
    }

    /**
     * Copy constructor.
     * @param attributes
     *        fetch attributes to copy from.
     */
    public FetchAttributes(final FetchAttributes attributes) {
        fetchAudio = attributes.getFetchAudio();
        fetchHint = attributes.getFetchHint();
        fetchTimeout = attributes.getFetchTimeout();
        maxage = attributes.getMaxage();
        maxstale = attributes.getMaxstale();
    }

    /**
     * Retrieve the <code>fetchtimeout</code> attribute.
     * @param seconds Number of seconds to wait.
     * @see #fetchTimeout
     */
    public void setFetchTimeout(final long seconds) {
        fetchTimeout = seconds;
    }

    /**
     * Set the <code>fetchtimeout</code> attribute.
     * @return Number of seconds to wait.
     * @see #fetchTimeout
     */
    public long getFetchTimeout() {
        return fetchTimeout;
    }

    /**
     * Retrieve the <code>fetchhint</code> attribute.
     * @param hint Hint when the interpreter context should retrieve content
     *             from the server.
     * @see #fetchHint
     */
    public void setFetchHint(final String hint) {
        fetchHint = hint;
    }

    /**
     * Set the <code>fetchhint</code> attribute.
     * @return Hint when the interpreter context should retrieve content
     *             from the server.
     * @see #fetchHint
     */
    public String getFetchHint() {
        return fetchHint;
    }

    /**
     * Checks if the fetch hint has a value of {@link #HINT_PREFETCH}
     * @return true if the it is OK to prefetch the document.
     * @since 0.7.8
     */
    public boolean isFetchintPrefetch() {
        if (fetchHint == null) {
            return false;
        }
        return fetchHint.equalsIgnoreCase(HINT_PREFETCH);
    }

    /**
     * Checks if the fetch hint has a value of {@link #HINT_SAFE}
     * @return true if the document should be downloaded when actually needed.
     * @since 0.7.8
     */
    public boolean isFetchintSafe() {
        if (fetchHint == null) {
            return false;
        }
        return fetchHint.equalsIgnoreCase(HINT_SAFE);
    }
    
    /**
     * Retrieve the <code>maxage</code> attribute.
     * @param seconds Maximum age of documents in seconds.
     * @see #maxage
     */
    public void setMaxage(final long seconds) {
        maxage = seconds;
    }

    /**
     * Set the <code>maxage</code> attribute.
     * @return Maximum age of documents in seconds.
     * @see #maxage
     */
    public long getMaxage() {
        return maxage;
    }

    /**
     * Retrieve the <code>maxstale</code> attribute.
     * @param seconds Number of seconds to use content that has exceeded
     * its expiration time.
     * @see #maxstale
     */
    public void setMaxstale(final long seconds) {
        maxstale = seconds;
    }

    /**
     * Set the <code>maxstale</code> attribute.
     * @return Number of seconds to use content that has exceeded
     * its expiration time.
     * @see #maxstale
     */
    public long getMaxstale() {
        return maxstale;
    }

    /**
     * Set the <code>fetchAudio</code> attribute.
     * @param uri URI of the audio clip to play while the fetch is being done.
     * @see #fetchAudio
     */
    public void setFetchAudio(final URI uri) {
        fetchAudio = uri;
    }

    /**
     * Retrieve the <code>fetchAudio</code> attribute.
     * @return URI of the audio clip to play while the fetch is being done.
     */
    public URI getFetchAudio() {
        return fetchAudio;
    }
}

