/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/interpreter/tagstrategy/TestJvoiceXmlSsmlParsingStrategyFactory.java $
 * Version: $LastChangedRevision: 4318 $
 * Date:    $Date: 2014-10-16 13:32:35 +0200 (Thu, 16 Oct 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.Profile;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.SsmlParsingStrategy;
import org.jvoicexml.interpreter.SsmlParsingStrategyFactory;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.mock.MockJvoiceXmlCore;
import org.jvoicexml.mock.MockProfile;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Test case for {@link JvoiceXmlSsmlParsingStrategyFactory}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4318 $
 * @since 0.7
 */

public final class TestJvoiceXmlSsmlParsingStrategyFactory {
    /** The test VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /**
     * {@inheritDoc}
     */
    @Before
    public void setUp() throws Exception {
        final ImplementationPlatform platform =
            new MockImplementationPlatform();
        final JVoiceXmlCore core = new MockJvoiceXmlCore();
        final Profile profile = new MockProfile();
        final JVoiceXmlSession session =
            new JVoiceXmlSession(platform, core, null, profile);
        context = new VoiceXmlInterpreterContext(session, null);
        scripting = context.getScriptingEngine();
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.tagstrategy.JvoiceXmlSsmlParsingStrategyFactory#getParsingStrategy(org.jvoicexml.xml.VoiceXmlNode)}.
     * @exception Exception
     *            test failed.
     * @exception JVoiceXMLEvent
     *            test failed
     */
    @Test
    public void testGetParsingStrategy() throws Exception, JVoiceXMLEvent {
        final String testVar = "testvalue";
        final String testValue = "hurz";
        scripting.setVariable(testVar, testValue);

        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();

        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        final Prompt prompt = block.appendChild(Prompt.class);
        prompt.addText("This is a test");
        final Value value = prompt.appendChild(Value.class);
        value.setExpr(testVar);
        SsmlParsingStrategyFactory factory =
            new JvoiceXmlSsmlParsingStrategyFactory();
        SsmlParsingStrategy strategy = factory.getParsingStrategy(value);
        Assert.assertNotNull(strategy);
        SsmlParsingStrategy promptStrategy = factory.getParsingStrategy(prompt);
        Assert.assertNull(promptStrategy);
    }

}
