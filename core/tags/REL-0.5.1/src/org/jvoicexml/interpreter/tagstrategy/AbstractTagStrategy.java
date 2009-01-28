/*
 * File:    $RCSfile: AbstractTagStrategy.java,v $
 * Version: $Revision: 1.10 $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;
import java.util.Map;

import org.jvoicexml.event.error.ErrorEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.VoiceXmlNode;
import org.mozilla.javascript.Context;

/**
 * Skeleton for a <code>TagStrategy</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.10 $
 * @since 0.3.1
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
abstract class AbstractTagStrategy
        implements Cloneable, TagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractTagStrategy.class);

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
     */
    public void getAttributes(final VoiceXmlInterpreterContext context,
                              final VoiceXmlNode node) {
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
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
        final Collection<String> evalAttributes = getEvalAttributes();
        if (evalAttributes == null) {
            return;
        }

        final ScriptingEngine scripting = context.getScriptingEngine();
        for (String name : evalAttributes) {
            final Object expr = attributes.get(name);
            if (expr != null) {
                final String exprstring = expr.toString();
                Object value = scripting.eval(exprstring);
                if (value == null) {
                    value = Context.getUndefinedValue();
                }

                attributes.put(name, value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void validateAttributes()
            throws ErrorEvent {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() {
        Object o;

        try {
            o = super.clone();
        } catch (CloneNotSupportedException cnse) {
            LOGGER.warn("clone failed", cnse);

            o = null;
        }

        return o;
    }

    /**
     * Retrieves the value of the given attribute.
     * @param name Name of the attribute.
     * @return Value of the attribute, <code>null</code> if the attribute
     * has no associated value.
     */
    protected Object getAttribute(final String name) {
        return attributes.get(name);
    }

    /**
     * Checks if the given attribute is defined, this means, neither
     * <code>null</code> nor <code>Context.getUndefinedValue()</code>.
     * @param name Name of the attribute.
     * @return <code>true</code> if the attribute is defined.
     */
    protected boolean isAttributeDefined(final String name) {
        final Object value = attributes.get(name);
        if (value == null) {
            return false;
        }

        return Context.getUndefinedValue() != value;
    }

    /**
     * {@inheritDoc}
     */
    public void dumpNode(final VoiceXmlNode node) {
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
            str.append(value);
            str.append("'");
        }

        LOGGER.debug(str.toString());
    }
}
