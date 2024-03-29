/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.SsmlParsingStrategy;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Skeleton for a {@link SsmlParsingStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.4
 */
abstract class AbstractSsmlParsingStrategy
        implements Cloneable, SsmlParsingStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(AbstractSsmlParsingStrategy.class);

    /** Map with evaluated attributes. */
    private Map<String, Object> attributes;

    /**
     * Constructs a new object.
     */
    AbstractSsmlParsingStrategy() {
        attributes = new java.util.HashMap<String, Object>();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves all attributes of the current tag and store their values in the
     * attributes map. If there is no value in the prompt, the method tries to
     * find a value that has been set via a {@code &lt;property&gt;} tag.<br>
     * Implementations are requested to obtain the values via the
     * {@link #getAttribute(String)} method.
     * </p>
     */
    @Override
    public void getAttributes(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia, final VoiceXmlNode node) {
        if (node == null) {
            LOGGER.warn("cannot get attributes from null");
        }

        attributes.clear();

        // Check all possible attributes, if it is defined
        // 1. in the node
        // 2. as a property.
        final Collection<String> names = node.getAttributeNames();
        for (String name : names) {
            String value = node.getAttribute(name);
            if (value == null) {
                value = context.getProperty(name);
            }

            if (value != null) {
                attributes.put(name, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
        final DataModel model = context.getDataModel();
        final Collection<String> evalAttributes = getEvalAttributes();
        if (evalAttributes == null) {
            return;
        }

        // Actually evalute the attributes
        for (String name : evalAttributes) {
            final Object expr = attributes.get(name);
            if (expr != null) {
                final String exprstring = expr.toString();
                final String unescapedExprstring = StringEscapeUtils
                        .unescapeXml(exprstring);
                Object value = model.evaluateExpression(unescapedExprstring,
                        Object.class);
                attributes.put(name, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws ErrorEvent {
    }

    /**
     * {@inheritDoc}
     *
     * The evaluated attributes are cloned via a shallow copy.
     */
    @Override
    public Object clone() {
        final AbstractSsmlParsingStrategy strategy;

        try {
            strategy = (AbstractSsmlParsingStrategy) super.clone();
            strategy.attributes = new java.util.HashMap<String, Object>(
                    attributes);
        } catch (CloneNotSupportedException cnse) {
            LOGGER.fatal("clone failed", cnse);
            return null;
        }

        return strategy;
    }

    /**
     * Retrieves the value of the given attribute.
     * 
     * @param name
     *            Name of the attribute.
     * @return Value of the attribute, <code>null</code> if the attribute has no
     *         associated value.
     */
    protected final Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Checks if the given attribute is defined, this means, neither
     * <code>null</code> nor <code>ScriptingEngine.getUndefinedValue()</code>.
     * 
     * @param model
     *            the data model to use
     * @param name
     *            Name of the attribute.
     * @return <code>true</code> if the attribute is defined.
     */
    protected boolean isAttributeDefined(final DataModel model, 
            final String name) {
        final Object value = attributes.get(name);
        if (value == null) {
            return false;
        }
        return model.getUndefinedValue() != value;
    }
}
