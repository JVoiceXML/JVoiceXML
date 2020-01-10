/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.jvoicexml.Configuration;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.JVoiceXmlCore;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.UuidSessionIdentifier;
import org.jvoicexml.interpreter.dialog.ExecutableMenuForm;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.interpreter.dialog.JVoiceXmlDialogFactory;
import org.jvoicexml.mock.MockJvoiceXmlCore;
import org.jvoicexml.mock.implementation.MockImplementationPlatform;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.SsmlParsingStrategyFactory;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * This class tests the {@link VoiceXmlInterpreter}.
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7
 */
public final class TestVoiceXmlInterpreter {
    /** The object to test. */
    private VoiceXmlInterpreter interpreter;

    /** The configuration to use. */
    private Configuration configuration;

    /**
     * Test setup.
     */
    @Before
    public void setUp() throws Exception {
        configuration = Mockito.mock(Configuration.class);
        JVoiceXmlDialogFactory dialogFactory = new JVoiceXmlDialogFactory();
        dialogFactory.addDialogMapping(Form.TAG_NAME, new ExecutablePlainForm());
        dialogFactory.addDialogMapping(Menu.TAG_NAME, new ExecutableMenuForm());
        Mockito.when(configuration.loadObject(DialogFactory.class))
                .thenReturn(dialogFactory);

        final ImplementationPlatform platform = new MockImplementationPlatform();
        final JVoiceXmlCore jvxml = new MockJvoiceXmlCore();
        final Profile profile = Mockito.mock(Profile.class);
        final SsmlParsingStrategyFactory factory = Mockito
                .mock(SsmlParsingStrategyFactory.class);
        Mockito.when(profile.getSsmlParsingStrategyFactory()).thenReturn(
                factory);

        final SessionIdentifier id = new UuidSessionIdentifier();
        final JVoiceXmlSession session = new JVoiceXmlSession(platform, jvxml,
                null, profile, id);
        final VoiceXmlInterpreterContext context = new VoiceXmlInterpreterContext(
                session, configuration);
        interpreter = new VoiceXmlInterpreter(context);

    }

    /**
     * Test method for {@link VoiceXmlInterpreter#setState(InterpreterState)}.
     */
    @Test
    public void testSetState() {
        interpreter.setState(InterpreterState.TRANSITIONING);
        Assert.assertEquals(InterpreterState.TRANSITIONING,
                interpreter.getState());
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#setDocument(VoiceXmlDocument, String, Configuration)}.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testSetDocument() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final String id = "hurz";
        form.setId(id);
        interpreter.setDocument(document, null, configuration);
        final Dialog dialog = interpreter.getNextDialog();

        Assert.assertEquals(id, dialog.getId());
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#setDocument(VoiceXmlDocument, String, Configuration)} with 2 forms.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testSetDocument2Forms() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form1 = vxml.appendChild(Form.class);
        final String id = "hurz";
        form1.setId(id);
        final Form form2 = vxml.appendChild(Form.class);
        final String id2 = "unwanted";
        form2.setId(id2);

        interpreter.setDocument(document, null, configuration);
        final Dialog dialog = interpreter.getNextDialog();

        Assert.assertEquals(id, dialog.getId());
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#setDocument(VoiceXmlDocument, String, Configuration)} without forms.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testSetDocumentNoForm() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        interpreter.setDocument(document, null, configuration);
        final Dialog dialog = interpreter.getNextDialog();

        Assert.assertNull(dialog);
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#setDocument(VoiceXmlDocument, String, Configuration)} with a null
     * document.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testSetDocumentNull() throws Exception {
        interpreter.setDocument(null, null, configuration);
        final Dialog dialog = interpreter.getNextDialog();

        Assert.assertNull(dialog);
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#getDialog(java.lang.String}.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetDialog() throws Exception {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form1 = vxml.appendChild(Form.class);
        final String id1 = "horst";
        form1.setId(id1);
        final Form form2 = vxml.appendChild(Form.class);
        final String id2 = "hans";
        form2.setId(id2);

        interpreter.setDocument(document, null, configuration);

        final Dialog dialog1 = interpreter.getDialog(id1);
        Assert.assertEquals(id1, dialog1.getId());
        final Dialog dialog2 = interpreter.getDialog(id2);
        Assert.assertEquals(id2, dialog2.getId());
        final Dialog dialog3 = interpreter.getDialog("unknown");
        Assert.assertNull("execpted to find no form", dialog3);
        final Dialog dialog4 = interpreter.getDialog(null);
        Assert.assertNull("execpted to find no form", dialog4);
    }

    /**
     * Test method for {@link VoiceXmlInterpreter#getDialog(java.lang.String)}
     * without a document.
     *
     * @exception Exception
     *                test failed
     */
    @Test
    public void testGetDialogNoDocument() throws Exception {
        final Dialog dialog = interpreter.getDialog("unknown");
        Assert.assertNull("execpted to find no form", dialog);
    }
}
