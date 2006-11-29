/*
 * File:    $RCSfile: GotoStrategy.java,v $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.net.URI;
import java.util.Collection;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextDocumentEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormEvent;
import org.jvoicexml.event.plain.jvxml.GotoNextFormItemEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy of the FIA to execute a <code>&lt;goto&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Goto
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 */
final class GotoStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GotoStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Goto.ATTRIBUTE_EXPR);
        EVAL_ATTRIBUTES.add(Goto.ATTRIBUTE_EXPRITEM);
    }

    /** The target of the goto. */
    private String next;

    /** The target item of the goto. */
    private String nextItem;

    /**
     * Constructs a new object.
     */
    GotoStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes()
            throws ErrorEvent {
        next = (String) getAttribute(Goto.ATTRIBUTE_NEXT);
        if (next != null) {
            return;
        }

        if (isAttributeDefined(Goto.ATTRIBUTE_EXPR)) {
            next = (String) getAttribute(Goto.ATTRIBUTE_EXPR);
            if (next != null) {
                return;
            }
        }

        nextItem = (String) getAttribute(Goto.ATTRIBUTE_NEXTITEM);
        if (nextItem != null) {
            return;
        }

        if (isAttributeDefined(Goto.ATTRIBUTE_EXPRITEM)) {
            final Object expritem = getAttribute(Goto.ATTRIBUTE_EXPRITEM);
            if (expritem != null) {
                nextItem = expritem.toString();

                return;
            }
        }

        throw new BadFetchError(
                "goto: Exactly one of 'next', 'expr', 'nextitem' or 'expritem' "
                + "must be specified!");
    }

    /**
     * {@inheritDoc}
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        if (nextItem != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to item '" + nextItem + "'...");
            }

            throw new GotoNextFormItemEvent(nextItem);
        }

        if (next.startsWith("#")) {
            final String nextForm = next.substring(1);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to form '" + nextForm + "'...");
            }

            throw new GotoNextFormEvent(nextForm);
        } else {
            final URI nextUri;
            try {
                nextUri = new URI(next);
            } catch (java.net.URISyntaxException use) {
                throw new BadFetchError(use);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("going to uri '" + nextUri + "'...");
            }

            throw new GotoNextDocumentEvent(nextUri);
        }
    }
}
