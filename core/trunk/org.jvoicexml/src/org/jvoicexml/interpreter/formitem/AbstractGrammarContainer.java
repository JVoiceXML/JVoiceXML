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
package org.jvoicexml.interpreter.formitem;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RecognitionResult;
import org.jvoicexml.SemanticInterpretation;
import org.jvoicexml.interpreter.GrammarContainer;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.srgs.Grammar;

/**
 * Basic implementation of a {@link GrammarContainer}.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.1
 */
abstract class AbstractGrammarContainer
    extends AbstractInputItem
    implements GrammarContainer {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(FieldFormItem.class);

    /** List of converted grammars for this grammar container. */
    private final Collection<GrammarImplementation<?>> grammarImplementations;

    /** Grammars of this input item. */
    private Collection<Grammar> grammars;

    /**
     * Create a new object.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     */
    public AbstractGrammarContainer(final VoiceXmlInterpreterContext context,
            final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
        grammarImplementations =
            new java.util.ArrayList<GrammarImplementation<?>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addGrammar(final GrammarImplementation<?> impl) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added grammar " + impl.getGrammar() + " for field '"
                    + getName() + "'");
        }
        grammarImplementations.add(impl);
    }

    /**
     * Checks if the grammars defined for the field match the given utterance.
     * @param result the recognition result coming from the
     * {@link org.jvoicexml.ImplementationPlatform}.
     * @return <code>true</code> if the utterance is matched.
     * @since 0.7
     */
    public boolean accepts(final RecognitionResult result) {
        for (GrammarImplementation<?> impl : grammarImplementations) {
            if (impl.accepts(result)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds semantic interpretation information to the recognition result.
     * @param result recognition result as it is returned by the recognizer
     * @return recognition result with added semantic interpretation
     */
    public RecognitionResult addSemanticInterpretation(
            final RecognitionResult result) {
        for (GrammarImplementation<?> impl : grammarImplementations) {
            if (impl.accepts(result)) {
                SemanticInterpretation interpretation =
                    impl.getSemanticInterpretation(result);
                result.setSemanticInterpretation(interpretation);
                return result;
            }
        }
        return result;
    }

    /**
     * Adds custom grammars of the grammar container.
     * @param grams already found grammars
     */
    protected void addCustomGrammars(final Collection<Grammar> grams) {
    }

    /**
     * Get all nested definitions of a <code>&lt;grammar&gt;</code>.
     *
     * @return Collection about all nested <code>&lt;grammar&gt;</code> tags.
     */
    public final Collection<Grammar> getGrammars() {
        final VoiceXmlNode node = getNode();
        if (node == null) {
            return null;
        }

        if (grammars == null) {
            grammars = node.getChildNodes(Grammar.class);
        }
        return grammars;
    }
}
