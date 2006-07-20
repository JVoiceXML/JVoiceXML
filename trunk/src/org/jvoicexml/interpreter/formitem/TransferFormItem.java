/*
 * File:    $RCSfile: TransferFormItem.java,v $
 * Version: $Revision: 1.6 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group
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

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.EventHandler;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * An input item which transfers the user to another telephone number. If the
 * transfer returns control, the field variable will be set to the result
 * status.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.6 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class TransferFormItem
        extends InputItem {
    /**
     * Create a new transfer input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public TransferFormItem(final VoiceXmlInterpreterContext context,
                            final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }


    /**
     * {@inheritDoc}
     */
    public EventHandler accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        return visitor.visitTransferFormItem(this);
    }

    /**
     * {@inheritDoc}
     */
    public Object getShadowVariableContainer() {
        return null;
    }
}
