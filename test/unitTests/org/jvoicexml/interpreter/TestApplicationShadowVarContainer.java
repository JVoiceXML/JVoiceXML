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
package org.jvoicexml.interpreter;

import junit.framework.TestCase;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.test.DummyRecognitionResult;

/**
 * Test case for {@link ApplicationShadowVarContainer}.
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
public final class TestApplicationShadowVarContainer
        extends TestCase {
    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private ApplicationShadowVarContainer application;

    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();

        scripting = new ScriptingEngine(null);

        try {
            application = scripting.createHostObject(
                    ApplicationShadowVarContainer.VARIABLE_NAME,
                    ApplicationShadowVarContainer.class);
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.ApplicationShadowVarContainer#getLastresult()}.
     */
    public void testGetLastresult() {
        final DummyRecognitionResult result = new DummyRecognitionResult();
        result.setUtterance("testutterance");
        result.setConfidence(0.7f);
        result.setMode("voice");

        application.setRecognitionResult(result);

        try {
            assertEquals("testutterance", (String) scripting
                    .eval("application.lastresult$[0].utterance"));
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }
    }
}
