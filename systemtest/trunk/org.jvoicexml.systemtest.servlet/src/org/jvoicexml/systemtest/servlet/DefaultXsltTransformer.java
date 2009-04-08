/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * This class transfer XML file with assigned XSLT file.
 *
 * @author lancer
 */
public class DefaultXsltTransformer extends HttpServlet {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(DefaultXsltTransformer.class);

    /**
     * template TXML file suffix.
     */
    private static final String TXML_SUFFIX = "txml";
    /**
     * template VXML file suffix.
     */
    private static final String VXML_SUFFIX = "vxml";
    /**
     * template IRCGI file suffix.
     */
    private static final String IRCGI_SUFFIX = "ircgi";
    /**
     * JSP file suffix.
     */
    private static final String JSP_SUFFIX = "jsp";
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 2961564659647125289L;


    /*
     * (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#service(
     *  javax.servlet.ServletRequest, javax.servlet.ServletResponse)
     */
    @Override
    public void service(final ServletRequest arg0, final ServletResponse arg1)
            throws ServletException, IOException {

        HttpServletRequest req = (HttpServletRequest) arg0;
        HttpServletResponse resp = (HttpServletResponse) arg1;

        String reqURI = req.getRequestURI();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("do service : " + reqURI);
        }

        if (reqURI.endsWith("." + TXML_SUFFIX)) {
            txmlService(req.getServletPath(), req, resp);
        } else if (reqURI.endsWith("." + VXML_SUFFIX)) {
            String path = req.getServletPath()
                    .replace(VXML_SUFFIX, TXML_SUFFIX);
            txmlService(path, req, resp);
        } else if (reqURI.endsWith("." + IRCGI_SUFFIX)) {
            ircgiService(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    /**
     * handle IRCGI transfer.
     * @param req http servlet request.
     * @param resp http servlet response.
     * @throws ServletException ServletException.
     * @throws IOException IOException.
     */
    private void ircgiService(final HttpServletRequest req,
            final HttpServletResponse resp)
            throws ServletException, IOException {
        ServletContext context = req.getSession().getServletContext();
        String rootPath = context.getRealPath("/");
        String relativelyPath = req.getServletPath();
        try {
            File source = createFile(rootPath, relativelyPath);

            File xsltFile = createFile(rootPath,
                    getInitParameter(IRCGI_SUFFIX));

            String jspPath = relativelyPath.replace(IRCGI_SUFFIX, JSP_SUFFIX);

            File jspFile = createFile(rootPath, jspPath);

            if (jspFile.exists()) {
                jspFile.delete();
            }
            try {
                transfer(jspFile, source, xsltFile);
                forward(jspPath, req, resp);
            } catch (Exception e) {
                LOGGER.error("Can not finish transformer.", e);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        e.getMessage());
            }
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }

    }

    /**
     * @param relativelyPath file path.
     * @param req http servlet request.
     * @param resp http servlet response.
     * @throws ServletException ServletException.
     * @throws IOException IOException.
     */
    private void txmlService(final String relativelyPath,
            final HttpServletRequest req,
            final HttpServletResponse resp)
            throws ServletException, IOException {
        ServletContext context = req.getSession().getServletContext();
        String rootPath = context.getRealPath("/");

        String xslt = getInitParameter(TXML_SUFFIX);

        File dataFile = null;
        File xsltFile = null;
        try {
            dataFile = createFile(rootPath, relativelyPath);
            xsltFile = createFile(rootPath, xslt);

            try {
                resp.setContentType("text/xml");
                OutputStream outStream = resp.getOutputStream();
                InputStream dataStream = new FileInputStream(dataFile);
                InputStream styleStream = new FileInputStream(xsltFile);
                transfer(outStream, dataStream, styleStream);
            } catch (Exception e) {
                LOGGER.error("Can not finish transformer.", e);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        e.getMessage());
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }
    }

    /**
     * create File with assigned parameter.
     * @param root directory
     * @param relativelyPath file path relative to root.
     * @return created file class.
     * @throws IOException IOException.
     */
    private File createFile(final String root, final String relativelyPath)
        throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("path = " + relativelyPath);
        }

        if (relativelyPath == null) {
            throw new IOException("file name is null.");
        }

        File file = new File(root, relativelyPath);

        if (!file.exists()) {
            throw new IOException(relativelyPath);
        }
        return file;
    }

    /**
     * @param relativelyURI file path.
     * @param req http servlet request.
     * @param resp http servlet response.
     * @throws ServletException ServletException.
     * @throws IOException IOException.
     */
    private void forward(final String relativelyURI,
            final HttpServletRequest req,
            final HttpServletResponse resp)
            throws ServletException, IOException {

        ServletContext context = req.getSession().getServletContext();
        RequestDispatcher rd = context
                .getRequestDispatcher("/" + relativelyURI);
        rd.forward(req, resp);
    }

    /**
     * transfer data XML file to outFile with styleFile XSLT style.
     * @param outFile output file.
     * @param dataFile source data file.
     * @param styleFile style file.
     * @throws IOException  IOException.
     * @throws TransformerException IOException.
     */
    private void transfer(final File outFile, final File dataFile,
            final File styleFile)
            throws IOException, TransformerException {
        OutputStream outStream = new FileOutputStream(outFile);
        InputStream dataStream = new FileInputStream(dataFile);
        InputStream styleStream = new FileInputStream(styleFile);
        transfer(outStream, dataStream, styleStream);
    }

    /**
     * transfer data stream to out stream with styleFile XSLT style stream.
     * @param out Output Stream.
     * @param dataStream data Input Stream.
     * @param styleStream Style InputStream.
     * @throws TransformerException TransformerException.
     */
    private void transfer(final OutputStream out, final InputStream dataStream,
            final InputStream styleStream)
            throws TransformerException {

        Source data = new StreamSource(dataStream);
        Source style = new StreamSource(styleStream);
        Result output = new StreamResult(out);

        Transformer xslt = TransformerFactory.newInstance().newTransformer(
                style);
        xslt.transform(data, output);
    }
}
