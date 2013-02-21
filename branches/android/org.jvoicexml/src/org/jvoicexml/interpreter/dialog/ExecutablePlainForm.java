/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/dialog/ExecutablePlainForm.java $
 * Version: $LastChangedRevision: 2612 $
 * Date:    $Date: 2011-02-28 11:58:33 -0600 (lun, 28 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.dialog;
import java.util.Collection;

import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.formitem.FormItemFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Filled;
import org.jvoicexml.xml.vxml.Form;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Implementation of a {@link Dialog} for the
 * <code>&lt;form&gt;</code> tag.
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2612 $
 *
 * @since 0.4
 */
public final class ExecutablePlainForm
        implements Dialog {
    /** The encapsulated form. */
    private Form form;

    /** Id of this dialog. */
    private String id;

    /** Form items of this dialog. */
    private Collection<FormItem> items;

    /**
     * Constructs a new object.
     */
    public ExecutablePlainForm() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNode(final XmlNode node) throws IllegalArgumentException {
        if (!(node instanceof Form)) {
            throw new IllegalArgumentException("'" + node + "' is not a from!");
        }
        form = (Form) node;
        id = DialogIdFactory.getId(form);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<XmlNode> getChildNodes() {
        return form.getChildren();
    }

    /**
     * {@inheritDoc}
     */
    public Collection<FormItem> getFormItems(
            final VoiceXmlInterpreterContext context) throws BadFetchError {
        if (items == null) {
            items = new java.util.ArrayList<FormItem>();

            final NodeList children = form.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final VoiceXmlNode node = (VoiceXmlNode) children.item(i);
                final FormItem item =
                    FormItemFactory.getFormItem(context, node);
                if (item != null) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<Filled> getFilledElements() {
        return form.getChildNodes(Filled.class);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<AbstractCatchElement> getCatchElements() {
        if (form == null) {
            return null;
        }

        final Collection<AbstractCatchElement> catches =
                new java.util.ArrayList<AbstractCatchElement>();
        final NodeList children = form.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);
            if (child instanceof AbstractCatchElement) {
                final AbstractCatchElement catchElement =
                        (AbstractCatchElement) child;
                catches.add(catchElement);
            }
        }

        return catches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog clone() {
        try {
            return (Dialog) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
