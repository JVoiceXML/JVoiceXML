/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/AbstractControlItem.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * @version $Revision: 2129 $
 */
abstract class AbstractControlItem
        extends AbstractFormItem implements ControlItem {
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
