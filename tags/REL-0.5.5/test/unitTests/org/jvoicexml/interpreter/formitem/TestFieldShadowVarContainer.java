/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 */

package org.jvoicexml.interpreter.formitem;

import junit.framework.TestCase;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.scripting.RhinoScriptingEngine;

/**
 * Test case for org.jvoicexml.interpreter.formitem.FieldShadowVarContainer.
 *
 * @see org.jvoicexml.interpreter.formitem.FieldShadowVarContainer
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class TestFieldShadowVarContainer
        extends TestCase {

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private FieldShadowVarContainer field;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp()
            throws Exception {
        super.setUp();

        scripting = new RhinoScriptingEngine(null);

        final String name = "test$";

        try {
            field =
                scripting.createHostObject(name, FieldShadowVarContainer.class);
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown()
            throws Exception {
        super.tearDown();
    }

    /**
     * Test method for
     * 'FieldShadowVarContainer.getUtterance()'.
     *
     * @see FieldShadowVarContainer#getUtterance()
     */
    public void testGetUtterance() {
        final String utterance1 = "utterance1";

        field.setUtterance(utterance1);
        assertEquals(utterance1, field.getUtterance());

        try {
            assertEquals(utterance1,
                         (String) scripting.eval("test$.utterance"));
        } catch (SemanticError ex) {
            fail(ex.getMessage());
        }

    }

}
