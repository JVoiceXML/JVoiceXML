/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
import java.util.Map;

import org.jvoicexml.interpreter.InitializationTagStrategyFactory;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Dummy {@link InitializationTagStrategyFactory} for test purposes.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.4
 */
public class DummyInitializationTagStrategyFactory
    implements InitializationTagStrategyFactory {
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
    public DummyInitializationTagStrategyFactory() 
        throws InstantiationException, IllegalAccessException,
        ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
        strategies = new java.util.HashMap<String, TagStrategy>();
        strategies.put("grammar",
         loadStrategy("org.jvoicexml.interpreter.tagstrategy.GrammarStrategy"));
        strategies.put("meta",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.MetaStrategy"));
        strategies.put("property",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.PropertyStrategy"));
        strategies.put("script",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.ScriptStrategy"));
        strategies.put("#text",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.TextStrategy"));
        strategies.put("var",
                loadStrategy("org.jvoicexml.interpreter.tagstrategy.VarStrategy"));
    }

    /**
     * Loads the specified tag strategy.
     * @param name name of the class to laod
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
    public TagStrategy getTagStrategy(final VoiceXmlNode node) {
        if (node == null) {
            return null;
        }

        final String tagName = node.getTagName();
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

}
