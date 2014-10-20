/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/AssignStrategy.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $LastChangedDate: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.mmi.tagstrategy;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.profile.mmi.LastMessage;
import org.jvoicexml.profile.mmi.MmiProfile;
import org.jvoicexml.profile.mmi.ProfileAwareTagStrategy;
import org.jvoicexml.profile.mmi.ReceiveEventQueue;
import org.jvoicexml.profile.vxml21.tagstrategy.AbstractTagStrategy;
import org.jvoicexml.xml.TimeParser;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to send events, similar to what is defined in the VoiceXML 3
 * standard at <a
 * href="http://www.w3.org/TR/voicexml30/#ExternalCommunicationModule:Send"
 * >http://www.w3.org/TR/voicexml30/#ExternalCommunicationModule:Send</a>.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.7
 */
final class ReceiveStrategy extends AbstractTagStrategy
        implements ProfileAwareTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(ReceiveStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add("maxtimeexpr");
    }

    /** The profile. */
    private MmiProfile profile;

    /** URI to which the event is sent. */
    private long maxtime;

    /**
     * Constructs a new object.
     */
    ReceiveStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProfile(final MmiProfile value) {
        profile = value;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes() throws ErrorEvent {
        if (isAttributeDefined("maxtime") || isAttributeDefined("maxtimeexpr")) {
            final Object value = getAttributeWithAlternativeExpr("maxtime",
                    "maxtimeexpr");
            final TimeParser parser = new TimeParser(value.toString());
            maxtime = parser.parse();
        } else {
            maxtime = -1;
        }
    }

    /**
     * {@inheritDoc}
     *
     * Assigns the values to the variable.
     */
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final ReceiveEventQueue queue = profile.getEventQueue(context);
        final LastMessage message = queue.getNextLastMessage(maxtime);
        if (message == null) {
            throw new BadFetchError("receive: no message available after "
                    + maxtime + " msec!");
        }
        final ScriptingEngine scripting = context.getScriptingEngine();
        scripting.setVariable("application.lastmessage$", message);
    }
}
