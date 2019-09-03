/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test cases for {@link JndiCodebaseHandler}
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class JndiCodebaseHandlerTest {

    /**
     * Test method for {@link org.eclipse.jetty.server.handler.AbstractHandler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test
    public void testHandleStringRequestHttpServletRequestHttpServletResponse() throws IOException, ServletException {
        final Request baseRequest = Mockito.mock(Request.class);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(
                JndiCodebaseHandler.CONTEXT_PATH + "/org/jvoicexml/Application.class");
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final JndiCodebaseHandler handler = new JndiCodebaseHandler();
        final ServletOutputStream out = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(out);
        handler.handle(null, baseRequest, request, response);
    }

    /**
     * Test method for {@link org.eclipse.jetty.server.handler.AbstractHandler#handle(java.lang.String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
     * @throws ServletException 
     * @throws IOException 
     */
    @Test
    public void testHandleStringRequestHttpServletRequestHttpServletResponseInvalidClass() throws ServletException, IOException {
        final Request baseRequest = Mockito.mock(Request.class);
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRequestURI()).thenReturn(
                JndiCodebaseHandler.CONTEXT_PATH + "/org/jvoicexml/InvalidApplication.class");
        final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        final JndiCodebaseHandler handler = new JndiCodebaseHandler();
        final ServletOutputStream out = Mockito.mock(ServletOutputStream.class);
        Mockito.when(response.getOutputStream()).thenReturn(out);
        IOException expected = null;
        try {
            handler.handle(null, baseRequest, request, response);
        } catch (IOException e) {
            expected = e;
        }
        Assert.assertNotNull("Class should not be found", expected);
    }

}
