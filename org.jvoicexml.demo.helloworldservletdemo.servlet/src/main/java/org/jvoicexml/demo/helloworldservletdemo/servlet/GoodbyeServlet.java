/*
 * JVoiceXML Demo - Demo for the free VoiceXML implementation JVoiceXML
 *
 * Copyright (C) 2005-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */

package org.jvoicexml.demo.helloworldservletdemo.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Servlet that generates a 'goodbye' VoiceXML document.
 *
 * @author Dirk Schnelle-Walka
 */
@WebServlet(displayName="Goodbye", description="VoiceXML Goodbye Demo",
    urlPatterns="/Goodbye")
public final class GoodbyeServlet
        extends HttpServlet {
    /** The serial version UID. */
    private static final long serialVersionUID = 657492536387249327L;

    /** Logger for this class. */
    private static final Logger LOGGER =
            LogManager.getLogger(GoodbyeServlet.class);

    /**
     * Construct a new object.
     */
    public GoodbyeServlet() {
    }

    /**
     * Create a simple VoiceXML document containing the obtained message.
     *
     * @param message
     *        The message to prompt.
     * @return Created VoiceXML document, <code>null</code> if an error
     *         occurs.
     */
    private VoiceXmlDocument createResponse(final String message) {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);
        final Block block = form.appendChild(Block.class);
        block.addText(message);

        return document;
    }

    /**
     * Retrieves a parameter from the hello world servlet and echoes
     * this in a new VoiceXML document.
     *
     * @param request
     *        HttpServletRequest object that contains the request the client has
     *        made of the servlet
     * @param response
     *        HttpServletResponse object that contains the response the servlet
     *        sends to the client
     * @throws ServletException
     *         If the request for the GET could not be handled.
     * @throws IOException
     *         If an input or output error is detected when the servlet handles
     *         the GET request.
     */
    @Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response)
            throws ServletException,
            IOException {
        response.setContentType("text/xml");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating goodbye VoiceXML document...");
        }

        final String message = request.getParameter("message") == null ? "Goodbye!" : request.getParameter("message");

        final VoiceXmlDocument document = createResponse(message);
        final String xml = document.toXml();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("returning document");
            LOGGER.debug(xml);
        }

        final PrintWriter out = response.getWriter();
        out.println(xml);
    }
}
