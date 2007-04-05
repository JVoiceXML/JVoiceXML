/*
 * File:    $HeadURL: $
 * Version: $LastChangedRevision:  $
 * Date:    $Date: $
 * Author:  $LastChangedBy: $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import junit.framework.TestCase;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.TagStrategy;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;

/**
 * Base class for tests of {@link org.jvoicexml.interpreter.TagStrategy}.
 *
 * @author Dirk Schnelle
 * @version $Revision: $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public abstract class TagStrategyTestBase extends TestCase {
    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

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
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        context = new VoiceXmlInterpreterContext(null);
        interpreter = new VoiceXmlInterpreter(context);

        scripting = context.getScriptingEngine();
    }

    /**
     * Convenient method to create a new VoiceXML document.
     * @return the newly created VoiceXML document.
     */
    protected final VoiceXmlDocument createDocument() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            fail(pce.getMessage());

            return null;
        }
        return document;
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
        strategy.getAttributes(context, node);
        strategy.evalAttributes(context);
        strategy.validateAttributes();
        strategy.execute(context, interpreter, null, null, node);
    }
}
