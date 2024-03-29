/*
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
package org.jvoicexml.profile;

import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;

/**
 * A profile follows the thought of the profiles as they are specified in <a
 * href ="http://www.w3.org/TR/voicexml30/#Profiles">http://www.w3.org/TR/
 * voicexml30 /#Profiles</a>. It enables users to tailor the behavior of tags
 * and their execution. However, the concept of modules is not known to
 * JVoiceXML, yet.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public interface Profile {
    /**
     * Retrieves the name of this profile.
     * 
     * @return name of this profile.
     */
    String getName();

    /**
     * Initialized this profile once a session is created.
     * <p>
     * Note, that profiles are shared among all sessions and that any properties
     * may be overwritten by sessions that are created in parallel.
     * </p>
     * 
     * @param context
     *            the context for the created session.
     */
    void initialize(VoiceXmlInterpreterContext context);

    /**
     * Initialized this profile once a session is closed.
     * <p>
     * Note, that profiles are shared among all sessions and that any properties
     * may be overwritten by sessions that are created in parallel.
     * </p>
     * 
     * @param context
     *            the context for the closed session.
     */
    void terminate(VoiceXmlInterpreterContext context);

    /**
     * Retrieves the tag strategy factory Factory for {@link TagStrategy}s that
     * are to be executed before executing the form items.
     * 
     * @return the initialization tag strategy factory.
     */
    TagStrategyFactory getInitializationTagStrategyFactory();

    /**
     * Retrieves the tag strategy factory.
     * 
     * @return the tag strategy factory.
     */
    TagStrategyFactory getTagStrategyFactory();

    /**
     * Retrieves the parsing strategy to parse nodes into SSML document.
     * 
     * @return the parsing strategy
     */
    SsmlParsingStrategyFactory getSsmlParsingStrategyFactory();
}
