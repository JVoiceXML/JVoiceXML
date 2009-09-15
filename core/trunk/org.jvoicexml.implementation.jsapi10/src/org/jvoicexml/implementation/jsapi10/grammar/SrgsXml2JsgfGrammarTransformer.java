/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.jsapi10.grammar;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * An instance of this class is able to transform a SRGS grammar with XML format
 * into RuleGrammar instance.<br>
 * The mime type of the accepted grammar is application/srgs+xml.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class SrgsXml2JsgfGrammarTransformer
        implements GrammarTransformer {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger
            .getLogger(SrgsXml2JsgfGrammarTransformer.class);

    /**
     * Standard constructor to instantiate as much
     * <code>GrammarTransformer</code> as you need.
     */
    public SrgsXml2JsgfGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.SRGS_XML;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getTargetType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarImplementation<? extends Object> createGrammar(
            final UserInput input, final GrammarDocument grammar,
            final GrammarType type) throws NoresourceError,
            UnsupportedFormatError, BadFetchError {
        /* First make sure, the type is supported */
        if (type != GrammarType.SRGS_XML) {
            throw new UnsupportedFormatError();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating new JSGF grammar");
        }

        // prepare a reader to read in the grammar string
        final StringReader reader = new StringReader(grammar.getDocument());
        final String str = processGrammar(reader);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transformed JSGF grammar:");
            LOGGER.debug(str);
        }

        // Load the grammar
        final StringReader jsgfReader = new StringReader(str);
        return input.loadGrammar(jsgfReader, GrammarType.JSGF);
    }

    /**
     * Transforms the <code>&lt;grammar&gt;</code> node of the given document.
     * @param document the document to transform.
     * @return transformed JSGF grammar.
     */
    private String processGrammar(final StringReader reader)
        throws BadFetchError {
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            final InputStream in =
                SrgsXml2JsgfGrammarTransformer.class.getResourceAsStream(
                        "srgs2jsgftransformer.xsl");
            final StreamSource xslSource = new StreamSource(in);
            final Templates templates =  factory.newTemplates(xslSource);
            final Transformer transformer = templates.newTransformer();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final Result result = new StreamResult(out);
            final Source source = new StreamSource(reader);
            transformer.transform(source, result);
            final byte[] bytes = out.toByteArray();
            return new String(bytes);
        } catch (TransformerConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (TransformerException e) {
            throw new BadFetchError(e.getMessage(), e);
        }
    }
}
