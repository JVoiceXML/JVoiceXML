/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2018 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.profile;

import java.util.Collection;

import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.SsmlNode;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Strategy to evaluate the contents of a VoiceXML tag within a
 * <code>&lt;prompt&gt;</code> tag into an SSML document that can be sent to the
 * speech synthesizer.
 *
 * <p>
 * The main purpose is to evaluate all expressions. Since the task is very
 * similar to evaluating a tag with a {@link TagStrategy} it is advisable to use
 * a single source for evaluation of the tags.
 * </p>
 *
 * <p>
 * Execution of an <code>SsmlParsingStrategy</code> comprises the following
 * steps:
 *
 * <ol>
 * <li>
 * {@link #clone()}<br>
 * Create a working copy from the template.</li>
 * <li>
 * {@link #getAttributes(VoiceXmlInterpreterContext, FormInterpretationAlgorithm, VoiceXmlNode)}
 * <br>
 * Retrieve the current attributes from the node.</li>
 * <li>
 * {@link #evalAttributes(VoiceXmlInterpreterContext)}
 * <br>
 * Evaluate attributes, that need to be evaluated by the current script context
 * </li>
 * <li>
 * {@link #validateAttributes(DataModel)}<br>
 * Check, if all necessary information are present.</li>
 * <li>
 * {@link #cloneNode(SsmlParser, DataModel, SsmlDocument, SsmlNode, VoiceXmlNode)}
 * <br>
 * Clone the node into the target structure.</li>
 * </ol>
 *
 * @author Dirk Schnelle
 * @since 0.6
 */
public interface SsmlParsingStrategy extends Cloneable {
    /**
     * Creates a new instance of this strategy.
     * 
     * @return new instance.
     */
    Object clone();

    /**
     * Retrieves the names of all attributes, which have to be evaluated by the
     * scripting environment.
     * 
     * @return Names of all attributes to be evaluated, <code>null</code> if the
     *         related node has not attributes to be evaluated.
     */
    Collection<String> getEvalAttributes();

    /**
     * Retrieves all attributes specified by the given node or by a
     * <code>&lt;property&gt;</code> tag and stores their values in the working
     * copy of this strategy.
     *
     * @param context
     *            The current VoiceXML interpreter context.
     * @param fia
     *            The current form interpretation algorithm, maybe
     *            <code>null</code> if there is no current fia.
     * @param node
     *            The node to process.
     */
    void getAttributes(VoiceXmlInterpreterContext context,
            FormInterpretationAlgorithm fia, VoiceXmlNode node);

    /**
     * Evaluates all attributes which have to be evaluated by the scripting
     * environment.
     * 
     * @param context
     *            The current VoiceXML interpreter context.
     * @throws SemanticError
     *             Error evaluating a variable.
     */
    void evalAttributes(VoiceXmlInterpreterContext context)
            throws SemanticError;

    /**
     * Validates the attributes of the current node. Check, if all needed
     * attributes are provided.
     *
     * @param model
     *            the employed data model
     * @throws ErrorEvent
     *             Validation failed.
     */
    void validateAttributes(DataModel model) throws ErrorEvent;

    /**
     * Creates a clone of this node in the given document.
     * 
     * @param parser
     *            the SSML parser.
     * @param model
     *            the employed data model scripting expressions.
     * @param document
     *            the SSML target document.
     * @param parent
     *            parent node of the node to clone.
     * @param node
     *            the node to clone.
     * @return cloned node, a value of <code>null</code> may be returned to
     *         indicate that the child nodes of <code>node</code> need no
     *         further processing.
     * @exception SemanticError
     *                Error evaluating the node.
     */
    SsmlNode cloneNode(SsmlParser parser, DataModel model,
            SsmlDocument document, SsmlNode parent,
            VoiceXmlNode node) throws SemanticError;
}
