/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/event/DefaultRepromptEventStrategy.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.event;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;

/**
 * Default strategy to perform a reprompt if the user did not specify an
 * event handler.
 * <p>
 * This can happen in case of
 * <ul>
 * <li>help</li>
 * <li>noinput</li>
 * <li>nomatch</li>
 * </ul>
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.7
 */
final class DefaultRepromptEventStrategy
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(DefaultRepromptEventStrategy.class);

    /**
     * Constructs a new object.
     */
    DefaultRepromptEventStrategy() {
    }

    /**
     * Constructs a new object.
     *
     * @param ctx
     *        the VoiceXML interpreter context.
     * @param ip
     *        the VoiceXML interpreter.
     * @param interpreter
     *        the FIA.
     * @param formItem
     *        the current form item.
     * @param type
     *        the event type.
     */
    public DefaultRepromptEventStrategy(final VoiceXmlInterpreterContext ctx,
                                  final VoiceXmlInterpreter ip,
                                  final FormInterpretationAlgorithm interpreter,
                                  final FormItem formItem,
                                  final String type) {
        super(ctx, ip, interpreter, formItem, null, type);
    }

    /**
     * {@inheritDoc}
     */
    public void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        LOGGER.info("performing an implicitly defined reprompt");
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        fia.setReprompt(true);
    }
}
