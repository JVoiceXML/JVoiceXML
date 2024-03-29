/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Log;
import org.jvoicexml.xml.vxml.Value;
import org.w3c.dom.NodeList;

/**
 * Strategy of the FIA to execute a <code>&lt;log&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Log
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 */
final class LogStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(LogStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Log.ATTRIBUTE_EXPR);
    }

    /**
     * Constructs a new object.
     */
    LogStrategy() {
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
     * Logs the containing text as a message.
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final NodeList list = node.getChildNodes();
        final StringBuilder outputText = new StringBuilder();

        // get and write the label attribute
        final String label = (String) getAttribute(Log.ATTRIBUTE_LABEL);
        if (label != null) {
            outputText.append(label);
            outputText.append(": ");
        }

        final String expr = (String) getAttribute(Log.ATTRIBUTE_EXPR);
        if (expr != null) {
            outputText.append(expr);
        }

        // process children
        final DataModel model = context.getDataModel();
        for (int i = 0; i < list.getLength(); i++) {
            final VoiceXmlNode current = (VoiceXmlNode) list.item(i);
            if (current instanceof Text) {
                // text node handling
                final Text text = (Text) current;
                final String msg = text.getNodeValue();
                if (msg == null) {
                    LOGGER.warn("ignoring empty log node");
                } else {
                    outputText.append(msg.trim());
                }
            }

            if (current instanceof Value) {
                // value node handling
                final Value value = (Value) current;
                String currentExpr = value.getExpr();
                if (currentExpr != null) {
                    final String unescapedCurrentExpr = StringEscapeUtils
                            .unescapeXml(currentExpr);
                    if (!currentExpr.endsWith(";")) {
                        currentExpr += ";";
                    }
                    final Object eval = model.evaluateExpression(
                            unescapedCurrentExpr, Object.class);
                    final String evalReadable = model.toString(eval);
                    outputText.append(evalReadable);
                }
            }
        }

        // write the eventual tag-value to the class-logger,
        // priority Level.INFO
        if (outputText.length() > 0) {
            LOGGER.info(outputText.toString());
        }
    }
}
