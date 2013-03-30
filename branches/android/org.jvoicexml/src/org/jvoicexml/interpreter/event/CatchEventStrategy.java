/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/event/CatchEventStrategy.java $
 * Version: $LastChangedRevision: 2655 $
 * Date:    $Date: 2011-05-18 03:13:16 -0500 (mié, 18 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategyExecutor;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to execute a user defined catch node.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2655 $
 *
 * @see org.jvoicexml.ImplementationPlatform
 * @see org.jvoicexml.xml.vxml.AbstractCatchElement
 */
final class CatchEventStrategy
        extends AbstractEventStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(CatchEventStrategy.class);

    /**
     * Constructs a new object.
     */
    CatchEventStrategy() {
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
     * @param node
     *        the node to execute.
     * @param type
     *        the event type.
     */
    public CatchEventStrategy(final VoiceXmlInterpreterContext ctx,
                                  final VoiceXmlInterpreter ip,
                                  final FormInterpretationAlgorithm interpreter,
                                  final FormItem formItem,
                                  final VoiceXmlNode node, final String type) {
        super(ctx, ip, interpreter, formItem, node, type);
    }

    /**
     * {@inheritDoc}
     */
    public void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();
        if (fia == null) {
            LOGGER.warn("Unable to process event '"
                    + event.getEventType()
                    + "' No reference to a form FIA!");
            return;
        }
        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        context.enterScope(Scope.ANONYMOUS);

        // Declare the special variable _event which contains the name of the
        // event that was thrown.
        final ScriptingEngine scripting = context.getScriptingEngine();
        final String name = event.getEventType();
        scripting.setVariable("_event", name);

        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();

        try {
            final FormItem item = getCurrentFormItem();
            final VoiceXmlNode node = getVoiceXmlNode();
            executor.executeChildNodes(context, interpreter, fia, item, node);
        } finally {
            context.exitScope(Scope.ANONYMOUS);
        }
    }
}
