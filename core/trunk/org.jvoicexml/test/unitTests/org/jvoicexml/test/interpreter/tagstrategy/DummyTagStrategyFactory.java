/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/interpreter/tagstrategy/DummyInitializationTagStrategyFactory.java $
 * Version: $LastChangedRevision: 2404 $
 * Date:    $Date: 2010-12-06 10:29:10 +0100 (Mo, 06 Dez 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-12-06 10:29:10 +0100 (Mo, 06 Dez 2010) $, Dirk Schnelle-Walka, project lead
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.test.interpreter.tagstrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;

/**
 * Dummy {@link TagStrategyFactory} for test purposes.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2404 $
 * @since 0.7.4
 */
public class DummyTagStrategyFactory
    implements TagStrategyFactory {
    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the <code>ForminterpreteationAlgorithm</code>.
     */
    private final Map<String, TagStrategy> strategies;

    /**
     * Creates a new object.
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
    public DummyTagStrategyFactory() 
        throws InstantiationException, IllegalAccessException,
        ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        strategies = new java.util.HashMap<String, TagStrategy>();
        strategies.put("assign",
         loadStrategy("org.jvoicexml.interpreter.tagstrategy.AssignStrategy"));
        strategies.put("audio",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.AudioTagStrategy"));
        strategies.put("clear",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ClearStrategy"));
        strategies.put("data",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.DataStrategy"));
        strategies.put("disconnect",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.DisconnectStrategy"));
        strategies.put("exit",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ExitStrategy"));
        strategies.put("goto",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.GotoStrategy"));
        strategies.put("if",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.IfStrategy"));
        strategies.put("log",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.LogStrategy"));
        strategies.put("prompt",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.PromptStrategy"));
        strategies.put("reprompt",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.RepromptStrategy"));
        strategies.put("return",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ReturnStrategy"));
        strategies.put("script",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ScriptStrategy"));
        strategies.put("submit",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.SubmitStrategy"));
        strategies.put("#text",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.TextStrategy"));
        strategies.put("throw",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ThrowStrategy"));
        strategies.put("submit",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.SubmitStrategy"));
        strategies.put("value",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ValueStrategy"));
        strategies.put("valvarue",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.VarStrategy"));
    }

    /**
     * Loads the specified tag strategy.
     * @param name name of the class to load
     * @return loaded tag strategy
     * @throws InstantiationException
     *         unable to create the tag strategy
     * @throws IllegalAccessException
     *         unable to create the tag strategy
     * @throws ClassNotFoundException
     *         unable to create the tag strategy
     * @throws NoSuchMethodException 
     *         unable to create the tag strategy
     * @throws SecurityException 
     *         unable to create the tag strategy
     * @throws InvocationTargetException 
     *         unable to create the tag strategy
     * @throws IllegalArgumentException 
     *         unable to create the tag strategy
     */
    private TagStrategy loadStrategy(final String name)
        throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        final Class<?> clazz = Class.forName(name);
        @SuppressWarnings("rawtypes")
        final Constructor constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (TagStrategy) constructor.newInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategy getTagStrategy(final Node node) {
        if (node == null) {
            return null;
        }

        final String tagName = node.getNodeName();
        return getTagStrategy(tagName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategy getTagStrategy(final String tag) {
        if (tag == null) {
            return null;
        }
        final TagStrategy strategy = strategies.get(tag);
        if (strategy == null) {
            return null;
        }

        return strategy.newInstance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getTagNamespace() throws URISyntaxException {
        return new URI(Vxml.DEFAULT_XMLNS);
    }

}
