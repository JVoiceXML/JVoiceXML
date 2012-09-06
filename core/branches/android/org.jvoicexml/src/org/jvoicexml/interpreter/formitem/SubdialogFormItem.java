/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/SubdialogFormItem.java $
 * Version: $Revision: 2249 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
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

import java.net.URI;
import java.net.URISyntaxException;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Subdialog;

/**
 * A <code>&lt;>subdialog&gt;</code> input item is roughly like a function call.
 * It invokes another dialog on the current page, or invokes another VoiceXML
 * document. It returns an ECMAScript Object as its result.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2249 $
 */
public final class SubdialogFormItem
        extends AbstractInputItem {
    /**
     * Create a new subdialog input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public SubdialogFormItem(final VoiceXmlInterpreterContext context,
                             final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }

    /**
     * Gets the subdialog belonging to this {@link SubdialogFormItem}.
     *
     * @return The related subdialog or <code>null</code> if there is no
     *  subdialog.
     * @since 0.7.4
     */
    private Subdialog getSubdialog() {
        final VoiceXmlNode node = getNode();

        if (node == null) {
            return null;
        }

        if (!(node instanceof Subdialog)) {
            return null;
        }

        return (Subdialog) node;
    }

    /**
     * Retrieves the URI of the subdialog to execute.
     * @return URI of the subdialog.
     * @since 0.7.4
     * @exception URISyntaxException
     *            if there is no valid uri
     */
    public URI getSubdialigUri() throws URISyntaxException {
        final Subdialog subdialog = getSubdialog();
        return subdialog.getSrcUri();
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitSubdialogFormItem(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void resetShadowVarContainer() throws SemanticError {
    }

    /**
     * {@inheritDoc}
     *
     * @todo Implement this method.
     */
    @Override
    public Class<? extends Object> getShadowVariableContainer() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModal() {
        return false;
    }
}
