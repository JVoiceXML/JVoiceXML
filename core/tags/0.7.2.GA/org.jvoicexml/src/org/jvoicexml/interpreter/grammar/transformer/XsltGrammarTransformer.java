/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
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
package org.jvoicexml.interpreter.grammar.transformer;

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
 * This class implements the GrammarTransformer interface based on XSL
 * transformation.
 *
 * @author Dirk Schnelle-Walka
 *
 * @see org.jvoicexml.interpreter.grammar.GrammarTransformer
 * @version $Revision$
 * @since 0.7.2
 */
public abstract class XsltGrammarTransformer
        implements GrammarTransformer {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
        Logger.getLogger(XsltGrammarTransformer.class);

    /**
     * Constructs a new object.
     */
    public XsltGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public final GrammarImplementation<?> createGrammar(
                final UserInput input,
                final GrammarDocument grammar,
                final GrammarType type)
            throws BadFetchError, NoresourceError, UnsupportedFormatError {
        final GrammarType sourceType = getSourceType();
        if (type != sourceType) {
            throw new UnsupportedFormatError("Grammar type must be "
                    + sourceType + " but was " + type);
        }

        // prepare a reader to read in the grammar string
        final GrammarType targetType = getTargetType();
        final StringReader reader = new StringReader(grammar.getDocument());
        final String str = processGrammar(reader);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("transformed " + targetType + " grammar:");
            LOGGER.debug(str);
        }

        // Load the grammar
        final StringReader transformedReader = new StringReader(str);
        return input.loadGrammar(transformedReader, targetType);
    }

    /**
     * Retrieves the resource name of the style sheet to use for the
     * transformation.
     * @return name of the resource
     */
    protected abstract String getStylesheetResourceName();

    /**
     * Transforms the <code>&lt;grammar&gt;</code> node of the given document.
     * @param reader the reader for the source grammar
     * @return transformed JSGF grammar.
     * @exception BadFetchError
     *            error transforming the grammar
     */
    private String processGrammar(final StringReader reader)
        throws BadFetchError {
        final TransformerFactory factory = TransformerFactory.newInstance();
        try {
            final String resource = getStylesheetResourceName();
            final InputStream in =
                getClass().getResourceAsStream(resource);
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

