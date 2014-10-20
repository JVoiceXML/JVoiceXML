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

import org.jvoicexml.Profile;
import org.jvoicexml.interpreter.SsmlParsingStrategyFactory;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.profile.vxml21.tagstrategy.JvoiceXmlSsmlParsingStrategyFactory;

/**
 * A profile that supports the VoiceXML 2.1 standards as defined in <a
 * href="http://www.w3.org/TR/voicexml21/">http://www.w3.org/TR/voicexml21/</a>
 * and adds some specific extensions for MMI.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4184 $
 * @since 0.7.7
 */
public class MmiProfile implements Profile {
    /** Name of th eprofile. */
    public final static String NAME = "MMI";
            
    /** The initialization tag strategy factory. */
    private TagStrategyFactory initializationTagStrategyFactory;

    /** The tag strategy factory. */
    private TagStrategyFactory tagStrategyFactory;

    /** The SSML parsing strategy factory. */
    private final SsmlParsingStrategyFactory ssmlParsingStrategyFactory;

    /**
     * Constructs a new object.
     */
    public MmiProfile() {
        ssmlParsingStrategyFactory = new JvoiceXmlSsmlParsingStrategyFactory();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
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
