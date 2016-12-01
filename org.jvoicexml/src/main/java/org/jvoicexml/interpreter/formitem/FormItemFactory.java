/*
 * File:    $RCSfile: FormItemFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Map;

import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Initial;
import org.jvoicexml.xml.vxml.ObjectTag;
import org.jvoicexml.xml.vxml.Record;
import org.jvoicexml.xml.vxml.Subdialog;
import org.jvoicexml.xml.vxml.Transfer;

/**
 * Factory for form items.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class FormItemFactory {
    /** Known form items. */
    private static final Map<Class<?>, AbstractFormItem> FORM_ITEMS;

    static {
        FORM_ITEMS = new java.util.HashMap<Class<?>, AbstractFormItem>();
        FORM_ITEMS.put(Block.class, new BlockFormItem());
        
        FORM_ITEMS.put(Block.class, new BlockFormItem());
        FORM_ITEMS.put(Initial.class, new InitialFormItem());
        FORM_ITEMS.put(Field.class, new FieldFormItem());
        FORM_ITEMS.put(ObjectTag.class, new ObjectFormItem());
        FORM_ITEMS.put(Record.class, new RecordFormItem());
        FORM_ITEMS.put(Subdialog.class, new SubdialogFormItem());
        FORM_ITEMS.put(Transfer.class, new TransferFormItem());
    }

    /**
     * Do not create from outside.
     */
    private FormItemFactory() {
    }

    /**
     * Factory method to get a {@link org.jvoicexml.interpreter.FormItem}
     * for a {@link VoiceXmlNode}.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param node
     *        The voice xml node for which to get a form item.
     * @return A corresponding form item or <code>null</code> if the given
     *         node cannot be transferred into a form item.
     */
    public static AbstractFormItem getFormItem(
            final VoiceXmlInterpreterContext context, final VoiceXmlNode node) {
        final Class<?> clazz = node.getClass();
        final AbstractFormItem template = FORM_ITEMS.get(clazz);
        if (template == null) {
            return null;
        }
        return template.newInstance(context, node);
    }
}
