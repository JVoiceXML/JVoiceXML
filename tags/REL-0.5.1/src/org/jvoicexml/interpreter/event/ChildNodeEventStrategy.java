/*
 * File:    $RCSfile: ChildNodeEventStrategy.java,v $
 * Version: $Revision: 1.10 $
 * Date:    $Date: 2006/05/17 08:20:22 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.AbstractFormItem;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to execute a child node.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.10 $
 *
 * @see org.jvoicexml.implementation.ImplementationPlatform
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class ChildNodeEventStrategy
        extends AbstractEventStrategy {
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
                                  final AbstractFormItem formItem,
                                  final VoiceXmlNode node, final String type) {
        super(ctx, ip, interpreter, formItem, node, type);
    }

    /**
     * {@inheritDoc}
     */
    void process(final JVoiceXMLEvent event)
            throws JVoiceXMLEvent {
        final FormInterpretationAlgorithm fia =
                getFormInterpretationAlgorithm();

        fia.executeChildNodes(getFormItem(), getVoiceXmlNode());
    }
}
