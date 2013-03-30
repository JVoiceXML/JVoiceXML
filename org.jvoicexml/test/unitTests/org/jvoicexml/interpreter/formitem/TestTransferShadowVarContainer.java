/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/interpreter/formitem/TestTransferShadowVarContainer.java $
 * Version: $LastChangedRevision: 2715 $
 * Date:    $Date: 2011-06-21 12:23:54 -0500 (mar, 21 jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.formitem;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;

/**
 * Test case for {@link TransferShadowVarContainer}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2715 $
 * @since 0.7
 */
public final class TestTransferShadowVarContainer {

    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private TransferShadowVarContainer transfer;

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
        transfer = scripting.createHostObject(name,
                TransferShadowVarContainer.class);
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.TransferShadowVarContainer#getDuration()}.
     * @exception SemanticError
     *            test failed.
     */
    @Test
    public void testGetDuration() throws SemanticError {
        final long duration = 42;

        transfer.setDuration(duration);
        Assert.assertEquals(duration, transfer.getDuration());

        Assert.assertEquals(new Long(duration),
                scripting.eval("test$.duration;"));
    }

    /**
     * Test method for {@link org.jvoicexml.interpreter.formitem.TransferShadowVarContainer#getInputmode()}.
     * @exception SemanticError
     *            test failed.
     */
    @Test
    public void testGetInputmode() throws SemanticError {
        final String mode = "dtmf";

        transfer.setInputmode(mode);
        Assert.assertEquals(mode, transfer.getInputmode());

        Assert.assertEquals(mode,
                scripting.eval("test$.inputmode;"));
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

        transfer.setUtterance(utterance1);
        Assert.assertEquals(utterance1, transfer.getUtterance());

        Assert.assertEquals(utterance1,
                scripting.eval("test$.utterance;"));
    }
}
