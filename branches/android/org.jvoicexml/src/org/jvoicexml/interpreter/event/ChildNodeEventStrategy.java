/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/event/ChildNodeEventStrategy.java $
 * Version: $LastChangedRevision: 2655 $
 * Date:    $Date: 2011-05-18 03:13:16 -0500 (mié, 18 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.TagStrategyExecutor;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to execute a child node.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2655 $
 */
final class ChildNodeEventStrategy
        extends AbstractEventStrategy {
    /**
     * Constructs a new object.
     */
    ChildNodeEventStrategy() {
    }

    /**
     * Construct a new object.
     *
     * @param ctx
     *        The VoiceXML interpreter context.
     * @param ip
     *        The VoiceXML interpreter.
     * @param interpreter
     *        The FIA.
     * @param formItem
     *        The current form item.
     * @param node
     *        The node to execute.
     * @param type
     *        The event type.
     */
    public ChildNodeEventStrategy(final VoiceXmlInterpreterContext ctx,
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

        final VoiceXmlInterpreterContext context =
            getVoiceXmlInterpreterContext();
        final VoiceXmlInterpreter interpreter = getVoiceXmlInterpreter();
        final TagStrategyExecutor executor = getTagStrategyExecutor();
        executor.executeChildNodes(context, interpreter, fia,
                getCurrentFormItem(), getVoiceXmlNode());
    }
}
