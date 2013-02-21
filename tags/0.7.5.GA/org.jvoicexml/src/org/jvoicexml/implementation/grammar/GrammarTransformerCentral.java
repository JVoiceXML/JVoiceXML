/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.implementation.grammar;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.GrammarImplementation;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.srgs.ModeType;

/**
 * The <code>GrammarTransformerCentral</code> takes control over the
 * process of transforming a grammar. It provides some convenience
 * methods as an entry point for the transformation.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class GrammarTransformerCentral {

    /**
     * Registered transformers. Transformers are kept with their source
     * type as a primary key. Values are maps with the target type as key and
     * the transformer as value.
     */
    private final
        Map<GrammarType, Map<GrammarType, GrammarTransformer>> transformer;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GrammarTransformerCentral.class);

    /**
     * Constructs a new object.
     */
    public GrammarTransformerCentral() {
        transformer = new java.util.HashMap<GrammarType,
            Map<GrammarType, GrammarTransformer>>();
    }

    /**
     * Creates a {@link GrammarImplementation} out of the given
     * {@link GrammarDocument}.
     *
     * @param input
     *        The user input.
     * @param grammar
     *        The grammar to be transformed.
     * @param type
     *        The target type of the grammar.
     * @return RuleGrammar A grammar, that can be passed to an ASR
     *         engine.
     *
     * @exception NoresourceError
     *         Error accessing the input device.
     * @exception UnsupportedFormatError
     *         If an unsupported grammar has to be transformed.
     * @exception BadFetchError
     *         If any dependent grammar could not be fetched correctly.
     *
     */
    public GrammarImplementation<? extends Object> createGrammar(
            final UserInput input, final GrammarDocument grammar,
            final GrammarType type)
            throws NoresourceError, UnsupportedFormatError, BadFetchError {
        if (type == null) {
            throw new UnsupportedFormatError(
                "Cannot determine a transformer if no source type is given!");
        }

        /* lets see, if there is any transformer, supporting this type */
        final GrammarType sourceType = grammar.getMediaType();
        final ModeType mode = grammar.getModeType();
        final Collection<GrammarType> supportedTypes =
            input.getSupportedGrammarTypes(mode);
        final GrammarTransformer trans =
            getTransformer(sourceType, supportedTypes);
        if (trans == null) {
            throw new UnsupportedFormatError(
                    "No transformer for source type '" + type + "'!");
        }

        // OK, we got one, lets create a grammar implementation.
        final GrammarImplementation<?> impl =
            trans.transformGrammar(input, grammar);
        if (impl == null) {
            throw new BadFetchError("Transformer did not return a grammar!");
        }
        return impl;
    }

    /**
     * Determine an appropriate transformer.
     * @param sourceType type of the document.
     * @param supportedTypes types that are supported by the platform.
     * @return transformer to use or <code>null</code> if no transformer
     *         was found.
     */
    private GrammarTransformer getTransformer(final GrammarType sourceType,
            final Collection<GrammarType> supportedTypes) {
        // Get all transformers for the source type.
        final Map<GrammarType, GrammarTransformer> map =
            transformer.get(sourceType);
        if (map == null) {
            return null;
        }

        // First try to get an ident transformer to avoid information loss
        // by transforming grammars.
        final GrammarTransformer identTransformer = map.get(sourceType);
        if (identTransformer != null) {
            return identTransformer;
        }

        // Try to find a matching transformer.
        for (GrammarType targetType : supportedTypes) {
            final GrammarTransformer trans = map.get(targetType);
            if (trans != null) {
                return trans;
            }
        }

        // No transformer found.
        return null;
    }

    /**
     * Creates a RuleGrammar out of the given textual grammar and
     * type.
     *
     * @since 0.3
     *
     * @param input
     *        The current user input.
     * @param grammar
     *        The grammar to be transformed.
     * @return RuleGrammar A grammar, that can be passed to an ASR
     *         engine.
     *
     * @exception NoresourceError
     *         Error accessing the input device.
     * @throws UnsupportedFormatError
     *         If an unsupported grammar has to be transformed.
     * @throws BadFetchError
     *         If any dependent grammar could not be fetched correctly.
     */
    public GrammarImplementation<? extends Object> createGrammar(
            final UserInput input, final GrammarDocument grammar)
            throws NoresourceError, UnsupportedFormatError, BadFetchError {
        final GrammarType type = grammar.getMediaType();

        return createGrammar(input, grammar, type);
    }

    /**
     * Adds the given list of transformers.
     * @param grammarTransfromer List with transformers to add.
     *
     * @since 0.5
     */
    public void setTransformer(
            final List<GrammarTransformer> grammarTransfromer) {
        for (GrammarTransformer trans : grammarTransfromer) {
            addTransformer(trans);
        }

    }

    /**
     * Adds the given grammar transformer.
     * @param trans The <code>GrammarTransformer</code> to add.
     */
    public void addTransformer(final GrammarTransformer trans) {
        final GrammarType sourceType = trans.getSourceType();

        // Check if the source type is already mapped to a transformer.
        // If this is not the case, create a new mapping.
        Map<GrammarType, GrammarTransformer> map =
            transformer.get(sourceType);
        if (map == null) {
            map = new java.util.HashMap<GrammarType, GrammarTransformer>();
            transformer.put(sourceType, map);
        }

        // Create a new mapping for the target type.
        final GrammarType targetType = trans.getTargetType();

        map.put(targetType, trans);

        LOGGER.info("added grammar transformer " + trans.getClass()
                + " for type '" + sourceType + "' to "
                + "'" + targetType + "'");
    }
}
