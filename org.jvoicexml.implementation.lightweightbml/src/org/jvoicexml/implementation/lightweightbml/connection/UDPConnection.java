/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2014 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 */
package org.jvoicexml.implementation.lightweightbml.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

import org.jvoicexml.implementation.lightweightbml.xmltags.BML;

/**
 * Handles a connection for sending and receiving bml-files via udp.
 * 
 * @author Matthias Mettel
 * @author Markus Ermuth
 * @author Alex Krause
 * 
 * @version $LastChangedRevision$
 * @since 0.7.3
 */
public final class UDPConnection
    extends Observable
    implements Runnable {
  /**
   * The udp sockket to control the connection.
   */
  private DatagramSocket socket;

  /**
   * THe thread for synchronous receving data from network stream.
   */
  private Thread thread;

  /**
   * The maximal size of the data buffer.
   */
  private int bufferSize;

  /**
   * The buffer to store the data, which are received.
   */
  private byte[] buffer;

  /**
   * Constructor to define the buffer size.
   * 
   * @param theBufferSize
   *          the buffer size for receving data
   * @throws SocketException
   */
  public UDPConnection(final int theBufferSize) {
    socket = null;
    thread = null;
    buffer = null;
    bufferSize = theBufferSize;
  }

  /**
   * Connects the udp socket to a specific host.
   * 
   * @param host the address of the host
   * @param port the port of the host application
   * @throws UnknownHostException the address cannot resolved
   * @throws SocketException some errors with the socket occurs
   */
  public void connect(final String host, final int port)
      throws UnknownHostException, SocketException {
    if (socket != null) {
      disconnect();
    }
    socket = new DatagramSocket();

    socket.connect(Inet4Address.getByName(host), port);
  }

  /**
   * Binds the socket to a specific address and port.
   * 
   * @param host the ip address of the host
   * @param port the port of the host
   * @throws SocketException some errors with the socket occurs
   */
  public void bind(final String host, final int port) throws SocketException {
    if (socket != null) {
      disconnect();
    }

    socket = new DatagramSocket(new InetSocketAddress(host, port));

    thread = new Thread(this);
    thread.start();
  }

  /**
   * Disconnects the socket.
   */
  public void disconnect() {
    if (thread != null) {
      thread.interrupt();
      thread = null;
    }

    socket.close();
    socket = null;
  }

  /**
   * Sends a string to specific host.
   * 
   * @param bmlString the string to send
   */
  public void send(final String bmlString) {
    try {
      final byte[] stringBytes = bmlString.getBytes("UTF-8");
      final DatagramPacket packet =
          new DatagramPacket(stringBytes, stringBytes.length);
      socket.send(packet);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a bml tree to a specific target.
   * 
   * @param bml the bml tree
   */
  public void send(final BML bml) {
    send(bml.toString());
  }

  /**
   * Method to execute synchronous receiving data.
   */
  @Override
  public void run() {
    while (true) {
      try {
        buffer = new byte[bufferSize];

        DatagramPacket packet = new DatagramPacket(buffer, bufferSize);

        if (socket == null) {
          return;
        } else {
          socket.receive(packet);
        }

        String str = new String(packet.getData(), "UTF-8");

        setChanged();
        notifyObservers(str);
      } catch (IOException e) {
        if (e.getMessage().compareTo("socket closed") == 0) {
          return;
        }

        e.printStackTrace();
      }
    }
  }

}
