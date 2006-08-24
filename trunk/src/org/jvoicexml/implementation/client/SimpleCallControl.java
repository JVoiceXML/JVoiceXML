/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date: $
 * Author:  $java.LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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
qq
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.implementation.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.implementation.CallControl;


/**
 * Basic call control to stream data from and to a client.
 *
 * <p>
 * Objects of this class are created at the client side and then
 * transferred to the interpreter. They carry the data with them that
 * is needed to establish a TCP/IP connection to the client.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
public class SimpleCallControl
        implements CallControl {
    /** The serial version UID: */
    static final long serialVersionUID = 5941398762917701143L;

    /** Type of the implementation platform to use. */
    private String type;

    /** The port for the server socket. */
    private int port;

    /** The address of the server socket. */
    private InetAddress address;

    /** The client socket. */
    private Socket endpoint;

    /**
     * Constructs a new object.
     */
    public SimpleCallControl() {
    }

    /**
     * Sets the port to use.
     * @param prt Port number.
     */
    public void setPort(final int prt) {
        port = prt;
    }

    public void setAddress(final InetAddress addr) {
        address = addr;
    }

    /**
     * {@inheritDoc}
     *
     * Opens a socket connection to the client.
     */
    public void open()
            throws NoresourceError {
        try {
            endpoint = new Socket(address, port);
        } catch (java.io.IOException ioe) {
            throw new NoresourceError(ioe);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Closes the socket.
     */
    public void close() {
        if (endpoint != null) {
            try {
                endpoint.close();
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
            }

            endpoint = null;
        }
    }

    /**
     * Retrieves the input for the <code>UserInput</code>.
     *
     * @return Input for the <code>UserInput</code>.
     * @todo Implement this org.jvoicexml.implementation.CallControl method
     */
    public InputStream getInputStream() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public OutputStream getOutputStream() {
        if (endpoint == null) {
            return null;
        }

        try {
            return endpoint.getOutputStream();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();

            return null;
        }
    }

    /**
     * Retrieves the type of the platform to use.
     *
     * @return Type of the platform.
     */
    public String getPlatformType() {
        return type;
    }

}
