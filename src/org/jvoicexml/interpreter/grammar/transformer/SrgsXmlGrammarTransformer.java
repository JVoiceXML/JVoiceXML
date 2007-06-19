/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.SrgsXmlGrammarImplementation;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements the GrammarTransformer interface. An instance of this
 * class is able to transform a SRGS grammar with XML format into RuleGrammar
 * instance. The mime type of the accepted grammar is application/srgs+xml.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsXmlGrammarTransformer
        implements GrammarTransformer {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SrgsXmlGrammarTransformer.class);

    /**
     * Standard constructor to instantiate as much
     * <code>GrammarTransformer</code> as you need.
     */
    public SrgsXmlGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSourceType() {
        return GrammarType.JSGF;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getTargetType() {
        return GrammarType.SRGS_XML;
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
            LOGGER.debug("creating new SRGS XML grammar");
        }

        // prepare a reader to read in the grammar string
        final StringReader reader = new StringReader(grammar.getDocument());

        // create a xml input source from the grammar
        final InputSource inputSource = new InputSource(reader);

        SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(inputSource);
        } catch (ParserConfigurationException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (SAXException e) {
            throw new BadFetchError(e.getMessage(), e);
        } catch (IOException e) {
            throw new BadFetchError(e.getMessage(), e);
        }

        return new SrgsXmlGrammarImplementation(doc);
    }
}
