/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

import junit.framework.Assert;

import org.junit.Test;
import org.jvoicexml.SpeakablePlainText;
import org.jvoicexml.SpeakableText;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.implementation.OutputStartedEvent;
import org.jvoicexml.implementation.SynthesizedOutputEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Value;

/**
 * This class provides a test case for the {@link ValueStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.6
 */
public final class TestValueStrategy extends TagStrategyTestBase
    implements SynthesizedOutputListener {
    /** The queued speakable. */
    private SpeakableText queuedSpeakable;

    /**
     * Test method for {@link ValueStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test(expected = SemanticError.class)
    public void testExecuteUndefined() throws Exception, JVoiceXMLEvent {
        final String name = "test";
        final Block block = createBlock();
        final Value value = block.appendChild(Value.class);
        value.setExpr(name);

        final ValueStrategy strategy = new ValueStrategy();
        executeTagStrategy(value, strategy);
    }

    /**
     * Test method for {@link VarStrategy#execute(org.jvoicexml.interpreter.VoiceXmlInterpreterContext, org.jvoicexml.interpreter.VoiceXmlInterpreter, org.jvoicexml.interpreter.FormInterpretationAlgorithm, org.jvoicexml.interpreter.FormItem, org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            Test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testExecuteExpr() throws Exception, JVoiceXMLEvent {
        final String name = "test";
        final String val = "hurz";
        final ScriptingEngine scripting = getScriptingEngine();
        scripting.setVariable(name, val);
        final Block block = createBlock();
        final Value value = block.appendChild(Value.class);
        value.setExpr(name);

        setSystemOutputListener(this);
        final ValueStrategy strategy = new ValueStrategy();
        executeTagStrategy(value, strategy);
        final SpeakableText speakable = new SpeakablePlainText(val);
        Assert.assertEquals(speakable, queuedSpeakable);
    }

    /**
     * Test method for {@link ValueStrategy#clone()}.
     * @throws JVoiceXMLEvent
     *         test failed
     * @exception Exception
     *         test failed
     */
    @Test
    public void testClone() throws JVoiceXMLEvent, Exception {
        final ScriptingEngine scripting = getScriptingEngine();
        final String name = "test";
        final String val = "hurz";
        scripting.setVariable(name, val);
        final Block block = createBlock();
        final Value value = block.appendChild(Value.class);
        value.setExpr(name);

        final ValueStrategy strategy = new ValueStrategy();
        final ValueStrategy clone1 = (ValueStrategy) strategy.clone();
        Assert.assertNull(strategy.getAttribute(Value.ATTRIBUTE_EXPR));
        Assert.assertNull(clone1.getAttribute(Value.ATTRIBUTE_EXPR));

        executeTagStrategy(value, strategy);

        final ValueStrategy clone2 = (ValueStrategy) strategy.clone();
        Assert.assertEquals(val, strategy.getAttribute(Value.ATTRIBUTE_EXPR));
        Assert.assertEquals(val,
                clone2.getAttribute(Value.ATTRIBUTE_EXPR));
        Assert.assertNull(clone1.getAttribute(Value.ATTRIBUTE_EXPR));
    }

    /**
     * {@inheritDoc}
     */
    public void outputStatusChanged(final SynthesizedOutputEvent event) {
        final int id = event.getEvent();
        if (id == SynthesizedOutputEvent.OUTPUT_STARTED) {
            final OutputStartedEvent started = (OutputStartedEvent) event;
            queuedSpeakable = started.getSpeakable();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void outputError(final ErrorEvent error) {
    }
}
