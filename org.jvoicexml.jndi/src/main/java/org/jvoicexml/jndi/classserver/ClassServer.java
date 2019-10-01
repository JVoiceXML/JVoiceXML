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
 * Copyright (c) 1996, 1996, 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 */

package org.jvoicexml.jndi.classserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ClassServer is an abstract class that provides the basic functionality of a
 * mini-webserver, specialized to load class files only. A ClassServer must be
 * extended and the concrete subclass should define the <b>getBytes</b> method
 * which is responsible for retrieving the bytecodes for a class.
 * <p>
 * The ClassServer creates a thread that listens on a socket and accepts HTTP
 * GET requests. The HTTP response contains the bytecodes for the class that
 * requested in the GET header.
 * </p>
 * <p>
 * For loading remote classes, an RMI application can use a concrete subclass of
 * this server in place of an HTTP server.
 * </p>
 */
public abstract class ClassServer implements Runnable {
    /** Logger instance. */
    private static final Logger LOGGER = LogManager
            .getLogger(ClassServer.class);

    /** The server socket to handle code downloads. */
    private final ServerSocket server;

    /**
     * Constructs a ClassServer that listens on <b>port</b> and obtains a
     * class's bytecodes using the method <b>getBytes</b>.
     *
     * @param port
     *            the port number
     * @exception IOException
     *                if the ClassServer could not listen on <b>port</b>.
     */
    protected ClassServer(int port) throws IOException {
        server = new ServerSocket(port);
        newListener();
        LOGGER.info("class server started on port " + port);
    }

    /**
     * Returns an array of bytes containing the bytecodes for the class
     * represented by the argument <b>path</b>. The <b>path</b> is a dot
     * separated class name with the ".class" extension removed.
     * @param path the path to look for
     * @return the bytecodes for the class
     * @exception ClassNotFoundException
     *                if the class corresponding to <b>path</b> could not be
     *                loaded.
     * @exception IOException
     *                if error occurs reading the class
     */
    public abstract byte[] getBytes(final String path)
            throws IOException, ClassNotFoundException;

    /**
     * The "listen" thread that accepts a connection to the server, parses the
     * header to obtain the class file name and sends back the bytecodes for the
     * class (or error if the class is not found or the response was malformed).
     */
    public void run() {
        Socket socket;

        // accept a connection
        try {
            socket = server.accept();
        } catch (IOException e) {
            LOGGER.error("Class Server died: " + e.getMessage(), e);
            return;
        }

        // create a new thread to accept the next connection
        newListener();

        try {
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            try {
                // get path to class file from header
                DataInputStream in = new DataInputStream(
                        socket.getInputStream());
                String path = getPath(in);
                // retrieve bytecodes
                byte[] bytecodes = getBytes(path);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("path: '" + path  + "', length:"
                            + bytecodes.length);
                }
                // send bytecodes in response (assumes HTTP/1.0 or later)
                out.writeBytes("HTTP/1.0 200 OK\r\n");
                out.writeBytes(
                        "Content-Length: " + bytecodes.length + "\r\n");
                out.writeBytes("Content-Type: application/java\r\n\r\n");
                out.write(bytecodes);
                out.flush();
            } catch (Exception e) {
                // write out error response
                out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
                out.writeBytes("Content-Type: text/html\r\n\r\n");
                out.flush();
            }

        } catch (IOException ex) {
            // eat exception (could log error to log file, but
            // write out to stdout for now).
            LOGGER.warn("error writing response: " + ex.getMessage(), ex);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Create a new thread to listen.
     */
    private void newListener() {
        final Thread currentThread = Thread.currentThread();
        final ClassLoader loader = currentThread.getContextClassLoader();
        final Thread thread = new Thread(this);
        thread.setContextClassLoader(loader);
        thread.start();
    }

    /**
     * Returns the path to the class file obtained from parsing the HTML header.
     */
    private static String getPath(DataInputStream in) throws IOException {
        String line = in.readLine();
        String path = "";

        // extract class from GET line
        if (line.startsWith("GET /")) {
            line = line.substring(5, line.length() - 1).trim();

            int index = line.indexOf(".class ");
            if (index != -1) {
                path = line.substring(0, index).replace('/', '.');
            }
        }

        // eat the rest of header
        do {
            line = in.readLine();
        } while ((line.length() != 0) && (line.charAt(0) != '\r')
                && (line.charAt(0) != '\n'));

        if (path.length() != 0) {
            return path;
        } else {
            throw new IOException("Malformed Header");
        }
    }
}
