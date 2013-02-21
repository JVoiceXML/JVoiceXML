/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.interpreter.ControlItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Basic functionality of a {@link ControlItem}.
 *
 * @see AbstractInputItem
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
abstract class AbstractControlItem
        extends AbstractFormItem implements ControlItem {
    /**
     * Constructs a object item as a template.
     */
    public AbstractControlItem() {
    }

    /**
     * Create a new control item.
     *
     * @param context
     *        the current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        the corresponding XML node in the VoiceXML document.
     */
    public AbstractControlItem(final VoiceXmlInterpreterContext context,
            final VoiceXmlNode voiceNode) {
        super(context, voiceNode);
    }
}
