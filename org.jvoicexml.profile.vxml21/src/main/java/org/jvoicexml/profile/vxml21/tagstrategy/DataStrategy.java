/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.activation.MimeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.Application;
import org.jvoicexml.DocumentDescriptor;
import org.jvoicexml.DocumentServer;
import org.jvoicexml.FetchAttributes;
import org.jvoicexml.Session;
import org.jvoicexml.SessionIdentifier;
import org.jvoicexml.event.ErrorEvent;
import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.interpreter.FormInterpretationAlgorithm;
import org.jvoicexml.interpreter.FormItem;
import org.jvoicexml.interpreter.VoiceXmlInterpreter;
import org.jvoicexml.interpreter.VoiceXmlInterpreterContext;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.DataModelObjectSerializer;
import org.jvoicexml.interpreter.datamodel.KeyValuePair;
import org.jvoicexml.xml.TimeParser;
import org.jvoicexml.xml.TokenList;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Data;
import org.jvoicexml.xml.vxml.RequestMethod;
import org.w3c.dom.Document;

/**
 * Strategy of the FIA to execute a <code>&lt;data&gt;</code> node.
 *
 * @see org.jvoicexml.interpreter.FormInterpretationAlgorithm
 * @see org.jvoicexml.xml.vxml.Data
 *
 * @author Dirk Schnelle-Walka
 * @since 0.7.1
 */
class DataStrategy extends AbstractTagStrategy {
    /** Logger for this class. */
    private static final Logger LOGGER = LogManager
            .getLogger(DataStrategy.class);

    /** List of attributes to be evaluated by the scripting environment. */
    private static final Collection<String> EVAL_ATTRIBUTES;

    static {
        EVAL_ATTRIBUTES = new java.util.ArrayList<String>();

        EVAL_ATTRIBUTES.add(Data.ATTRIBUTE_SRCEXPR);
    }

    /** URI of the XML document to fetch. */
    private URI src;

    /** List of variables to submit. */
    private TokenList namelist;

    /** The request method to use. */
    private RequestMethod method;

    /**
     * Constructs a new object.
     */
    DataStrategy() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<String> getEvalAttributes() {
        return EVAL_ATTRIBUTES;
    }

    /**
     * Retrieves the request method.
     * 
     * @return the request method
     * @since 0.7.9
     */
    protected RequestMethod getRequestMethod() {
        return method;
    }

    /**
     * Retrieves the src.
     * 
     * @return the src
     * @since 0.7.9
     */
    protected URI getSrc() {
        return src;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateAttributes(final DataModel model) throws ErrorEvent {
        final String names = (String) getAttribute(Data.ATTRIBUTE_NAMELIST);
        namelist = new TokenList(names);
        final String requestMethod = (String) getAttribute(
                Data.ATTRIBUTE_METHOD);
        if (requestMethod == null) {
            method = RequestMethod.GET;
        } else if (RequestMethod.POST.getMethod()
                .equalsIgnoreCase(requestMethod)) {
            method = RequestMethod.POST;
        } else if (RequestMethod.GET.getMethod()
                .equalsIgnoreCase(requestMethod)) {
            method = RequestMethod.GET;
        } else {
            throw new SemanticError("Method must be one of '"
                    + RequestMethod.GET + "' or '" + RequestMethod.POST + "'!");
        }

        final boolean srcDefined = isAttributeDefined(model,
                Data.ATTRIBUTE_SRC);
        final boolean srcexprDefined = isAttributeDefined(model,
                Data.ATTRIBUTE_SRCEXPR);
        if (srcDefined == srcexprDefined) {
            throw new BadFetchError(
                    "Exactly one of 'src' or 'srcexpr' must be specified");
        }
        final String srcAttribute = (String) getAttribute(Data.ATTRIBUTE_SRC);
        if (srcAttribute != null) {
            try {
                src = new URI(srcAttribute);
            } catch (URISyntaxException e) {
                throw new SemanticError(
                        "'" + srcAttribute + "' is no valid uri!");
            }
            return;
        }
        final String srcExprAttribute = (String) getAttribute(
                Data.ATTRIBUTE_SRCEXPR);
        try {
            src = new URI(srcExprAttribute);
        } catch (URISyntaxException e) {
            throw new SemanticError(
                    "'" + srcExprAttribute + "' is no valid uri!");
        }
    }

    /**
     * Retrieves the XML document from the {@link DocumentServer}.
     * 
     * @param context
     *            the interpreter context
     * @param type
     *            the MIME type
     * @return obtained document
     * @throws BadFetchError
     *             error obtaining the document
     * @throws SemanticError
     *             error creating the query to the document server
     * @since 0.7.9
     */
    protected Object getDocument(final VoiceXmlInterpreterContext context,
            final MimeType type) throws BadFetchError, SemanticError {
        final DocumentServer server = context.getDocumentServer();
        final Session session = context.getSession();

        final Application application = context.getApplication();
        final URI uri;
        if (application == null) {
            uri = src;
        } else {
            uri = application.resolve(src);
        }
        LOGGER.info("obtaining data from '" + uri + "'");
        final DocumentDescriptor descriptor = new DocumentDescriptor(uri, type,
                method);
        try {
            appendVariables(context, descriptor);
            final FetchAttributes attributes = getFetchAttributes();
            descriptor.setAttributes(attributes);
            final SessionIdentifier sessionId = session.getSessionId();
            return server.getObject(sessionId, descriptor);
        } catch (BadFetchError e) {
            throw new BadFetchError(
                    "error reading data from '" + uri + "': " + e.getMessage(),
                    e);
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
        final Document document = (Document) getDocument(context,
                DocumentDescriptor.MIME_TYPE_XML);
        final String name = (String) getAttribute(Data.ATTRIBUTE_NAME);
        if (name == null) {
            LOGGER.info("name of data tat not provided."
                    + " ignoring the retrieved content.");
            return;
        }
        final DataModel model = context.getDataModel();
        if (model.existsVariable(name)) {
            int rc = model.updateVariable(name, document);
            if (rc != DataModel.NO_ERROR) {
                LOGGER.warn("error updating '" + name + "':" + rc);
                throw new SemanticError("error updating '" + name + "':" + rc);
            }
        } else {
            int rc = model.createVariable(name, document);
            if (rc != DataModel.NO_ERROR) {
                LOGGER.warn("error updating '" + name + "':" + rc);
                throw new SemanticError("error updating '" + name + "':" + rc);
            }
        }
    }

    /**
     * Expand the variables of the namelist to the descriptor.
     *
     * @param context
     *            the current <code>VoiceXmlInterpreterContext</code>.
     * @param descriptor
     *            the document descriptor where to add the resolved parameters
     * @exception SemanticError
     *                A referenced variable is undefined
     */
    protected void appendVariables(final VoiceXmlInterpreterContext context,
            final DocumentDescriptor descriptor) throws SemanticError {
        final DataModel model = context.getDataModel();
        final DataModelObjectSerializer serializer = model.getSerializer();
        for (String name : namelist) {
            final Object value = model.readVariable(name, Object.class);
            if (value instanceof String) {
                String str = (String) value;
                if (str.startsWith("file:/")) {
                    final File file = new File(str);
                    final KeyValuePair pair = new KeyValuePair(name, file);
                    descriptor.addParameter(pair);
                } else {
                    final Collection<KeyValuePair> pairs = serializer
                            .serialize(model, name, value);
                    descriptor.addParameters(pairs);
                }
            } else {
                final Collection<KeyValuePair> pairs = serializer
                        .serialize(model, name, value);
                descriptor.addParameters(pairs);
            }
        }
    }

    /**
     * Determines the fetch attributes from the current node.
     * 
     * @return fetch attributes to use.
     */
    protected FetchAttributes getFetchAttributes() {
        final FetchAttributes attributes = new FetchAttributes();
        final String fetchHint = (String) getAttribute(
                Data.ATTRIBUTE_FETCHHINT);
        if (fetchHint != null) {
            attributes.setFetchHint(fetchHint);
        }
        final String fetchTimeout = (String) getAttribute(
                Data.ATTRIBUTE_FETCHTIMEOUT);
        if (fetchTimeout != null) {
            final TimeParser parser = new TimeParser(fetchTimeout);
            final long seconds = parser.parse();
            attributes.setFetchTimeout(seconds);
        }
        final String maxage = (String) getAttribute(Data.ATTRIBUTE_MAXAGE);
        if (maxage != null) {
            final TimeParser parser = new TimeParser(maxage);
            final long seconds = parser.parse();
            attributes.setMaxage(seconds);
        }
        final String maxstale = (String) getAttribute(Data.ATTRIBUTE_MAXSTALE);
        if (maxstale != null) {
            final TimeParser parser = new TimeParser(maxstale);
            final long seconds = parser.parse();
            attributes.setMaxstale(seconds);
        }
        return attributes;
    }
}
