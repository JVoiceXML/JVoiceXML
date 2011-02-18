/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Deactivates grammars, once the grammars gets out of scope.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.3
 */
class GrammarDeactivator implements ActiveGrammarSetObserver {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(GrammarDeactivator.class);

    /** The current implementation platform. */
    private final ImplementationPlatform platform;

    /**
     * Constructs a new object.
     * @param ip the current implementation platform
     */
    public GrammarDeactivator(final ImplementationPlatform ip) {
        platform = ip;
    }

    /**
     * {@inheritDoc}
     * Deactivates the grammars in the current implementation platform.
     */
    @Override
    public void removedGrammars(final ActiveGrammarSet set,
            final Collection<GrammarDocument> removed) {
        try {
            final UserInput input = platform.getUserInput();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deactivating " + removed.size()
                        + " grammars...");
            }
            input.deactivateGrammars(removed);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("...grammars deactivated");
            }
        } catch (NoresourceError e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ConnectionDisconnectHangupEvent e) {
            LOGGER.error(e.getMessage(), e);
        } catch (BadFetchError e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
