/*
 * File:    $RCSfile: IfStrategy.java,v $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlNodeList;
import org.jvoicexml.xml.vxml.Else;
import org.jvoicexml.xml.vxml.Elseif;
import org.jvoicexml.xml.vxml.If;
import org.w3c.dom.NodeList;

/**
 * Strategy of the FIA to execute an <code>&lt;if&gt;</code> node.
 *
 * <p>
 * This class is also responsible to handle the <code>&lt;else&gt;</code>
 * and <code>&lt;elseif&gt;</code> tags since they can appear only as
 * children of the <code>&lt;if&gt;</code> tag.
 * </p>
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.If
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class IfStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(IfStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(If.ATTRIBUTE_COND);
    }

    /**
     * Constructs a new object.
     */
    IfStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     *
     * Find the child nodes to execute and execute them.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final NodeList list = getListToExecute(context, node);
        if (list == null) {
            LOGGER.info("no condition evaluated to true");

            return;
        }

        fia.executeChildNodes(item, list);
    }

    /**
     * Retrieves a list of follow-on nodes to execute.
     * @param context The current VoiceXML interpreter context.
     * @param node The current node.
     * @return List of nodes to execute.
     * @exception SemanticError
     *            Error evaluating the cond expression.
     */
    private NodeList getListToExecute(final VoiceXmlInterpreterContext context,
                                      final VoiceXmlNode node)
            throws SemanticError {

        final NodeList children = node.getChildNodes();
        if (checkCondition(context, node)) {
            return collect(children, 0);
        }

        for (int i = 0; i < children.getLength(); i++) {
            final VoiceXmlNode child = (VoiceXmlNode) children.item(i);

            final String name = child.getTagName();
            if (Else.TAG_NAME.equalsIgnoreCase(name)
                || Elseif.TAG_NAME.equalsIgnoreCase(name)) {
                if (checkCondition(context, child)) {
                    return collect(children, i + 1);
                }
            }
        }

        return null;
    }

    /**
     * Checks the condition of the given node.
     *
     * @param context The current VoiceXML interpreter context
     * @param node The node with a cond.
     * @return <code>true</code> if the cond expression evaluates to true.
     *
     * @exception SemanticError
     *            Error evaluating the cond expression.
     */
    private boolean checkCondition(final VoiceXmlInterpreterContext context,
                                   final VoiceXmlNode node)
            throws SemanticError {
        final String cond = node.getAttribute(If.ATTRIBUTE_COND);
        if (cond == null) {
            // This holds only for the else cases.
            return true;
        }

        final ScriptingEngine scripting = context.getScriptingEngine();

        final Object result = scripting.eval(cond);
        return Boolean.TRUE.equals(result);
    }

    /**
     * Collects all children, which belong to the current case.
     *
     * @param children Child nodes of the <code>&lt;if&gt;</code> tag.
     * @param start Start number of the first child after the expression.
     *
     * @return List of follow on nodes to execute.
     */
    private NodeList collect(final NodeList children, final int start) {
        final XmlNodeList<VoiceXmlNode> list = new XmlNodeList<VoiceXmlNode>();

        for (int i = start; i < children.getLength(); i++) {
            final VoiceXmlNode node = (VoiceXmlNode) children.item(i);

            final String name = node.getTagName();
            if (Else.TAG_NAME.equalsIgnoreCase(name)
                || Elseif.TAG_NAME.equalsIgnoreCase(name)) {
                break;
            }

            list.add(node);
        }

        return list;
    }
}
