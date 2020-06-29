/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2020 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.profile.vxml21.tagstrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimeType;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Before;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.ImplementationPlatform;
import org.jvoicexml.Session;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.Dialog;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectDeserializer;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.dialog.ExecutablePlainForm;
import org.jvoicexml.profile.Profile;
import org.jvoicexml.profile.TagStrategy;
import org.jvoicexml.profile.TagStrategyFactory;
import org.jvoicexml.profile.vxml21.VoiceXml21Profile;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.mockito.Mockito;

/**
 * Base class for tests of {@link org.jvoicexml.profile.TagStrategy}.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4318 $
 * @since 0.6
 */
public abstract class TagStrategyTestBase {
    /** The implementation platform. */
    private ImplementationPlatform platform;

    /** The VoiceXML interpreter context. */
    private VoiceXmlInterpreterContext context;

    /** The VoiceXML interpreter. */
    private VoiceXmlInterpreter interpreter;

    /** The fia. */
    private FormInterpretationAlgorithm fia;

    /** The profile. */
    private Profile profile;

    /** The employed data model */
    private DataModel model;

    /**
     * Constructs a new object.
     */
    public TagStrategyTestBase() {
        super();
    }

    /**
     * Retrieves the data model
     * 
     * @return the data mdoel
     */
    protected final DataModel getDataModel() {
        return model;
    }

    /**
     * Retrieves the interpreter.
     * 
     * @return the interpreter.
     */
    protected final VoiceXmlInterpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Retrieves the {@link VoiceXmlInterpreterContext}.
     * 
     * @return the voice XML interpreter context.
     */
    protected final VoiceXmlInterpreterContext getContext() {
        return context;
    }

    /**
     * Retrieves the {@link ImplementationPlatform}.
     * 
     * @return the implementation platform
     */
    protected final ImplementationPlatform getImplementationPlatform() {
        return platform;
    }

    /**
     * {@inheritDoc}
     */
    @Before
    public final void baseSetUp() throws Exception {
        platform = Mockito.mock(ImplementationPlatform.class);
        profile = new VoiceXml21Profile();
        final TagStrategyFactory taginitfactory = Mockito
                .mock(TagStrategyFactory.class);
        final VoiceXml21Profile vxml21Profile = (VoiceXml21Profile) profile;
        vxml21Profile.setInitializationTagStrategyFactory(taginitfactory);        
        final Session session = Mockito.mock(Session.class);
        context = Mockito.mock(VoiceXmlInterpreterContext.class);
        Mockito.when(context.getSession()).thenReturn(session);
        Mockito.when(context.getProfile()).thenReturn(new VoiceXml21Profile());
        Mockito.when(context.getImplementationPlatform()).thenReturn(platform);
        interpreter = new VoiceXmlInterpreter(context);
        model = Mockito.mock(DataModel.class);
        final DataModelObjectSerializer serializer = Mockito
                .mock(DataModelObjectSerializer.class);
        Mockito.when(model.getSerializer()).thenReturn(serializer);
        DataModelObjectDeserializer deserializer = new DataModelObjectDeserializer() {
            @Override
            public MimeType getMimeType() {
                return DocumentDescriptor.MIME_TYPE_JSON;
            }

            @Override
            public Object deserialize(DataModel model, MimeType type,
                    Object object) throws SemanticError {
                return object;
            }
        };
        Mockito.when(model.getDeserializer(Mockito.any()))
                .thenReturn(deserializer);
        Mockito.when(context.getDataModel()).thenReturn(model);
    }

    /**
     * Convenience method to creates an empty form.
     * 
     * @return the created form.
     */
    protected final Form createForm() {
        final VoiceXmlDocument document = createDocument();
        final Vxml vxml = document.getVxml();
        return vxml.appendChild(Form.class);
    }

    /**
     * Convenient method to create a new VoiceXML document.
     * 
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
     * @param doc
     *            Optional VoiceXML document.
     *
     * @return Created block.
     * @exception Exception
     *                error creating the FIA
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
     * 
     * @param form
     *            the form for which to create a fia.
     */
    protected final void createFia(final Form form) {
        final Dialog executableForm = new ExecutablePlainForm();
        executableForm.setNode(form);
        fia = new FormInterpretationAlgorithm(context, interpreter,
                executableForm);
    }

    /**
     * Convenient method to execute the tag strategy.
     * 
     * @param node
     *            the node.
     * @param strategy
     *            the tag strategy.
     * @exception JVoiceXMLEvent
     *                error executing the strategy.
     * @exception Exception
     *                error executing the strategy.
     */
    protected final void executeTagStrategy(final VoiceXmlNode node,
            final TagStrategy strategy) throws JVoiceXMLEvent, Exception {
        if (fia != null) {
            fia.initialize(profile, null);
        }
        strategy.getAttributes(context, fia, node);
        strategy.evalAttributes(context);
        strategy.validateAttributes(model);
        strategy.execute(context, interpreter, fia, null, node);
    }

    /**
     * Reads the specified resource as a string.
     * @param name name of the resource to read
     * @return read resource as a string
     * @throws IOException if the reosurce cold not be found
     * @since 0.7.9
     */
    protected String readResource(final String name) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final InputStream input = TestScriptStrategy.class
                .getResourceAsStream(name);
        final byte[] buffer = new byte[1024];
        int read;
        do {
            read = input.read(buffer);
            if (read > 0) {
                out.write(buffer, 0, read);
            }
        } while (read >= 0);
        return out.toString();
    }


}
