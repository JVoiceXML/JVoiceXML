/*
 * File:    $RCSfile: ExecutablePlainForm.java,v $
 * Version: $Revision: 1.2 $
 * Date:    $Date: 2006/05/16 07:26:21 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.form;

import java.util.Collection;

import org.jvoicexml.interpreter.ExecutableForm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FormItemFactory;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.VoiceXmlNode;
import org.w3c.dom.NodeList;

/**
 * Implementation of an <code>ExecutableForm</code> for the
 * <code>&lt;form&gt;</code> tag.
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.2 $
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class ExecutablePlainForm
        implements ExecutableForm {
    /** The encapsulated form. */
    private final Form form;

    /**
     * Constructs a new object.
     * @param tag The form.
     */
    public ExecutablePlainForm(final Form tag) {
        form = tag;
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        final String formId = form.getId();
        if (formId == null) {
            return ExecutableForm.UNNAMED_FORM;
        }

        return formId;
    }

    /**
     * {@inheritDoc}
     */
    public NodeList getChildNodes() {
        return form.getChildNodes();
    }


    /**
     * {@inheritDoc}
     */
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) {
        final Collection<FormItem> items = new java.util.ArrayList<FormItem>();

        final NodeList children = form.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final VoiceXmlNode node = (VoiceXmlNode) children.item(i);
            final FormItem item =
                    FormItemFactory.getFormItem(context, node);

            if (item != null) {
                items.add(item);
            }
        }

        return items;
    }
}
