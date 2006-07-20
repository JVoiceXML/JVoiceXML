/*
 * File:    $RCSfile: JVoiceXmlGrammarRegistry.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2005/12/13 08:25:12 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.speech.recognition.RuleGrammar;

import org.jvoicexml.interpreter.GrammarRegistry;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.ScopedMap;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import java.util.Collection;

/**
 * Implementation of a <code>GrammarRegistry</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class JVoiceXmlGrammarRegistry
        implements GrammarRegistry {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(JVoiceXmlGrammarRegistry.class);

    /** The scope aware map of all grammars. */
    private ScopedMap<String, RuleGrammar> grammars;

    /**
     * Constructs a new object.
     * @param context
     *        The current VoiceXML interpreter context.
     */
    public JVoiceXmlGrammarRegistry(final VoiceXmlInterpreterContext context) {
        grammars = new ScopedMap<String, RuleGrammar>(context);
    }

    /**
     * {@inheritDoc}
     */
    public void addGrammar(final RuleGrammar grammar) {
        if (grammar == null) {
            LOGGER.warn("cannot add a null grammar");

            return;
        }

        final String name = grammar.getName();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("adding grammar '" + name + "'");
        }

        grammars.put(name, grammar);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<RuleGrammar> getGrammars() {
        return grammars.values();
    }
}
