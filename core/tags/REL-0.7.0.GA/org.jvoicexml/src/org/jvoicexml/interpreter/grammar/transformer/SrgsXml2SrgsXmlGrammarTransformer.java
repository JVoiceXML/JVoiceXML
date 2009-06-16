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

import java.io.StringReader;

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
 * into an {@link org.jvoicexml.xml.srgs.SrgsXmlDocument}.
 * The mime type of the accepted grammar is <code>application/srgs+xml</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public final class SrgsXml2SrgsXmlGrammarTransformer
        implements GrammarTransformer {
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = Logger
            .getLogger(SrgsXml2SrgsXmlGrammarTransformer.class);

    /**
     * Constructs a new object.
     */
    public SrgsXml2SrgsXmlGrammarTransformer() {
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

        return input.loadGrammar(reader, GrammarType.SRGS_XML);
    }
}
