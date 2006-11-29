/*
 * File:    $RCSfile: BlockFormItem.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
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

package org.jvoicexml.interpreter.formitem;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * A sequence of procedural statements used for prompting and computation, but
 * not for gathering input. A block has a (normally implicit) form item variable
 * that is set to true, just before it is interpreted.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class BlockFormItem
        extends ControlItem {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(BlockFormItem.class);

    /**
     * Create a new block form item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public BlockFormItem(final VoiceXmlInterpreterContext context,
                         final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }

    /**
     * Mark this form item visited by setting the form item variable to
     * <code>true</code>.
     */
    public void setVisited() {
        setFormItemVariable(Boolean.TRUE);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("block '" + getName() + "' marked as visited.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public EventHandler accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        return visitor.visitBlockFormItem(this);
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>null</code>, since a <code>&lt;block&gt;</code> must
     *         not contain nested catches.
     */
    @Override
    public Collection<AbstractCatchElement> getCatchElements() {
        return null;
    }
}
