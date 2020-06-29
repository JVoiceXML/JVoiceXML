/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2020 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.xml.vxml;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * JVoiceXML extensions to {@link Prompt}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JVoiceXmlPrompt extends Prompt {
    /**
     * Sets the priority of this prompt.
     */
    public static final String ATTRIBUTE_PRIORITY = "priority";

    /**
     * Set the valid attributes for this node.
     */
    static {
        ATTRIBUTE_NAMES.add(ATTRIBUTE_PRIORITY);
    }

    /**
     * Construct a new data object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public JVoiceXmlPrompt() {
        super(null);
    }

    /**
     * Construct a new data object.
     * @param node The encapsulated node.
     */
    JVoiceXmlPrompt(final Node node) {
        super(node);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private JVoiceXmlPrompt(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new JVoiceXmlPrompt(n, factory);
    }
    
    /**
     * Retrieves the priority of this prompt.
     * @return the priority of this prompt,
     *          {@code PriorityType.APPEND.toString()} as a default value.
     */
    public String getPriority() {
        final String priority = getAttribute(ATTRIBUTE_PRIORITY);
        if (priority == null) {
            return PriorityType.APPEND.toString();
        }
        return priority;
    }
    
    /**
     * Retrieves the priority of this prompt as a priority.
     * @return priority of this prompt.
     */
    public PriorityType getPriorityAsPriorityType() {
        final String priority = getPriority();
        return PriorityType.valueOf(priority);
    }
    
    /**
     * Sets the priority.
     * @param value the priority to set
     * @since 0.7.9
     */
    public void setPriority(final String value) {
        setAttribute(ATTRIBUTE_PRIORITY, value);
    }

    /**
     * Sets the priority.
     * @param value the priority to set
     * @since 0.7.9
     */
    public void setPriority(final PriorityType value) {
        final String priority = value.toString();
        setPriority(priority);
    }

}
