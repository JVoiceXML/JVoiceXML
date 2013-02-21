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
package org.jvoicexml.interpreter.grammar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * The <code>GrammarIdentifierCentral</code> takes control over the
 * process of identifying a grammar. It provides some convenience
 * methods as an entry point for the identification.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/ </a>
 * </p>
 */
public final class GrammarIdentifierCentral {
    /**
     * A Set of registered identifiers.
     */
    private final Collection<GrammarIdentifier> identifier;

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GrammarIdentifierCentral.class);


    /**
     * Constructs a new object.
     */
    public GrammarIdentifierCentral() {
        identifier = new java.util.ArrayList<GrammarIdentifier>();
    }

    /**
     * Identifies the given grammar. If the grammar could not be
     * identified an UnsupportedFormatError is thrown.
     *
     * @param grammar
     *        The given grammar which will be identified.
     * @return The actual type of the grammar.
     * @throws UnsupportedFormatError
     *         If no identifier is able to identify this grammar.
     */
    public GrammarType identifyGrammar(final GrammarDocument grammar)
            throws UnsupportedFormatError {
        /* first of all make sure, grammar is not null nor empty */
        if (grammar == null) {
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
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("trying to identify grammar with '"
                        + current.getClass() + "'");
            }
            final GrammarType currentType = current.identify(grammar);
            if (currentType != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("identified grammar with type '"
                                 + currentType + "'");
                }

                return currentType;
            }
        }

        LOGGER.warn("unable to identify grammar!");

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
            getAvailableIdentifier(final GrammarType type) {
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
            final GrammarType currentType = current.getSupportedType();
            if (type != currentType) {
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
    public boolean typeSupported(final GrammarType type) {
        final List<GrammarIdentifier> list = getAvailableIdentifier(type);

        return !list.isEmpty();
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

        final GrammarType type = id.getSupportedType();

        LOGGER.info("added grammar identifier " + id.getClass()
                + " for type '" + type + "'");
    }
}
