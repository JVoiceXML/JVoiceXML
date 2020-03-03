/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.GrammarDocument;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.UserInput;
import org.jvoicexml.event.EventSubscriber;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

/**
 * Deactivates grammars, once the grammars gets out of scope. This class is
 * intended to decouple the {@link ActiveGrammarSet} from the used
 * {@link ImplementationPlatform}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.3
 */
class GrammarDeactivator implements ActiveGrammarSetObserver, EventSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(GrammarDeactivator.class);

    /** The current implementation platform. */
    private final ImplementationPlatform platform;

    /** The active grammarss. */
    private final ActiveGrammarSet grammars;
    
    /**
     * Constructs a new object.
     * 
     * @param ip
     *            the current implementation platform
     * @param set
     *            the active grammars
     */
    public GrammarDeactivator(final ImplementationPlatform ip,
            final ActiveGrammarSet set) {
        platform = ip;
        grammars = set;
    }

    /**
     * {@inheritDoc} Deactivates the grammars in the current implementation
     * platform.
     */
    @Override
    public void removedGrammars(final ActiveGrammarSet set,
            final Collection<GrammarDocument> removed) {
        if (platform.isHungup()) {
            LOGGER.info("no need to cleanup grammars. caller has hung up.");
            return;
        }
        deactivateGramars(removed);
    }

    /**
     * Deactivates the given set of grammars.
     * @param grammars the grammars to deactivate
     * @since 0.7.9
     */
    private void deactivateGramars(final Collection<GrammarDocument> grammars) {
        try {
            final UserInput input = platform.getUserInput();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("deactivating " + grammars.size() + " grammars...");
            }
            final int num = input.deactivateGrammars(grammars);
            LOGGER.info(num + " grammars deactivated");
        } catch (NoresourceError e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ConnectionDisconnectHangupEvent e) {
            LOGGER.error(e.getMessage(), e);
        } catch (BadFetchError e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final JVoiceXMLEvent event) {
        if (!event.isType(ConnectionDisconnectHangupEvent.EVENT_TYPE)) {
            return;
        }
        if (platform.isHungup()) {
            LOGGER.info("no need to cleanup grammars. caller has hung up.");
            return;
        }
        LOGGER.info("hangup: deactivating all grammars");
        final Collection<GrammarDocument> allGrammars = grammars.getGrammars();
        deactivateGramars(allGrammars);
    }
}
