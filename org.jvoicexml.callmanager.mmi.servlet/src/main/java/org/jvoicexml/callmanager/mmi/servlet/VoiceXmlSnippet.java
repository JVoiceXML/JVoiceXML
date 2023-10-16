/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.mmi.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A servlet that creates VoiceXML documents from snippets.
 * @author Dirk Schnelle-Walka
 */
@WebServlet(description = "creation of VoiceXML documents from snippets",
    urlPatterns = { "/VoiceXmlSnippet" })
public class VoiceXmlSnippet extends HttpServlet {
    /** The serial version UID. */
    private static final long serialVersionUID = -1780982925617074243L;

    /** The document builder. */
    private DocumentBuilder builder;

    /** The XSL template. */
    private Templates promptTemplate;
    
    @Override
    public void init() throws ServletException {
        super.init();

        final DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Configure the factory to ignore comments
        factory.setIgnoringComments(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new ServletException(e.getMessage(), e);
        }
        TransformerFactory transFact = TransformerFactory.newInstance();
        try {
            final ServletContext context = getServletContext();
            final URL xsltURL = context.getResource(
                    "/VoiceXmlPromptTemplate.xsl");
            final String xsltSystemID = xsltURL.toExternalForm();
            promptTemplate = transFact.newTemplates(
                    new StreamSource(xsltSystemID));
        } catch (TransformerConfigurationException tce) {
            throw new UnavailableException("Unable to compile stylesheet");
        } catch (MalformedURLException mue) {
            throw new UnavailableException("Unable to locate XSLT file");
        }
    }

    /**
     * Creates a VoiceXML document from the given XML snippet.
     * @param xml the XML snippet
     * @return create VoiceXML document
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     * @throws ParserConfigurationException
     */
    private VoiceXmlDocument createDocument(final String xml)
            throws SAXException, IOException, TransformerException,
                ParserConfigurationException {
        final Reader reader = new StringReader(xml);
        final InputSource source = new InputSource(reader);
        Document document = builder.parse(source);
        Transformer transformer = promptTemplate.newTransformer();
        final Source domSource = new DOMSource(document);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Result result = new StreamResult(out); 
        transformer.transform(domSource, result);
        final InputStream in = new ByteArrayInputStream(out.toByteArray());
        final InputSource transformedSource = new InputSource(in);
        return new VoiceXmlDocument(transformedSource);
    }

    /**
     * Creates a VoiceXML for the given prompt.
     * @param prompt the text for the prompt
     * @return created VoiceXML document
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    private VoiceXmlDocument createDocumentWithPrompt(final String prompt)
            throws ParserConfigurationException, SAXException, IOException,
                TransformerException {
        if (prompt.startsWith("<")) {
            return createDocument(prompt);
        }
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        block.addText(prompt);
        return document;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response)
                    throws ServletException, IOException {
        VoiceXmlDocument document = null;
        final String prompt = request.getParameter("prompt");
        try {
            if (prompt != null) {
                document = createDocumentWithPrompt(prompt);
            } else {
                final String field = request.getParameter("field");
                if (field != null) {
                    document = createDocument(field);
                }
            }
        } catch (ParserConfigurationException | SAXException
                | TransformerException e) {
            throw new IOException(e.getMessage(), e);
        }
        if (document == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "neither parameter 'prompt' nor 'field' found");
            return;
        }
        response.setContentType("text/xml");
        final String xml = document.toXml();
        final ServletOutputStream out = response.getOutputStream();
        out.print(xml);
    }
}
