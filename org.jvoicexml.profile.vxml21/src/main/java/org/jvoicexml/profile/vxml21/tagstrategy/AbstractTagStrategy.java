/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.profile.TagStrategy;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Skeleton for a {@link TagStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.3.1
 */
abstract public class AbstractTagStrategy implements Cloneable, TagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(AbstractTagStrategy.class);

    /** Map with evaluated attributes. */
    private Map<String, Object> attributes;

    /**
     * Constructs a new object.
     */
    public AbstractTagStrategy() {
        attributes = new java.util.HashMap<String, Object>();
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy newInstance() {
        return (TagStrategy) clone();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Retrieves all attributes of the current tag and store their values in the
     * attributes map. If there is no value in the prompt, the method tries to
     * find a value that has been set via a <code>&lt;property&gt;</code> tag.<br>
     * Implementations are requested to obtain the values via the
     * {@link #getAttribute(String)} method.
     * </p>
     */
    public void getAttributes(final VoiceXmlInterpreterContext context,
            final FormInterpretationAlgorithm fia, final VoiceXmlNode node) {
        if (node == null) {
            LOGGER.warn("cannot get attributes from null");
        }

        attributes.clear();

        // Check all possible attributes, if it is defined
        // 1. in the node
        // 2. as a property local to the form item
        // 3. as a property outside the form item
        final Collection<String> names = node.getAttributeNames();
        for (String name : names) {
            String value = node.getAttribute(name);
            if (value == null) {
                if (fia != null) {
                    value = fia.getLocalProperty(name);
                }
                if (value == null) {
                    value = context.getProperty(name);
                }
            }

            if (value != null) {
                attributes.put(name, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
        final Collection<String> evalAttributes = getEvalAttributes();
        if (evalAttributes == null) {
            return;
        }

        final DataModel model = context.getDataModel();
        for (String name : evalAttributes) {
            final Object expr = attributes.get(name);
            if (expr != null) {
                final String exprstring = expr.toString();
                final String cleanedExprstring = StringEscapeUtils
                        .unescapeXml(exprstring);
                final Object value = model.evaluateExpression(
                        cleanedExprstring, Object.class);
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
        final AbstractTagStrategy strategy;

        try {
            strategy = (AbstractTagStrategy) super.clone();
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
    protected Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Retrieves the value of an attribute that may alternatively defined by an
     * evaluated expression.
     * 
     * @param pure
     *            the original name of the attribute
     * @param expr
     *            the name of the attribute that has been evaluated by the
     *            scripting engine
     * @return value of the attribute
     * @throws BadFetchError
     *             if both or none of the attributes are defined
     * @since 0.7.7
     */
    protected Object getAttributeWithAlternativeExpr(final DataModel model,
            final String pure, final String expr) throws BadFetchError {
        final boolean pureDefined = isAttributeDefined(model, pure);
        final boolean exprDefined = isAttributeDefined(model, expr);

        // None or both are defined
        if ((pureDefined && exprDefined) || (!pureDefined && !exprDefined)) {
            throw new BadFetchError("Exactly one of '" + pure + "' or '" + expr
                    + "' must be defined!");
        }

        if (pureDefined) {
            return getAttribute(pure);
        }

        return getAttribute(expr);
    }

    /**
     * Retrieves the value of an optional attribute that may alternatively
     * defined by an evaluated expression.
     *
     * @param pure
     *            the original name of the attribute
     * @param expr
     *            the name of the attribute that has been evaluated by the
     *            scripting engine
     * @return value of the attribute, or null if neither pure nor expr are defined
     * @throws BadFetchError
     *             if both of the attributes are defined
     * @since 0.7.7
     */
    protected Object getOptionalAttributeWithAlternativeExpr(final DataModel model,
            final String pure, final String expr) throws BadFetchError {
        final boolean pureDefined = isAttributeDefined(model, pure);
        final boolean exprDefined = isAttributeDefined(model, expr);

        // Both are defined
        if ((pureDefined && exprDefined)) {
            throw new BadFetchError("Exactly one of '" + pure + "' or '" + expr
                    + "' may be defined!");
        }

        if (pureDefined) {
            return getAttribute(pure);
        }

        if (exprDefined) {
            return getAttribute(expr);
        }

        return null;
    }

    /**
     * Retrieves the value of an attribute that may alternatively defined by an
     * evaluated expression.
     * 
     * @param pure
     *            the original name of the attribute
     * @param expr
     *            the name of the attribute that has been evaluated by the
     *            scripting engine
     * @return value of the attribute, {@code null} if both or none of the
     *         attributes are defined
     * @since 0.7.7
     */
    protected Object getAndCheckAttributeWithAlternativeExpr(
            final DataModel model, final String pure, final String expr) {
        final boolean pureDefined = isAttributeDefined(model, pure);
        final boolean exprDefined = isAttributeDefined(model, expr);

        // None or both are defined
        if ((pureDefined && exprDefined) || (!pureDefined && !exprDefined)) {
            return null;
        }

        if (pureDefined) {
            return getAttribute(pure);
        }

        return getAttribute(expr);
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
    protected boolean isAttributeDefined(DataModel model, final String name) {
        final Object value = attributes.get(name);
        if (value == null) {
            return false;
        }
        return model.getUndefinedValue() != value;
    }

    /**
     * {@inheritDoc}
     */
    public void dumpNode(final DataModel model, final VoiceXmlNode node) {
        final StringBuilder str = new StringBuilder();

        str.append("node: '");
        str.append(node.getNodeName());
        str.append("'");

        final Collection<String> attributeNames = attributes.keySet();
        for (String attribute : attributeNames) {
            final Object value = attributes.get(attribute);

            str.append(" ");
            str.append(attribute);
            str.append(": '");
            final String converted = model.toString(value);
            str.append(converted);
            str.append("'");
        }

        LOGGER.debug(str.toString());
    }

    /**
     * {@inheritDoc}
     */
    public void executeLocal(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
    }
}
