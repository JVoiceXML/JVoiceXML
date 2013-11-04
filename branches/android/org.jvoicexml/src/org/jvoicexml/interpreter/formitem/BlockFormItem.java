/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/formitem/BlockFormItem.java $
 * Version: $LastChangedRevision: 3242 $
 * Date:    $Date: 2012-10-08 15:41:57 +0200 (Mon, 08 Oct 2012) $
 * Author:  $LastChangedBy: schnelle $
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

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormItemVisitor;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.AbstractCatchElement;
import org.jvoicexml.xml.vxml.Block;

/**
 * A sequence of procedural statements used for prompting and computation, but
 * not for gathering input. A block has a (normally implicit) form item variable
 * that is set to true, just before it is interpreted.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3242 $
 */
public final class BlockFormItem
        extends AbstractControlItem {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(BlockFormItem.class);

    /**
     * Constructs a new object as a template.
     */
    public BlockFormItem() {
    }

    /**
     * Create a new block form item.
     *
     * @param context
     *        The current <code>VoiceXmlInterpreterContext</code>.
     * @param voiceNode
     *        The corresponding XML node in the VoiceXML document.
     * @throws IllegalArgumentException
     *         if the given node is not a {@link Block}
     */
    public BlockFormItem(final VoiceXmlInterpreterContext context,
                         final VoiceXmlNode voiceNode)
                                 throws IllegalArgumentException {
        super(context, voiceNode);
        if (!(voiceNode instanceof Block)) {
            throw new IllegalArgumentException("Node must be a <block>");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractFormItem newInstance(
            final VoiceXmlInterpreterContext ctx,
            final VoiceXmlNode voiceNode) {
        return new BlockFormItem(ctx, voiceNode);
    }

    /**
     * Mark this form item visited by setting the form item variable to
     * <code>true</code>.
     */
    public void setVisited() {
        try {
            setFormItemVariable(Boolean.TRUE);
        } catch (SemanticError e) {
            LOGGER.error("error marking '" + getName() + "' as visited");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("block '" + getName() + "' marked as visited.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent {
        visitor.visitBlockFormItem(this);
    }

    /**
     * {@inheritDoc}
     *
     * @return <code>null</code>, since a <code>&lt;block&gt;</code> must
     *         not contain nested catches.
     */
    @Override
    public Collection<AbstractCatchElement> getCatchElements() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isModal() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ScriptingEngine scripting) throws SemanticError,
            BadFetchError {
        final String name = getName();
        final Object expression = evaluateExpression(scripting);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initializing form item '" + name + "'");
        }
        scripting.setVariable(name, expression);
        LOGGER.info("initialized block form item '" + name + "' with '"
                + expression + "'");
    }
}
