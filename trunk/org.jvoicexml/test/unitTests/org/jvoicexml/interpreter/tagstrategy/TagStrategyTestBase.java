/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.implementation.SynthesizedOutputListener;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.JVoiceXmlSession;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.test.DummyJvoiceXmlCore;
import org.jvoicexml.test.implementation.DummyImplementationPlatform;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Base class for tests of {@link org.jvoicexml.interpreter.TagStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007-2008 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public abstract class TagStrategyTestBase {
    /** The implementation platform. */
    private DummyImplementationPlatform platform;

    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /** The fia. */
    private FormInterpretationAlgorithm fia;

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /**
     * Constructs a new object.
     */
    public TagStrategyTestBase() {
        super();
    }

    /**
     * Retrieves the scripting engine.
     * @return the scripting engine.
     */
    protected final ScriptingEngine getScriptingEngine() {
        return scripting;
    }


    /**
     * Retrieves the interpreter.
     * @return the interpreter.
     */
    protected final VoiceXmlInterpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Retrieves the {@link VoiceXmlInterpreterContext}.
     * @return the voice XML interpreter context.
     */
    protected final VoiceXmlInterpreterContext getContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Before
    public final void baseSetUp() throws Exception {
        platform = new DummyImplementationPlatform();
        final JVoiceXmlCore jvxml = new DummyJvoiceXmlCore();
        final JVoiceXmlSession session = new JVoiceXmlSession(platform, jvxml);
        context = new VoiceXmlInterpreterContext(session);
        interpreter = new VoiceXmlInterpreter(context);

        scripting = context.getScriptingEngine();
    }

    /**
     * Sets the output listener to add once the system output is obtained.
     * @param listener the listener.
     */
    public final void setSystemOutputListener(
            final SynthesizedOutputListener listener) {
        platform.setSystemOutputListener(listener);
    }

    /**
     * Convenience method to creates an empty form.
     * @return the created form.
     */
    protected final Form createForm() {
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        return vxml.appendChild(Form.class);
    }


    /**
     * Convenient method to create a new VoiceXML document.
     * @return the newly created VoiceXML document.
     */
    protected final VoiceXmlDocument createDocument() {
        fia = null;

        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            Assert.fail(pce.getMessage());

            return null;
        }
        return document;
    }

    /**
     * Creates a <code>&lt;block&gt;</code> inside the VoiceXML document.
     *
     * @return Created block.
     */
    protected final Block createBlock() {
        return createBlock(null);
    }

    /**
     * Creates a <code>&lt;block&gt;</code> inside the VoiceXML document.
     *
     * @param doc Optional VoiceXML document.
     *
     * @return Created block.
     */
    protected final Block createBlock(final VoiceXmlDocument doc) {
        final VoiceXmlDocument document;

        if (doc == null) {
            document = createDocument();
        } else {
            document = doc;
        }

        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        createFia(form);

        return form.appendChild(Block.class);
    }

    /**
     * Creates a fia for the given form.
     * @param form the form for which to create a fia.
     */
    protected final void createFia(final Form form) {
        final Dialog executableForm  = new ExecutablePlainForm(form);
        fia = new FormInterpretationAlgorithm(context, interpreter,
                executableForm);
    }

    /**
     * Convenient method to execute the tag strategy.
     * @param node the node.
     * @param strategy the tag strategy.
     * @exception JVoiceXMLEvent
     *            Error executing the strategy.
     */
    protected final void executeTagStrategy(final VoiceXmlNode node,
            final TagStrategy strategy) throws JVoiceXMLEvent {
        if (fia != null) {
            fia.initialize();
        }
        strategy.getAttributes(context, node);
        strategy.evalAttributes(context);
        strategy.validateAttributes();
        strategy.execute(context, interpreter, fia, null, node);
    }
}
