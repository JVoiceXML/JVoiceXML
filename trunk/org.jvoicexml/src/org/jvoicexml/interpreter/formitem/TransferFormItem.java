/*
 * File:    $HeadURL$
 * Version: $Revision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group
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
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Transfer;

/**
 * An input item which transfers the user to another telephone number. If the
 * transfer returns control, the field variable will be set to the result
 * status.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 */
public final class TransferFormItem
        extends AbstractInputItem {
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
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitTransferFormItem(this);
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends Object> getShadowVariableContainer() {
        return TransferShadowVarContainer.class;
    }

    /**
     * Retrieves the destination of this transfer by evaluating the
     * <code>dest</code> and the <code>destexpr</code> attributes.
     * @return destination of this transfer.
     * @throws SemanticError
     *         Error evaluating the <code>destexpr</code> attribute.
     * @throws BadFetchError
     *         No destination specified.
     * @since 0.7
     * TODO evaluate the telephone URI after RFC2806
     */
    public String getDest() throws SemanticError, BadFetchError {
        final Transfer transfer = getTransfer();
        if (transfer == null) {
            return null;
        }
        String dest = transfer.getDest();
        if (dest != null) {
            return dest;
        }
        dest = transfer.getDestexpr();
        if (dest == null) {
            throw new BadFetchError("Either one of \"dest\" or \"destexpr\""
            		+ " must be specified!");
        }
        final VoiceXmlInterpreterContext context = getContext();
        final ScriptingEngine scripting = context.getScriptingEngine();
        return (String) scripting.eval(dest);
    }

    /**
     * Checks if the requested transfer is a bridge transfer.
     * @return <code>true</code> if the requested transfer is bridged.
     */
    public boolean isBridged() {
        final Transfer transfer = getTransfer();
        if (transfer == null) {
            return false;
        }

        return transfer.isBridge();
    }

    /**
     * Gets the transfer node belonging to this {@link TransferFormItem}.
     *
     * @return The related transfer node or <code>null</code> if there is no
     *          node.
     * @since 0.7
     */
    private Transfer getTransfer() {
        final VoiceXmlNode node = getNode();
        if (node == null) {
            return null;
        }

        if (!(node instanceof Transfer)) {
            return null;
        }

        return (Transfer) node;
    }

}
