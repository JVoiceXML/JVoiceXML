/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;

/**
 * Test case for {@link FieldShadowVarContainer}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class TestFieldShadowVarContainer {

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private FieldShadowVarContainer field;

    /**
     * Set up of the test environment.
     * @exception Exception
     *            test failed.
     * @exception SemanticError
     *            test failed.
     */
    @Before
    public void setUp()
            throws Exception, SemanticError {
        scripting = new ScriptingEngine(null);

        final String name = "test$";
        field = scripting.createHostObject(name, FieldShadowVarContainer.class);
    }

    /**
     * Test method for {@link FieldShadowVarContainer#getUtterance()}.
     *
     * @exception SemanticError
     *            test failed.
     */
    @Test
    public void testGetUtterance() throws SemanticError {
        final String utterance1 = "utterance1";

        field.setUtterance(utterance1);
        Assert.assertEquals(utterance1, field.getUtterance());

        Assert.assertEquals(utterance1,
                scripting.eval("test$.utterance;"));
    }
}
