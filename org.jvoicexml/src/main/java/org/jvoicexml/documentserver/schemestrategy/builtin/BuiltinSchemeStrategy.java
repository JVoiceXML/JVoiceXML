/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.documentserver.schemestrategy.builtin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.UnsupportedBuiltinError;
import org.jvoicexml.event.error.UnsupportedElementError;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Schemestrategy for builtin grammars.
 * <p>
 * This class provides basic support for built grammars as specified at
 * <a href="http://www.w3.org/TR/voicexml20#dmlABuiltins">
 * http://www.w3.org/TR/voicexml20#dmlABuiltins</a>.
 * </p>
 * <p>
 * The URI can be platform dependent. This implementation expects the URIs
 * to be of the following form
 * <pre>builtin:&lt;mode&gt;/&lt;type&gt;[?parameters]</pre>
 * where mode is a lower-case presentation of
 * {@link org.jvoicexml.xml.srgs.ModeType} and type and parameters as
 * specified in <a href="http://www.w3.org/TR/voicexml20#dmlABuiltins">
 * http://www.w3.org/TR/voicexml20#dmlABuiltins</a>.
 * <p>
 * Custom grammar types can be added by
 * {@link #addGrammarCreator(GrammarCreator)} or
 * {@link #setGrammarCreators(Collection)}.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
public final class BuiltinSchemeStrategy implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(BuiltinSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "builtin";

    /** Known grammar creators. */
    private final Map<String, GrammarCreator> creators;

    /**
     * Constructs a new object.
     */
    public BuiltinSchemeStrategy() {
        creators = new java.util.HashMap<String, GrammarCreator>();
    }

    /**
     * Adds the specified grammar creators to the list of known grammar
     * creators.
     * @param col the creators to add
     * @since 0.7.5
     */
    public void setGrammarCreators(final Collection<GrammarCreator> col) {
        for (GrammarCreator creator : col) {
            addGrammarCreator(creator);
        }
    }

    /**
     * Adds the specified grammar creator to the list of known grammar creators.
     * @param creator the creator to add
     * @since 0.7.5
     */
    public void addGrammarCreator(final GrammarCreator creator) {
        final String type = creator.getTypeName();
        creators.put(type, creator);
        LOGGER.info("added builtin grammar creator '" + creator.getClass()
                + "' for type '" + type + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(final SessionIdentifier sessionId,
            final URI uri, final RequestMethod method, final long timeout,
            final Collection<KeyValuePair> parameters)
            throws BadFetchError, UnsupportedElementError, IOException {
        final String type = extractBuiltinType(uri);
        final GrammarCreator creator = creators.get(type);
        if (creator == null) {
            throw new UnsupportedBuiltinError("builtin type '" + type
                    + "' is not supported!");
        }
        final byte[] bytes = creator.createGrammar(uri);
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Extracts the builtin type from the URI.
     * @param uri the given URI.
     * @return extracted builtin type
     */
    private String extractBuiltinType(final URI uri) {
        final String schemeSpecificPart = uri.getSchemeSpecificPart();
        final String[] path = schemeSpecificPart.split("/");
        String type = path[1];
        final int pos = type.indexOf('?');
        if (pos >= 0) {
            type = type.substring(0, pos);
        }
        return type.toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getScheme() {
        return SCHEME_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sessionClosed(final SessionIdentifier sessionId) {
    }

}
