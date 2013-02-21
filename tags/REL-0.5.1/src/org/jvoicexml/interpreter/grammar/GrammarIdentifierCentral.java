/*
 * File:    $RCSfile: GrammarIdentifierCentral.java,v $
 * Version: $Revision: 1.25 $
 * Date:    $Date: 2006/07/17 14:12:24 $
 * Author:  $Author: schnelle $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;

/**
 * The <code>GrammarIdentifierCentral</code> takes control over the
 * process of identifying a grammar. It provides some convinience
 * methodes as an entry point for the identification.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision: 1.25 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/ </a>
 * </p>
 */
final class GrammarIdentifierCentral {
    /**
     * A Set of registered identifiers.
     */
    private final Collection<GrammarIdentifier> identifier;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GrammarIdentifierCentral.class);


    /**
     * Constructs a new object.
     */
    GrammarIdentifierCentral() {
        identifier = new java.util.ArrayList<GrammarIdentifier>();
    }

    /**
     * Returns true, if grammars actual type is the same type as
     * estimated type.
     *
     * @param grammar
     *        the grammar to be identified.
     * @param estimatedType
     *        the estimated type
     * @return true if grammars actual type is the same a s estimated
     *         type, else false.
     * @throws UnsupportedFormatError
     *         if actual type of grammar is not supported.
     */
    public boolean isType(final String grammar, final String estimatedType)
            throws UnsupportedFormatError {
        /*
         * first of all make sure, grammar and type are not null nor
         * empty
         */
        if (grammar != null && estimatedType != null && !grammar.equals("")
            && !estimatedType.equals("")) {

            final String identifiedGrammar = identifyGrammar(grammar);

            if (estimatedType.equals(identifiedGrammar)) {
                return true;
            }

        }

        return false;
    }

    /**
     * Identifies the given grammar. If the grammar could not be
     * identified an UnsupportedFormatError is thrown.
     *
     * @param grammar
     *        The given grammar which will be identified.
     * @return String The actual type of the grammar.
     * @throws UnsupportedFormatError
     *         If no identifier is able to identify this grammar.
     */
    public String identifyGrammar(final String grammar)
            throws UnsupportedFormatError {
        /* first of all make sure, grammar is not null nor empty */
        if ((grammar == null) || "".equals(grammar)) {
            throw new UnsupportedFormatError("Cannot identify a null grammar!");
        }

        if (identifier.isEmpty()) {
            LOGGER.warn("no registered identifier!");

            return null;
        }

        /*
         * alright let's see, if there is any identifier,
         * supporting the type
         */
        for (GrammarIdentifier current : identifier) {
            /* try to identify */
            final String currentType = current.identify(grammar);
            if (currentType != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("identified grammar with type '"
                                 + currentType + "'");
                }

                return currentType;
            }
        }

        LOGGER.warn("unable to identify gramamr!");

        return null;
    }


    /**
     * Returns a List of identifiers, that have been registered and
     * are supporting the given type.
     *
     * @param type
     *        Required type.
     * @return List List of available identifiers.
     */
    protected List<GrammarIdentifier>
            getAvailableIdentifier(final String type) {
        final List<GrammarIdentifier> list =
                new ArrayList<GrammarIdentifier>(identifier);
        if (type == null) {
            list.clear();
            LOGGER.warn("no identifiers for null type!");
            return list;
        }

        final Iterator<GrammarIdentifier> it = list.iterator();

        while (it.hasNext()) {
            final GrammarIdentifier current = it.next();
            final String currentType = current.getSupportedType();
            if (!type.equals(currentType)) {
                it.remove();
            }
        }

        return list;
    }

    /**
     * Checks wether the given type is supported by one of the
     * registered identifiers or not. Returns true if type is
     * supported, else false
     *
     * @param type
     *        String representing the type
     * @return boolean true if type is supported, else false.
     */
    public boolean typeSupported(final String type) {
        final List<GrammarIdentifier> list = getAvailableIdentifier(type);
        if (list.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * Adds the given list of idententifiers.
     * @param grammarIdenifier List with identifiers to add.
     *
     * @since 0.5
     */
    public void setIdentifier(final List<GrammarIdentifier> grammarIdenifier) {
        for (GrammarIdentifier id : grammarIdenifier) {
            addIdentifier(id);
        }

    }

    /**
     * Adds the given grammar identifier.
     * @param id The <code>GrammarIdentifier</code> to add.
     */
    public void addIdentifier(final GrammarIdentifier id) {
        identifier.add(id);

        if (LOGGER.isInfoEnabled()) {
            final String type = id.getSupportedType();

            LOGGER.info("added grammar identifier " + id.getClass()
                        + " for type '" + type + "'");
        }
    }
}
