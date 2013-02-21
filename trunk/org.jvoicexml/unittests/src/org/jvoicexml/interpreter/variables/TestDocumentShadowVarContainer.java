/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.variables;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.ScriptingEngine;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeObserver;

/**
 * Test case for {@link ApplicationShadowVarContainer}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7
 */
public final class TestDocumentShadowVarContainer {
    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private DocumentShadowVarContainer document;

    /** The scope observer. */
    private ScopeObserver observer;

    /**
     * Test setup.
     * @exception Exception
     *            test failed.
     * @exception SemanticError
     *            test failed.
     */
    @Before
    public void setUp() throws Exception, SemanticError {
        observer = new ScopeObserver();
        scripting = new ScriptingEngine(observer);
        observer.enterScope(Scope.APPLICATION);
        scripting.createHostObject(
                ApplicationShadowVarContainer.VARIABLE_NAME,
                ApplicationShadowVarContainer.class);

        observer.enterScope(Scope.DOCUMENT);

        document = scripting.createHostObject(
                DocumentShadowVarContainer.VARIABLE_NAME,
                DocumentShadowVarContainer.class);
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.variables.DocumentShadowVarContainer}.
     * @exception SemanticError
     *            test failed.
     */
    @Test
    public void testDocumentVar() throws SemanticError {
        final String val = "horst";
        scripting.eval("application.test = '" + val + "';");
        Assert.assertEquals(val, scripting.eval("document.test;"));
        observer.enterScope(Scope.DIALOG);
        scripting.setVariable("test2", "hans");
        Assert.assertNull(scripting.eval("document.test2;"));
        Assert.assertEquals(val, scripting.eval("document.test;"));
        Assert.assertEquals(val, scripting.eval("application.test;"));
        Assert.assertTrue(
                (Boolean) scripting.eval("document.test == application.test;"));
        Assert.assertNull(scripting.eval("document.test3;"));
        observer.exitScope(Scope.DIALOG);
        Assert.assertEquals(val, scripting.eval("document.test;"));
    }
}
