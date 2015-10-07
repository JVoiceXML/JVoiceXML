/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2012 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;

/**
 * When a prompt must be chosen, a set of prompts to be queued is chosen
 * according to the following algorithm:
 *
 * <ol>
 * <li>
 * Form an ordered list of prompts consisting of all prompts in the enclosing
 * element in document order.</li>
 * <li>
 * Remove from this list all prompts whose <code>cond</code> evaluates to false
 * after conversion to boolean.</li>
 * <li>
 * Find the <em>correct count</em>: the highest count among the prompt elements
 * still on the list less than or equal to the current count value.</li>
 * <li>
 * Remove from the list all the elements that don't have the
 * <em>correct count</em>.</li>
 * </ol>
 *
 * <p>
 * All elements that remain on the list will be queued for play.
 * </p>
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
final class PromptChooser {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(PromptChooser.class);

    /** The prompt countable item for which prompts should be chosen. */
    private final PromptCountable countable;

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /**
     * Constructs a new <code>PromptChooser</code> for the given
     * <code>FormItem</code>.
     *
     * @param cnt
     *            The countable input item for which prompts should be chosen.
     * @param ctx
     *            The current VoiceXML interpreter context.
     */
    public PromptChooser(final PromptCountable cnt,
            final VoiceXmlInterpreterContext ctx) {
        countable = cnt;
        context = ctx;
    }

    /**
     * Retrieves the list of prompts that will be queued for play.
     *
     * @return Collection of prompts that will be queued for play.
     * @exception SemanticError
     *                Error evaluating the condition.
     */
    public Collection<VoiceXmlNode> collect() throws SemanticError {
        final int count = countable.getPromptCount();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("find all prompts of '" + countable.getName()
                    + "' with count " + count);
        }
        final Collection<VoiceXmlNode> allPrompts = findAllPrompts();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + allPrompts.size() + " prompt(s) in '"
                    + countable.getName() + "'");
        }
        final Collection<VoiceXmlNode> condPrompts = filterCond(allPrompts);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + condPrompts.size()
                    + " prompt(s) after cond evaluation in '"
                    + countable.getName() + "'");
        }
        final int highestCount = findHighestCount(condPrompts, count);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("highest count of " + countable.getName() + "' is "
                    + highestCount + " <= " + count);
        }
        final Collection<VoiceXmlNode> correctCountPrompts = filterCount(
                condPrompts, highestCount);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + correctCountPrompts.size()
                    + " prompt(s) with count " + highestCount + " in '"
                    + countable.getName() + "'");
        }

        return correctCountPrompts;
    }

    /**
     * Form an ordered list of prompts consisting of all prompts in the
     * enclosing element in document order.
     *
     * @return List of prompts.
     */
    private Collection<VoiceXmlNode> findAllPrompts() {
        final Collection<VoiceXmlNode> prompts = new java.util.ArrayList<VoiceXmlNode>();
        final VoiceXmlNode node = countable.getNode();
        final Collection<VoiceXmlNode> children = node.getChildren();
        for (VoiceXmlNode child : children) {
            if (child instanceof Prompt) {
                prompts.add(child);
            } else if (child instanceof Text) {
                prompts.add(child);
            } else if (child instanceof Audio) {
                prompts.add(child);
            } else if (child instanceof Value) {
                prompts.add(child);
            }
        }
        return prompts;
    }

    /**
     * Remove from this list all prompts whose cond evaluates to false after
     * conversion to boolean.
     *
     * @param prompts
     *            Collection of prompts to be filtered.
     * @return list of filtered prompts.
     *
     * @exception SemanticError
     *                Error evaluating the condition.
     */
    private Collection<VoiceXmlNode> filterCond(
            final Collection<VoiceXmlNode> prompts) throws SemanticError {
        final Collection<VoiceXmlNode> filteredPrompts = new java.util.ArrayList<VoiceXmlNode>();
        final DataModel model = context.getDataModel();
        for (VoiceXmlNode node : prompts) {
            if (node instanceof Prompt) {
                final Prompt prompt = (Prompt) node;
                final String cond = prompt.getCond();
                final String unescapedCond = StringEscapeUtils
                        .unescapeXml(cond);
                final boolean result = model.evaluateExpression(unescapedCond,
                        Boolean.class);
                if (result) {
                    filteredPrompts.add(prompt);
                }
            } else {
                filteredPrompts.add(node);
            }
        }
        return filteredPrompts;
    }

    /**
     * Find the <em>correct count</em>: the highest count among the prompt
     * elements still on the list less than or equal to the current count value.
     *
     * @param prompts
     *            Collection of prompts to examine.
     * @param count
     *            The current count value.
     *
     * @return highest count among the prompts.
     */
    private int findHighestCount(final Collection<VoiceXmlNode> prompts,
            final int count) {
        int highestCount = 0;
        for (VoiceXmlNode node : prompts) {
            final int currentCount;
            if (node instanceof Prompt) {
                final Prompt prompt = (Prompt) node;
                currentCount = prompt.getCountAsInt();
            } else {
                currentCount = 1;
            }
            if (currentCount <= count) {
                if (currentCount > highestCount) {
                    highestCount = currentCount;
                }
            }
        }
        return highestCount;
    }

    /**
     * Remove from the list all the elements that don't have the
     * <em>correct count</em>.
     *
     * @param prompts
     *            Collection of prompts to be filtered.
     * @param count
     *            The correct count.
     * @return Collection of prompts with the correct count.
     */
    private Collection<VoiceXmlNode> filterCount(
            final Collection<VoiceXmlNode> prompts, final int count) {
        final Collection<VoiceXmlNode> filteredPrompts = new java.util.ArrayList<VoiceXmlNode>();
        for (VoiceXmlNode node : prompts) {
            final int currentCount;
            if (node instanceof Prompt) {
                final Prompt prompt = (Prompt) node;
                currentCount = prompt.getCountAsInt();
            } else {
                currentCount = 1;
            }
            if (currentCount == count) {
                filteredPrompts.add(node);
            }
        }
        return filteredPrompts;
    }
}
