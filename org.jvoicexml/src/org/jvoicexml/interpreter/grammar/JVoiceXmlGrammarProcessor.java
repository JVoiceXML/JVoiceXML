/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/grammar/JVoiceXmlGrammarProcessor.java $
 * Version: $LastChangedRevision: 2899 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar;

import java.util.Collection;

import org.jvoicexml.Configuration;
import org.jvoicexml.ConfigurationException;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.GrammarProcessor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.IllegalAttributeException;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * The <code>GrammarProcessor</code> is the main entry point for
 * grammar processing.<br>
 * This class provides a lean method interface to process a grammar
 * in a VoiceXML file.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2899 $
 */
public final class JVoiceXmlGrammarProcessor
        implements GrammarProcessor {
    /** grammar identifier central. */
    private GrammarIdentifierCentral identifier;

    /** The grammar loader. */
    private final GrammarLoader loader;

    /**
     * Private constructor to prevent manual instantiation.
     */
    public JVoiceXmlGrammarProcessor() {
        identifier = new GrammarIdentifierCentral();
        loader = new GrammarLoader();
    }

    /**
     * {@inheritDoc}
     * This implementation loads the {@link GrammarIdentifier}s 
     * from the configuration. They can also be
     * added manually by
     * {@link GrammarIdentifierCentral#addIdentifier(GrammarIdentifier)}
     * TODO Rewrite the configuration to let the centrals be configured.
     */
    @Override
    public void init(final Configuration configuration)
        throws ConfigurationException {
        final Collection<GrammarIdentifier> identifiers =
            configuration.loadObjects(GrammarIdentifier.class, "jvxmlgrammar");
        for (GrammarIdentifier current : identifiers) {
            identifier.addIdentifier(current);
        }
    }

    /**
     * Sets the central to identify grammars.
     * @param central GrammarIdentifierCentral
     * @since 0.5
     */
    public void setGrammaridentifier(final GrammarIdentifierCentral central) {
        identifier = central;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GrammarDocument process(
            final VoiceXmlInterpreterContext context,
            final FetchAttributes attributes,
            final Grammar grammar)
            throws NoresourceError, BadFetchError, UnsupportedFormatError,
                SemanticError {
        /*
         * check if grammar is external or not an process with
         * appropriates methods
         */
        final GrammarDocument document;
        try {
            document = loader.loadGrammarDocument(context, attributes, grammar);
        } catch (IllegalAttributeException e) {
            throw new BadFetchError(e.getMessage(), e);
        }

        // Identify the grammar.
        identifyGrammar(grammar, document);
        adaptMode(grammar, document);
        return document;
    }

    /**
     * Identifies the given grammar.
     * @param grammar the grammar to identify
     * @param document current grammar document
     * @return identified grammar document
     * @throws UnsupportedFormatError
     *         if the grammar type is not supported.
     * @since 0.7.3
     */
    private GrammarDocument identifyGrammar(final Grammar grammar,
            final GrammarDocument document) throws UnsupportedFormatError {
        // now we need to know the actual type.
        final GrammarType expectedType = grammar.getType();
        final GrammarType actualType =
                identifier.identifyGrammar(document, expectedType);
        // let's check, if the declared type is supported.
        if (actualType == null) {
            throw new UnsupportedFormatError(
                    grammar.getType() + " is not supported.");
        }

        document.setMediaType(actualType);

        // Yes they really match. return the external grammar.
        return document;
    }

    /**
     * Adapts the mode of the grammar in the document.
     * @param grammar the identified grammar
     * @param document the resulting grammar document
     * @since 0.7.5
     */
    private void adaptMode(final Grammar grammar,
            final GrammarDocument document) {
        final ModeType mode = grammar.getMode();
        document.setModeType(mode);
    }
}
