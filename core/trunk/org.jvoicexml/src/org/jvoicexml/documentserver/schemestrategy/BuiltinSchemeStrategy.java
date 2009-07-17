/*
 * File:    $HeadURL:  $
 * Version: $LastChangedRevision: 643 $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.documentserver.schemestrategy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.jvoicexml.Session;
import org.jvoicexml.documentserver.SchemeStrategy;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.jvoicexml.xml.vxml.RequestMethod;

/**
 * Schemestrategy for builtin grammars.
 * <p>
 * This class provides basic support for built grammars as specified at
 * <a href="http://www.w3.org/TR/voicexml20#dmlABuiltins>
 * http://www.w3.org/TR/voicexml20#dmlABuiltins</a>.
 * </p>
 * <p>
 * The URI can be platform dependent. This implementation expects the URIs
 * to be of the following form
 * <pre>
 * builtin://&lt;mode&gt;/<&lt;type&gt;[?parameters]
 * </pre>
 * where mode is a lower-case presentation of
 * {@link org.jvoicexml.xml.srgs.ModeType} and type and parameters as
 * specified in <a href="http://www.w3.org/TR/voicexml20#dmlABuiltins>
 * http://www.w3.org/TR/voicexml20#dmlABuiltins</a>.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: $
 * @since 0.7.1
 */
public final class BuiltinSchemeStrategy implements SchemeStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FileSchemeStrategy.class);

    /** Scheme for which this scheme strategy is responsible. */
    public static final String SCHEME_NAME = "builtin";

    /** Creator for boolean grammars. */
    private final BooleanGrammarCreator booleanCreator;

    /**
     * Constructs a new object.
     */
    public BuiltinSchemeStrategy() {
        booleanCreator = new BooleanGrammarCreator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream(final Session session, final URI uri,
            final RequestMethod method, final long timeout,
            final Map<String, Object> parameters)
            throws BadFetchError {
        final String path = uri.getPath();
        final SrgsXmlDocument document;
        if (path.startsWith("/boolean")) {
            document = booleanCreator.createGrammar(uri);
        } else {
            LOGGER.warn("builtin grammar for '" + uri + "' is not supported."
                    + " Ignoring...");
            try {
                document = new SrgsXmlDocument();
            } catch (ParserConfigurationException e) {
                throw new BadFetchError(e.getMessage(), e);
            }
        }
        String xml;
        try {
            xml = document.toXml();
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
        return new ByteArrayInputStream(xml.getBytes());
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
    public void sessionClosed(final Session session) {
        // TODO Auto-generated method stub
        
    }

}
