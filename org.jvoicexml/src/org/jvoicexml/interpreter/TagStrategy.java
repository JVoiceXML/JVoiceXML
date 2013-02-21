/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to execute a node. This can happen in two cases:
 * <ol>
 * <li>Form initialization</li>
 * <li>Tag execution by the FIA</li>
 * </ol>
 *
 * <p>
 * <b>Form initialization</b><br/>
 * Strategy of the interpreter and the FIA to initialize a form. When the
 * <code>VoiceXmlInterpreter</code> iterates overall VoiceXML
 * tags, and asks a <code>TagStrategyFactory</code> for a strategy
 * how to initialize the current node. If a matching strategy was found, it
 * is executed.
 * </p>
 *
 * <p>
 * <b>Tag execution by the FIA</b><br/>
 * Strategy of the FIA to execute a Node. When the
 * <code>ForminterpretationAlgorithm</code> comes to a VoiceXML tag, it asks
 * a <code>TagStrategyFactory</code> for a strategy how to process the
 * current node. If a matching strategy was found, the strategy is executed.
 * </p>
 *
 * <p>
 * Execution of a <code>TagStrategy</code> comprises the following steps:
 *
 * <ol>
 * <li>
 * {@link #newInstance newInstance}<br>
 * Create a working copy from the template.
 * </li>
 * <li>
 * {@link #getAttributes(VoiceXmlInterpreterContext, VoiceXmlNode)
 * getAttributes}<br>
 * Retrieve the current attributes from the node.
 * </li>
 * <li>
 * {@link #evalAttributes(org.jvoicexml.interpreter.VoiceXmlInterpreterContext)
 * evalAttributes}<br>
 * Evaluate attributes, that need to be evaluated by the current script
 * context
 * </li>
 * <li>
 * {@link #validateAttributes() validateAttributes}<br>
 * Check, if all necessary information are present.
 * </li>
 * <li>
 * {@link #execute(VoiceXmlInterpreterContext, VoiceXmlInterpreter, FormInterpretationAlgorithm, FormItem, VoiceXmlNode)
 * execute}<br>
 * Process the node.
 * </li>
 * </ol>
 * </p>
 *
 * <p>
 * The tags for which a <code>TagStrategy</code> exists are executable
 * content.
 * Executable content refers to a block of procedural logic. Such logic appears
 * in:
 * <ul>
 * <li>The <code>&lt;block&gt;</code> form item.</li>
 * <li>The <code>&lt;filled&gt;</code> actions in <code>form</code>s and
 * <code>InputItem</code>s.</li>
 * <li>
 * Event handlers (<code>&lt;catch&gt;</code>, <code>&lt;help&gt;</code>,
 * et cetera).
 * </li>
 * </ul>
 * Executable elements are executed in document order in their block of
 * procedural logic. If an executable element generates an error, that error is
 * thrown immediately. Subsequent executable elements in that block of
 * procedural logic are not executed.
 * </p>
 *
 * @see org.jvoicexml.interpreter.TagStrategyFactory
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.interpreter.formitem.BlockFormItem
 * @see org.jvoicexml.interpreter.formitem.InputItem
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface TagStrategy {
    /**
     * Factory method to get a new instance of this strategy.
     *
     * @return Strategy that can be used in the FIA.
     * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
     */
    TagStrategy newInstance();

    /**
     * Retrieves the names of all attributes, which have to be evaluated
     * by the scripting environment.
     * @return Names of all attributes to be evaluated, <code>null</code>
     *         if the related node has not attributes to be evaluated.
     * @since 0.3.1
     */
    Collection<String> getEvalAttributes();

    /**
     * Retrieves all attributes specified by the given node or by
     * a <code>&lt;property&gt;</code> tag and stores their
     * values in the working copy of this strategy.
     *
     * @param context The current VoiceXML interpreter context.
     * @param node The node to process.
     * @since 0.3.1
     */
    void getAttributes(final VoiceXmlInterpreterContext context,
                       final VoiceXmlNode node);

    /**
     * Evaluates all attributes which have to be evaluated by the scripting
     * environment.
     * @param context The current VoiceXML interpreter context.
     * @throws SemanticError
     *         Error evaluating a variable.
     * @since 0.3.1
     */
    void evalAttributes(final VoiceXmlInterpreterContext context)
            throws SemanticError;

    /**
     * Validates the attributes of the current node. Check, if all
     * needed attributes are provided.
     *
     * @throws ErrorEvent
     *         Validation failed.
     * @since 0.3.1
     */
    void validateAttributes()
            throws ErrorEvent;

    /**
     * Executes the strategy with the current parameters.
     *
     * @param context
     *        The VoiceXML interpreter context.
     * @param interpreter
     *        The current VoiceXML interpreter.
     * @param fia
     *        The current form interpretation algorithm, maybe <code>null</code>
     *        if there is no current fia.
     * @param item
     *        The current form item,maybe <code>null</code> if there is no
     *        current form item.
     * @param node
     *        The current child node.
     * @throws JVoiceXMLEvent
     *         Error while executing this strategy.
     */
    void execute(final VoiceXmlInterpreterContext context,
                 final VoiceXmlInterpreter interpreter,
                 final FormInterpretationAlgorithm fia, final FormItem item,
                 final VoiceXmlNode node)
            throws JVoiceXMLEvent;

    /**
     * Debugging facility to display the contents of all attributes in the
     * node.
     * @param node The current node.
     *
     * @since 0.4
     */
    void dumpNode(final VoiceXmlNode node);
}
