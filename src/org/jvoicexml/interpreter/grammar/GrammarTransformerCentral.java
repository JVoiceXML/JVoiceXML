/*
 * File:    $RCSfile: GrammarTransformerCentral.java,v $
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
package org.jvoicexml.interpreter.grammar;

import java.util.List;
import java.util.Map;

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>GrammarTransformerCentral</code> takes control over the
 * process of transforming a grammar. It provides some convinience
 * methodes as an entry point for the transformation.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
final class GrammarTransformerCentral {

    /**
     *Registered transformers.
     */
    private final Map<GrammarType, GrammarTransformer> transformer;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GrammarTransformerCentral.class);


    /**
     * Constructs a new object.
     */
    GrammarTransformerCentral() {
        transformer = new java.util.HashMap<GrammarType, GrammarTransformer>();
    }

    /**
     * Creates a Rulegrammar out of the given textual grammar and
     * type.
     *
     * @param context
     *        The current VoiceXML interpreter context.
     * @param grammar
     *        The grammar to be transformed.
     * @param type
     *        The type of the grammar.
     * @return RuleGrammar A grammar, that can be passed to an ASR
     *         engine.
     *
     * @exception NoresourceError
     *         Error accessing the input device.
     * @exception UnsupportedFormatError
     *         If an unsupported grammar has to be transformed.
     * @exception BadFetchError
     *         If any dependend grammar could not be fetched correctly.
     *
     */
    public RuleGrammar createGrammar(final VoiceXmlInterpreterContext context,
                                     final String grammar,
                                     final GrammarType type)
            throws NoresourceError, UnsupportedFormatError, BadFetchError {

        /* lets see, if there is any transformer, supporting this type */
        final GrammarTransformer trans = transformer.get(type);
        if (trans == null) {
            throw new UnsupportedFormatError("No transformer for type '" + type
                                             + "'!");
        }

        /* ok, we got one, lets create a RuleGrammar */
        final ImplementationPlatform platform =
                context.getImplementationPlatform();

        final UserInput input = platform.getUserInput();

        return trans.createGrammar(input, grammar, type);

    }

    /**
     * Creates a Rulegrammar out of the given textual grammar and
     * type.
     *
     * @since 0.3
     *
     * @param context
     *        The current VoiceXML interpreter context.
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
     *         If any dependend grammar could not be fetched correctly.
     */
    public RuleGrammar createGrammar(final VoiceXmlInterpreterContext context,
                                     final ExternalGrammar grammar)
            throws NoresourceError, UnsupportedFormatError, BadFetchError {
        final String contents = grammar.getContents();
        final GrammarType type = grammar.getMediaType();

        return createGrammar(context, contents, type);
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
     * Adds the given grammar identifier.
     * @param trans The <code>GrammarTrasnformer</code> to add.
     */
    public void addTransformer(final GrammarTransformer trans) {
        final GrammarType type = trans.getSupportedType();

        transformer.put(type, trans);

        if (LOGGER.isInfoEnabled()) {

            LOGGER.info("added grammar transformer " + trans.getClass()
                        + " for type '" + type + "'");
        }
    }


    /**
     * Checks if there is any registered GrammarTransformer supporting
     * the given type.
     *
     * @param type
     *        a String representing a grammar type
     * @return true if there is at least one GrammarTransformer for
     *         the given type.
     */
    public boolean isSupported(final String type) {
        final GrammarTransformer trans = transformer.get(type);

        return trans != null;
    }
}
