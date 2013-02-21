/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.interpreter;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.formitem.PromptCountable;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Prompt;
import org.w3c.dom.NodeList;

/**
 * When a prompt must be chosen, a set of prompts to be queued is chosen
 * according to the following algorithm:
 *
 * <ol>
 * <li>
 * Form an ordered list of prompts consisting of all prompts in the
 * enclosing element in document order.
 * </li>
 * <li>
 * Remove from this list all prompts whose cond evaluates to false after
 * conversion to boolean.
 * </li>
 * <li>
 * Find the <em>correct count</em>: the highest count among the prompt elements
 * still on the list less than or equal to the current count value.
 * </li>
 * <li>
 * Remove from the list all the elements that don't have the
 * <em>correct count</em>.
 * </li>
 * </ol>
 *
 * <p>
 * All elements that remain on the list will be queued for play.
 * </p>
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
final class PromptChooser {
    /** Logger for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(PromptChooser.class);

    /** The prompt countable item for which prompts should be chosen. */
    private final PromptCountable countable;

    /** The current VoiceXML interpreter context. */
    private final VoiceXmlInterpreterContext context;

    /**
     * Constructs a new <code>PromptChooser</code> for the given
     * <code>FormItem</code>.
     *
     * @param cnt The countable input item for which prompts should be chosen.
     * @param ctx The current VoiceXML interpreter context.
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
     *            Error evaluating the condition.
     */
    public Collection<Prompt> collect()
            throws SemanticError {
        final int count = countable.getPromptCount();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("find all prompts of '" + countable.getName()
                         + "' with count " + count);
        }

        final Collection<Prompt> allPrompts = findAllPrompts();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("found " + allPrompts.size() + " prompt(s) in '"
                         + countable.getName() + "'");
        }

        final Collection<Prompt> condPrompts = filterCond(allPrompts);
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

        final Collection<Prompt> correctCountPrompts =
                filterCount(condPrompts, highestCount);
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
    private Collection<Prompt> findAllPrompts() {
        final Collection<Prompt> prompts = new java.util.ArrayList<Prompt>();

        final VoiceXmlNode node = countable.getNode();
        final NodeList children = node.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            final VoiceXmlNode child = (VoiceXmlNode) children.item(i);

            if (child instanceof Prompt) {
                final Prompt prompt = (Prompt) child;
                prompts.add(prompt);
            }
        }

        return prompts;
    }

    /**
     * Remove from this list all prompts whose cond evaluates to false after
     * conversion to boolean.
     *
     * @param prompts Collection of prompts to be filered.
     * @return List of filtered prompts.
     *
     * @exception SemanticError
     *            Error evaluating the condition.
     */
    private Collection<Prompt> filterCond(final Collection<Prompt> prompts)
            throws SemanticError {
        final Collection<Prompt> filteredPrompts =
                new java.util.ArrayList<Prompt>();
        final ScriptingEngine scripting = context.getScriptingEngine();
        for (Prompt prompt : prompts) {
            final String cond = prompt.getCond();

            final Object result = scripting.eval(cond);
            if (Boolean.TRUE.equals(result)) {
                filteredPrompts.add(prompt);
            }
        }

        return filteredPrompts;
    }

    /**
     * Find the <em>correct count</em>: the highest count among the prompt
     * elements still on the list less than or equal to the current count value.
     *
     * @param prompts Collection of prompts to examine.
     * @param count The current count value.
     *
     * @return highest count among the prompts.
     */
    private int findHighestCount(final Collection<Prompt> prompts,
                                 final int count) {
        int highestCount = 0;

        for (Prompt prompt : prompts) {
            final int currentCount = prompt.getCountAsInt();
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
     * @param prompts Collection of prompts to be filtered.
     * @param count The correct count.
     * @return Collection of prompts with the correct count.
     */
    private Collection<Prompt> filterCount(final Collection<Prompt> prompts,
                                           final int count) {
        final Collection<Prompt> filteredPrompts =
                new java.util.ArrayList<Prompt>();

        for (Prompt prompt : prompts) {
            final int currentCount = prompt.getCountAsInt();
            if (currentCount == count) {
                filteredPrompts.add(prompt);
            }
        }

        return filteredPrompts;
    }
}
