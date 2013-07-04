package org.jvoicexml.callmanager.mmi.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * A servlet that creates VoiceXML documents from snippets.
 */
@WebServlet(description = "creation of VoiceXML documents from snippets", urlPatterns = { "/VoiceXmlSnippet" })
public class VoiceXmlSnippet extends HttpServlet {

    /** The serial version UID. */
    private static final long serialVersionUID = 3445970029411929471L;

    private VoiceXmlDocument createDocumentWithPrompt(final String prompt)
            throws ParserConfigurationException {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        block.addText(prompt);
        return document;
    }

    private VoiceXmlDocument createDocumentWithField(final String field)
        throws ParserConfigurationException {
        final VoiceXmlDocument document = new VoiceXmlDocument();
        
        return document;
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
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
                    document = createDocumentWithField(field);
                }
            }
        } catch (ParserConfigurationException e) {
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
