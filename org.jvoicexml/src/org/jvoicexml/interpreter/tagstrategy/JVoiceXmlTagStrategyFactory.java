/*
 * File:    $RCSfile: JVoiceXmlTagStrategyFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
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

package org.jvoicexml.interpreter.tagstrategy;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.TagStrategyFactory;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.vxml.Assign;
import org.jvoicexml.xml.vxml.Clear;
import org.jvoicexml.xml.vxml.Disconnect;
import org.jvoicexml.xml.vxml.Exit;
import org.jvoicexml.xml.vxml.Goto;
import org.jvoicexml.xml.vxml.If;
import org.jvoicexml.xml.vxml.Log;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Reprompt;
import org.jvoicexml.xml.vxml.Return;
import org.jvoicexml.xml.vxml.Script;
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.Throw;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.Var;

/**
 * Factory for tag strategies.
 *
 * @see org.jvoicexml.interpreter.TagStrategy
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class JVoiceXmlTagStrategyFactory
        implements TagStrategyFactory {
    /** Logger for this class. */
    private static final Logger LOGGER =
            Logger.getLogger(JVoiceXmlTagStrategyFactory.class);

    /**
     * Known strategies. The known strategies are templates for the strategy to
     * be executed by the <code>ForminterpreteationAlgorithm</code>.
     */
    private static final Map<String, TagStrategy> STRATEGIES;

    static {
        STRATEGIES = new java.util.HashMap<String, TagStrategy>();

        STRATEGIES.put(Assign.TAG_NAME, new AssignStrategy());
        STRATEGIES.put(Audio.TAG_NAME, new AudioTagStrategy());
        STRATEGIES.put(Clear.TAG_NAME, new ClearStrategy());
        STRATEGIES.put(Disconnect.TAG_NAME, new DisconnectStrategy());
        STRATEGIES.put(Exit.TAG_NAME, new ExitStrategy());
        STRATEGIES.put(Goto.TAG_NAME, new GotoStrategy());
        STRATEGIES.put(If.TAG_NAME, new IfStrategy());
        STRATEGIES.put(Log.TAG_NAME, new LogStrategy());
        STRATEGIES.put(Prompt.TAG_NAME, new PromptStrategy());
        STRATEGIES.put(Reprompt.TAG_NAME, new RepromptStrategy());
        STRATEGIES.put(Return.TAG_NAME, new ReturnStrategy());
        STRATEGIES.put(Script.TAG_NAME, new ScriptStrategy());
        STRATEGIES.put(Submit.TAG_NAME, new SubmitStrategy());
        STRATEGIES.put(Text.TAG_NAME, new TextStrategy());
        STRATEGIES.put(Throw.TAG_NAME, new ThrowStrategy());
        STRATEGIES.put(Value.TAG_NAME, new ValueStrategy());
        STRATEGIES.put(Var.TAG_NAME, new VarStrategy());
    }

    /**
     * Construct a new object.
     */
    public JVoiceXmlTagStrategyFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy getTagStrategy(final VoiceXmlNode node) {
        if (node == null) {
            LOGGER.warn("cannot get strategy for null");

            return null;
        }

        final String tagName = node.getTagName();

        return getTagStrategy(tagName);
    }

    /**
     * {@inheritDoc}
     */
    public TagStrategy getTagStrategy(final String tag) {
        if (tag == null) {
            LOGGER.warn("cannot get strategy for null");

            return null;
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting strategy for tag: '" + tag + "'");
        }

        final TagStrategy strategy = STRATEGIES.get(tag);
        if (strategy == null) {
            LOGGER.warn("no suitable strategy for tag: '" + tag + "'");

            return null;
        }

        return strategy.newInstance();
    }
}
