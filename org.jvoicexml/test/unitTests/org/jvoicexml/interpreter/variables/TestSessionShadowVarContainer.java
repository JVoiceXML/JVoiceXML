/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
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
package org.jvoicexml.interpreter.variables;

import java.net.URI;

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
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 */
public final class TestSessionShadowVarContainer {
    /** The scripting engine. */
    private ScriptingEngine scripting;

    /** The test object. */
    private SessionShadowVarContainer session;

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

        session = scripting.createHostObject(
                SessionShadowVarContainer.VARIABLE_NAME,
                SessionShadowVarContainer.class);
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setRemoteCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         test failed
     */
    @Test
    public void testSetRemoteCallerDevice() throws Exception, SemanticError {
        final URI uri = new URI("sip:jvoicexml@127.0.0.1:4242");
        session.setRemoteCallerDevice(uri);
        Assert.assertEquals(uri.toString(),
                scripting.eval("session.connection.remote.uri"));
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setRemoteCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         test failed
     */
    @Test
    public void testSetRemoteCallerDeviceNull()
        throws Exception, SemanticError {
        session.setRemoteCallerDevice(null);
        Assert.assertNull(scripting.eval("session.connection.remote.uri"));
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setRemoteCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         expected behavior
     */
    @Test(expected = SemanticError.class)
    public void testSetRemoteCallerDeviceNotSet()
        throws Exception, SemanticError {
        scripting.eval("session.connection.remote.uri");
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setLocalCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         test failed
     */
    @Test
    public void testSetLocalCallerDevice() throws Exception, SemanticError {
        final URI uri = new URI("sip:jvoicexml@127.0.0.1:4242");
        session.setLocalCallerDevice(uri);
        Assert.assertEquals(uri.toString(),
                scripting.eval("session.connection.local.uri"));
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setLocalCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         test failed
     */
    @Test
    public void testSetLocalCallerDeviceNull() throws Exception, SemanticError {
        session.setLocalCallerDevice(null);
        Assert.assertNull(scripting.eval("session.connection.local.uri"));
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#setLocalCallerDevice(java.net.URI)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         expected behavior
     */
    @Test(expected = SemanticError.class)
    public void testSetLocalCallerDeviceNotSet()
        throws Exception, SemanticError {
        scripting.eval("session.connection.local.uri");
    }

    /**
     * Test method for
     * {@link SessionShadowVarContainer#protocol(String, String)}.
     * @throws Exception
     *         test failed.
     * @throws SemanticError
     *         test failed
     */
    @Test
    public void testSetProtocol() throws Exception, SemanticError {
        final String name = "TCP/IP";
        final String version = "1.0";
        session.protocol(name, version);
        Assert.assertEquals(name, scripting.eval("session.protocol.name"));
        Assert.assertEquals(version,
                scripting.eval("session.protocol.version"));
    }

    /**
     * Test method for
     * {@link org.jvoicexml.interpreter.variables.SessionShadowVarContainer}.
     * @exception SemanticError
     *            test failed.
     */
    @Test
    public void testSessionVar() throws SemanticError {
        final String val = "horst";
        scripting.setVariable("test", val);
        Assert.assertEquals(val, scripting.eval("session.test"));
        observer.enterScope(Scope.DOCUMENT);
        scripting.setVariable("test2", "hans");
        Assert.assertEquals("hans", scripting.eval("session.test2"));
        Assert.assertEquals(val, scripting.eval("session.test"));
        Assert.assertNull(scripting.eval("session.test3"));
        observer.exitScope(Scope.DOCUMENT);
        Assert.assertEquals(val, scripting.eval("session.test"));
        scripting.eval("session.test = 'hugo'");
        Assert.assertEquals("hugo", scripting.eval("session.test"));
        scripting.setVariable("test", "dirk");
        Assert.assertEquals("dirk", scripting.eval("session.test"));
        scripting.setVariable("session.test", "piri");
        Assert.assertEquals("piri", scripting.eval("session.test"));
    }
}
