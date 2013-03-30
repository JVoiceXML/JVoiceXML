/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/AssignStrategy.java $
 * Version: $LastChangedRevision: 2715 $
 * Date:    $LastChangedDate: 2011-06-21 12:23:54 -0500 (mar, 21 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Assign;

/**
 * Strategy of the FIA to execute an <code>&lt;assign&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Assign
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2715 $
 */
final class AssignStrategy
        extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(AssignStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Assign.ATTRIBUTE_EXPR);
    }

    /** Name of the variable. */
    private String name;

    /** Value to be assigned to value. */
    private Object expr;

    /**
     * Constructs a new object.
     */
    AssignStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes()
            throws ErrorEvent {
        name = (String) getAttribute(Assign.ATTRIBUTE_NAME);
        if (name == null) {
            throw new SemanticError("No name given to assign a value!");
        }

        expr = getAttribute(Assign.ATTRIBUTE_EXPR);
    }

    /**
     * {@inheritDoc}
     *
     * Assigns the values to the variable.
     */
    public void execute(final VoiceXmlInterpreterContext context,
                        final VoiceXmlInterpreter interpreter,
                        final FormInterpretationAlgorithm fia,
                        final FormItem item,
                        final VoiceXmlNode node)
            throws JVoiceXMLEvent {
        final ScriptingEngine scripting = context.getScriptingEngine();
        if (expr instanceof String) {
            scripting.eval(name + " = '" + expr + "';");
        } else {
            scripting.eval(name + " = " + expr + ";");
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("assigned var '" + name + "' to value: '" + expr
                         + "'");
        }
    }
}
