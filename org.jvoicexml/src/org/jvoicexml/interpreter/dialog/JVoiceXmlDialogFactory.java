/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.DialogFactory;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.vxml.Vxml;


/**
 * Implementation of a {@link DialogFactory}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.4
 */
public final class JVoiceXmlDialogFactory
        implements DialogFactory {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(JVoiceXmlDialogFactory.class);

    /** Mapping of dialog tag names to dialogs. */
    private final Map<String, Dialog> dialogs;

    /**
     * Constructs a new object.
     */
    public JVoiceXmlDialogFactory() {
        dialogs = new java.util.HashMap<String, Dialog>();
    }

    /**
     * Adds the given mappings of dialogs to tag names.
     * @param mappings 
     * @since 0.7.5
     */
    public void setDialogs(final Map<String, Dialog> mappings) {
        final Collection<String> tags = mappings.keySet();
        for (String tag : tags) {
            final Dialog dialog = mappings.get(tag);
            addDialogMapping(tag, dialog);
        }
    }

    /**
     * Adds the dialog as a template to handle the given tag.
     * @param tag the tag that is handled by the dialog
     * @param dialog the dialog
     * @since 0.7.5
     */
    public void addDialogMapping(final String tag, final Dialog dialog) {
        dialogs.put(tag, dialog);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("added dialog template '" + dialog.getClass()
                    + "' to handle tag '" + tag + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Dialog> getDialogs(final Vxml vxml) {
        final Collection<Dialog> col = new java.util.ArrayList<Dialog>();

        // Check all child nodes, if there is either a form or a menu.
        // This has to be done one after the other to keep the order.
        final Collection<XmlNode> children = vxml.getChildren();
        for (XmlNode node : children) {
            final String tagname = node.getTagName();
            final Dialog template = dialogs.get(tagname);
            if (template != null) {
                final Dialog dialog = template.clone();
                dialog.setNode(node);
                col.add(dialog);
            }
        }

        return col;
    }
}
