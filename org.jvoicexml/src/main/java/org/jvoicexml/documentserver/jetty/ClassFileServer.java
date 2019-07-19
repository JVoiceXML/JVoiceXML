/*
 * Copyright (c) 1996, 1996, 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package org.jvoicexml.documentserver.jetty;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * The ClassFileServer implements a ClassServer that
 * reads class files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 */
public class ClassFileServer extends ClassServer {
    
    private final String classpath;
    
    private static int DefaultServerPort = 2001;
    
    /**
     * Constructs a ClassFileServer.
     *
     * @param classpath the classpath where the server locates classes
     */
    public ClassFileServer(int port, String classpath) throws IOException
    {
	super(port);
	this.classpath = classpath;
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
     */
    @Override
    public byte[] getBytes(String path)
	throws IOException, ClassNotFoundException 
    {
	System.out.println("reading: " + path);
	File f = new File(classpath + File.separator +
			  path.replace('.', File.separatorChar) + ".class");
	int length = (int)(f.length());
	if (length == 0) {
	    throw new IOException("File length is zero: " + path);
	} else {
	    FileInputStream fin = new FileInputStream(f);
	    DataInputStream in = new DataInputStream(fin);
	    
	    byte[] bytecodes = new byte[length];
	    in.readFully(bytecodes);
	    return bytecodes;
	}
    }

    /**
     * Main method to create the class server that reads
     * class files. This takes two command line arguments, the
     * port on which the server accepts requests and the
     * root of the classpath. To start up the server: <br><br>
     *
     * <code>   java ClassFileServer <port> <classpath>
     * </code><br><br>
     *
     * The codebase of an RMI server using this webserver would
     * simply contain a URL with the host and port of the web
     * server (if the webserver's classpath is the same as
     * the RMI server's classpath): <br><br>
     *
     * <code>   java -Djava.rmi.server.codebase=http://zaphod:2001/ RMIServer
     * </code> <br><br>
     *
     * You can create your own class server inside your RMI server
     * application instead of running one separately. In your server
     * main simply create a ClassFileServer: <br><br>
     *
     * <code>   new ClassFileServer(port, classpath);
     * </code>
     */
    public static void main(String args[]) 
    {
	int port = DefaultServerPort;
	String classpath = "";
	
	if (args.length >= 1) {
	    port = Integer.parseInt(args[0]);
	}

	if (args.length >= 2) {
	    classpath = args[1];
	}

	try {
	    new ClassFileServer(port, classpath);
	} catch (IOException e) {
	    System.out.println("Unable to start ClassServer: " +
			       e.getMessage());
	    e.printStackTrace();
	}
    }
}
