/*
 * File:    $HeadURL$
 * Version: $Revision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
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
 * This input item invokes a platform-specific <em>object</em> with various
 * parameters. The result of the platform object is an ECMAScript Object. One
 * platform object could be a builtin dialog that gathers credit card
 * information. Another could gather a text message using some proprietary DTMF
 * text entry method. There is no requirement for implementations to provide
 * platform-specific objects, although implementations must handle the
 * <code>&lt;object&gt;</code> element by throwing
 * <code>error.unsupported.objectname</code> if the particular platform-specific
 * object is not supported (note that <code>objectname</code> in
 * <code>error.unsupported.objectname</code> is a fixed string, so not
 * substituted with the name of the unsupported object; more specific error
 * information may be provided in the event <code>_message</code> special
 * variable as described in
 * <a href="http://www.w3.org/TR/voicexml20#dml5.2.2">Section 5.2.2</a>).
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * @see org.jvoicexml.event.error.UnsupportedObjectnameError
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class ObjectFormItem
        extends InputItem {
    /**
     * Creates a new object input item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding xml node in the VoiceXML document.
     */
    public ObjectFormItem(final VoiceXmlInterpreterContext context,
                          final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitObjectFormItem(this);
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>null</code> since there is no shadow var container.
     */
    public Class<? extends Object> getShadowVariableContainer() {
        return null;
    }
}
