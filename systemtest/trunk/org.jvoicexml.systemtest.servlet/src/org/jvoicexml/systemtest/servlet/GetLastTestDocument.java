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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * This class return last test XML file.
 * 
 * @author lancer
 */
public class GetLastTestDocument extends HttpServlet {

    /**     */
    private static final long serialVersionUID = -6564669429931890009L;

    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(DefaultXsltTransformer.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void service(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException,
            IOException {
        String lastReq = (String) request.getSession().getAttribute("LASTREQ");
        //response.getWriter().write(lstReq);


        String rootPath = request.getSession().getServletContext().getRealPath(
                "/");

        File dataFile = createFile(rootPath, lastReq);
        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        response.reset();
        response.setContentType("text/xml");
        while(true){
            String line = br.readLine();
            if(null == line ){
                break;
            }
            response.getWriter().write(line);
        }
    }

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
}
