/*
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

package org.jvoicexml.documentserver.jetty;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ClassServer is an abstract class that provides the
 * basic functionality of a mini-webserver, specialized
 * to load class files only. A ClassServer must be extended
 * and the concrete subclass should define the <b>getBytes</b>
 * method which is responsible for retrieving the bytecodes
 * for a class.<p>
 *
 * The ClassServer creates a thread that listens on a socket
 * and accepts  HTTP GET requests. The HTTP response contains the
 * bytecodes for the class that requested in the GET header. <p>
 *
 * For loading remote classes, an RMI application can use a concrete
 * subclass of this server in place of an HTTP server. <p>
 *
 * @see ClassFileServer
 */
public abstract class ClassServer implements Runnable {

    private ServerSocket server = null;
    private final int port;

    /**
     * Constructs a ClassServer that listens on <b>port</b> and
     * obtains a class's bytecodes using the method <b>getBytes</b>.
     *
     * @param port the port number
     * @exception IOException if the ClassServer could not listen
     *            on <b>port</b>.
     */
    protected ClassServer(int port) throws IOException
    {
	this.port = port;
	server = new ServerSocket(port);
	newListener();
    }

    /**
     * Returns an array of bytes containing the bytecodes for
     * the class represented by the argument <b>path</b>.
     * The <b>path</b> is a dot separated class name with
     * the ".class" extension removed.
     *
     * @return the bytecodes for the class
     * @exception ClassNotFoundException if the class corresponding
     * to <b>path</b> could not be loaded.
     * @exception IOException if error occurs reading the class
     */
    public abstract byte[] getBytes(String path)
	throws IOException, ClassNotFoundException;
    
    /**
     * The "listen" thread that accepts a connection to the
     * server, parses the header to obtain the class file name
     * and sends back the bytecodes for the class (or error
     * if the class is not found or the response was malformed).
     */
    public void run()
    {
	Socket socket;

	// accept a connection
	try {
	    socket = server.accept();
	} catch (IOException e) {
	    System.out.println("Class Server died: " + e.getMessage());
	    e.printStackTrace();
	    return;
	}

	// create a new thread to accept the next connection
	newListener();

	try {
	    DataOutputStream out =
		new DataOutputStream(socket.getOutputStream());
	    try {
		// get path to class file from header
		DataInputStream in =
		    new DataInputStream(socket.getInputStream());
		String path = getPath(in);
		// retrieve bytecodes
		byte[] bytecodes = getBytes(path);
		// send bytecodes in response (assumes HTTP/1.0 or later)
		try {
		    out.writeBytes("HTTP/1.0 200 OK\r\n");
		    out.writeBytes("Content-Length: " + bytecodes.length +
				   "\r\n");
		    out.writeBytes("Content-Type: application/java\r\n\r\n");
		    out.write(bytecodes);
		    out.flush();
		} catch (IOException ie) {
		    return;
		}
		
	    } catch (Exception e) {
		// write out error response
		out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
		out.writeBytes("Content-Type: text/html\r\n\r\n");
		out.flush();
	    }
		
	} catch (IOException ex) {
	    // eat exception (could log error to log file, but
	    // write out to stdout for now).
	    System.out.println("error writing response: " + ex.getMessage());
	    ex.printStackTrace();
	    
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
    private void newListener() 
    {
	(new Thread(this)).start();
    }

    /**
     * Returns the path to the class file obtained from
     * parsing the HTML header.
     */
    private static String getPath(DataInputStream in)
	throws IOException
    {
	String line = in.readLine();
	String path = "";
	
	// extract class from GET line
	if (line.startsWith("GET /")) {
	    line = line.substring(5, line.length()-1).trim();

	    int index = line.indexOf(".class ");
	    if (index != -1) {
		path = line.substring(0, index).replace('/', '.');
	    }
	}

	// eat the rest of header
	do {
	    line = in.readLine();
	} while ((line.length() != 0) &&
	         (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

	if (path.length() != 0) {
	    return path;
	} else {
	    throw new IOException("Malformed Header");
	}
    }
}
