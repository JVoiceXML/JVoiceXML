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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Var;

/**
 * Strategy of the FIA to execute the <code>&lt;var&gt;</code> node within a
 * block.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Var
 *
 * @author Torben Hardt
 * @author Dirk Schnelle
 */
final class VarStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager.getLogger(VarStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Var.ATTRIBUTE_EXPR);
    }

    /** Flag indicating that we are in a subdialog context. */
    private boolean isSubdialog;

    /** Name of the variable. */
    private String name;

    /** Initial value of the variable. */
    private Object value;

    /**
     * Constructs a new object.
     */
    VarStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc} Does nothing if called in a subdialog context.
     */
    @Override
    public void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError {
        isSubdialog = context.isInitializingSubdialog();
        if (!isSubdialog) {
            super.evalAttributes(context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws SemanticError {
        name = (String) getAttribute(Var.ATTRIBUTE_NAME);

        /**
         * @todo Check if the name specifies a scope prefix and throw an
         *       error.semantic in that case. Check if this is handled by the
         *       scripting environment.
         */
        if (!isSubdialog) {
            value = getAttribute(Var.ATTRIBUTE_EXPR);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Create the variable with the given value.
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        if (isSubdialog) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ignoring expr of name '" + name
                        + "' in a subdialog");
            }
            return;
        }
        if (name == null) {
            // The VXML specification leaves it undefined, what happens, if
            // no name is given. So we simply ignore it.
            LOGGER.warn("ignoring emtpy var");
            return;
        }

        final DataModel model = context.getDataModel();
        if (value == null) {
            model.createVariable(name);
        } else {
            model.createVariable(name, value);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("created var name '" + name + "': '" + value + "'");
        }
    }
}
