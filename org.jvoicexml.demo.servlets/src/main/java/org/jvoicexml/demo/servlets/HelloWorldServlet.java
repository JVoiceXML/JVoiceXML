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
package org.jvoicexml.demo.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Submit;
import org.jvoicexml.xml.vxml.Var;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;

/**
 * Servlet that generates a 'hello world' VoiceXML document and calls the
 * <em>Goodbye</em> servlet.
 *
 * @author Dirk Schnelle-Walka
 *
 * @see org.jvoicexml.demo.helloworldservletdemo.servlet.GoodbyeServlet
 */
@WebServlet(displayName = "HelloWorld", 
    description = "VoiceXML Hello World Demo", urlPatterns = "/HelloWorld")
public final class HelloWorldServlet
        extends HttpServlet {
    /** The serial version UID. */
    private static final long serialVersionUID = 8678456219150026410L;

    /**
     * Construct a new object.
     */
    public HelloWorldServlet() {
    }

    /**
     * Create a simple VoiceXML document containing the hello world phrase.
     *
     * @param request
     *        HttpServletRequest object that contains the request the client has
     *        made of the servlet
     * @param response
     *        HttpServletResponse object that contains the response the servlet
     *        sends to the client
     * @return Created VoiceXML document, <code>null</code> if an error
     *         occurs.
     */
    private VoiceXmlDocument createHelloWorld(final HttpServletRequest request,
                                              final HttpServletResponse
                                              response) {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();
        final Form form = vxml.appendChild(Form.class);

        final Var var = form.appendChild(Var.class);
        var.setName("message");
        var.setExpr("'Goodbye!'");

        final Block block = form.appendChild(Block.class);
        block.addText("Hello World!");

        final StringBuffer url = request.getRequestURL();
        final String path = request.getServletPath();
        url.delete(url.length() - path.length() + 1, url.length());
        url.append("Goodbye");

        final Submit next = block.appendChild(Submit.class);
        next.setNext(response.encodeURL(url.toString()));
        next.setNamelist("message");

        return document;
    }

    /**
     * Returns a hello world VoiceXML document.
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
        final VoiceXmlDocument document = createHelloWorld(request, response);
        final String xml = document.toXml();
        final PrintWriter out = response.getWriter();
        out.println(xml);
    }
}
