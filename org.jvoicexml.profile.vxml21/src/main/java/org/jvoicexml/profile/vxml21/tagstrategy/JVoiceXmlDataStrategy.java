/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectDeserializer;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Data;
import org.jvoicexml.xml.vxml.JVoiceXmlData;

/**
 * Strategy of the FIA to execute a <code>&lt;data&gt;</code> node with
 * extensions from JVoiceXML
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Data
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
final class JVoiceXmlDataStrategy extends DataStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(JVoiceXmlDataStrategy.class);

    /** The MIME type to use when getting the document. */
    private MimeType type;

    /**
     * Constructs a new object.
     */
    JVoiceXmlDataStrategy() {
    }

    /**
     * {@inheritDoc}
     * 
     * Also evaluating the {@link JVoiceXmlData#ATTRIBUTE_JVOICEXML_TYPE}
     * attribute.
     */
    @Override
    public void validateAttributes(DataModel model) throws ErrorEvent {
        super.validateAttributes(model);
        final String mimeType = (String) getAttribute(
                JVoiceXmlData.ATTRIBUTE_JVOICEXML_TYPE);
        if (mimeType == null) {
            type = DocumentDescriptor.MIME_TYPE_XML;
        } else {
            try {
                type = new MimeType(mimeType);
            } catch (MimeTypeParseException e) {
                throw new SemanticError(e.getMessage(), e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final VoiceXmlInterpreterContext context,
            final VoiceXmlInterpreter interpreter,
            final FormInterpretationAlgorithm fia, final FormItem item,
            final VoiceXmlNode node) throws JVoiceXMLEvent {
        final Object object = getDocument(context, type);
        final String name = (String) getAttribute(Data.ATTRIBUTE_NAME);
        if (name == null) {
            LOGGER.info("name of data tat not provided."
                    + " ignoring the retrieved content.");
            return;
        }
        final DataModel model = context.getDataModel();
        final DataModelObjectDeserializer deserializer = model
                .getDeserializer(type);
        if (deserializer == null) {
            throw new BadFetchError("No deserializer known for '" + type + "'");
        }
        final Object deserialized = deserializer.deserialize(model, type,
                object);
        if (model.existsVariable(name)) {
            int rc = model.updateVariable(name, deserialized);
            if (rc != DataModel.NO_ERROR) {
                LOGGER.warn("error updating '" + name + "':" + rc);
                throw new SemanticError("error updating '" + name + "':" + rc);
            }
        } else {
            int rc = model.createVariable(name, deserialized);
            if (rc != DataModel.NO_ERROR) {
                LOGGER.warn("error updating '" + name + "':" + rc);
                throw new SemanticError("error updating '" + name + "':" + rc);
            }
        }
    }

}
