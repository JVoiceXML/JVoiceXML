/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.processor.srgs/src/org/jvoicexml/processor/srgs/GrammarChecker.java $
 * Version: $LastChangedRevision: 4184 $
 * Date:    $Date: 2014-08-11 09:20:42 +0200 (Mon, 11 Aug 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi;

import java.util.Map;

import org.jvoicexml.event.EventBus;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.profile.vxml21.tagstrategy.JvoiceXmlSsmlParsingStrategyFactory;

/**
 * A profile that supports the VoiceXML 2.1 standards as defined in <a
 * href="http://www.w3.org/TR/voicexml21/">http://www.w3.org/TR/voicexml21/</a>
 * and adds some specific extensions for MMI.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class MmiProfile implements Profile {
    /** Name of the profile. */
    public static final String NAME = "MMI";

    /** The initialization tag strategy factory. */
    private TagStrategyFactory initializationTagStrategyFactory;

    /** The tag strategy factory. */
    private TagStrategyFactory tagStrategyFactory;

    /** The SSML parsing strategy factory. */
    private final SsmlParsingStrategyFactory ssmlParsingStrategyFactory;

    /** Active queues. */
    private final Map<VoiceXmlInterpreterContext, ReceiveEventQueue> queues;

    /**
     * Constructs a new object.
     */
    public MmiProfile() {
        ssmlParsingStrategyFactory = new JvoiceXmlSsmlParsingStrategyFactory();
        queues = new java.util.HashMap<VoiceXmlInterpreterContext,
                    ReceiveEventQueue>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final VoiceXmlInterpreterContext context) {
        final EventBus bus = context.getEventBus();
        final ReceiveEventQueue queue = new ReceiveEventQueue(context);
        synchronized (queues) {
            queues.put(context, queue);
        }
        bus.subscribe("", queue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void terminate(final VoiceXmlInterpreterContext context) {
        synchronized (queues) {
            queues.remove(context);
        }
    }

    /**
     * Retrieves the event queue for the given context.
     * 
     * @param context
     *            the active context
     * @return corresponding event queue.
     */
    public ReceiveEventQueue getEventQueue(
            final VoiceXmlInterpreterContext context) {
        synchronized (queues) {
            return queues.get(context);
        }
    }

    /**
     * Sets the tag strategy factory.
     * 
     * @param factory
     *            the tag strategy factory
     */
    public void setInitializationTagStrategyFactory(
            final TagStrategyFactory factory) {
        initializationTagStrategyFactory = factory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategyFactory getInitializationTagStrategyFactory() {
        return initializationTagStrategyFactory;
    }

    /**
     * Sets the tag strategy factory.
     * 
     * @param factory
     *            the tag strategy factory
     */
    public void setTagStrategyFactory(final TagStrategyFactory factory) {
        tagStrategyFactory = factory;
        if (tagStrategyFactory instanceof MmiTagStrategyFactory) {
            MmiTagStrategyFactory mmiTagStrategyFactory =
                    (MmiTagStrategyFactory) tagStrategyFactory;
            mmiTagStrategyFactory.setProfle(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TagStrategyFactory getTagStrategyFactory() {
        return tagStrategyFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SsmlParsingStrategyFactory getSsmlParsingStrategyFactory() {
        return ssmlParsingStrategyFactory;
    }
}
