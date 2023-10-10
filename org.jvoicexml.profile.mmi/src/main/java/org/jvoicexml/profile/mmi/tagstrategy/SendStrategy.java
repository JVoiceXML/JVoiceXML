/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.EventBus;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.mmi.events.AnyComplexType;
import org.jvoicexml.mmi.events.ExtensionNotification;
import org.jvoicexml.mmi.events.Mmi;
import org.jvoicexml.profile.mmi.OutgoingExtensionNotificationJVoiceXmlEvent;
import org.jvoicexml.profile.vxml21.tagstrategy.AbstractTagStrategy;
import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Strategy to send events, similar to what is defined in the VoiceXML 3
 * standard at <a
 * href="http://www.w3.org/TR/voicexml30/#ExternalCommunicationModule:Send"
 * >http://www.w3.org/TR/voicexml30/#ExternalCommunicationModule:Send</a>.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
final class SendStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(SendStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add("contenttypeexpr");
        EVAL_ATTRIBUTES.add("bodyexpr");
        EVAL_ATTRIBUTES.add("bodyexpr");
        EVAL_ATTRIBUTES.add("targetexpr");
    }

    /** Data to be sent in the body of the message. */
    private Object body;

    /** Name of the event to send. */
    private String event;

    /** URI to which the event is sent. */
    private String target;

    /**
     * Constructs a new object.
     */
    SendStrategy() {
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
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        body = getAttributeWithAlternativeExpr(model, "body", "bodyexpr");
        event = (String) getAttributeWithAlternativeExpr(model, "event",
                "eventexpr");
        target = (String) getAttributeWithAlternativeExpr(model, "target",
                "targetexpr");
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
        // Construct an MMI event
        final Mmi mmi = new Mmi();
        final ExtensionNotification ext = new ExtensionNotification();
        mmi.setExtensionNotification(ext);
        ext.setTarget(target);
        ext.setName(event);
        final AnyComplexType any = new AnyComplexType();
        any.addContent(body);
        ext.setData(any);

        LOGGER.info("sending " + mmi);

        // Deliver it over the event bus
        final OutgoingExtensionNotificationJVoiceXmlEvent jvxmlevent =
                new OutgoingExtensionNotificationJVoiceXmlEvent(
                mmi);
        final EventBus bus = context.getEventBus();
        bus.publish(jvxmlevent);
    }
}
