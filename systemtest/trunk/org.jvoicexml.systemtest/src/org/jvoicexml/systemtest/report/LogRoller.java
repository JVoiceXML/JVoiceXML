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
package org.jvoicexml.systemtest.report;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.varia.ExternallyRolledFileAppender;

/**
 * Log4j ExternallyRolledFileAppender roller.
 * @author lancer
 *
 */
public class LogRoller {

    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(LogRoller.class);

    /**
     * log name of local.
     */
    public static final String LOCAL_LOG_NAME = "localLog";
    /**
     * log name of remote.
     */
    public static final String REMOTE_LOG_NAME = "remoteLog";
    /**
     * log name of ERROR level log.
     */
    public static final String ERROR_LEVEL_LOG_NAME = "errorLog";
    /**
     * log name of Log Tag log.
     */
    public static final String LOG_TAG_LOG_NAME = "logTagLog";

    /**
     * first log suffix of log4j when rolled.
     */
    public static final String LAST_LOG_SUFFIX = ".1";

    /**
     * the host name.
     */
    private String host = null;

    /**
     * the port list.
     */
    private String ports = null;

    /**
     * @param arg0 host of log4j ExternallyRolledFileAppender.
     */
    public final void setHost(final String arg0) {
        this.host = arg0;
    }

    /**
     * @param arg0 port list split by ','.
     */
    public final void setPort(final String arg0) {
        this.ports = arg0;
    }

    /**
     * roll the current log to ${log file name}.1 for log4j implements.
     */
    public final void roll() {
        if (host == null || ports == null) {
            LOGGER.warn("host or ports is not current. "
                    + "check config.");
            return;
        }
        String[] words = ports.split(",");
        for (String word : words) {
            try {
                int port = Integer.parseInt(word.trim());
                roll(host, port);
            } catch (NumberFormatException e) {
                LOGGER.warn(word + " is not a integer. "
                        + "check config.");
                continue;
            }
        }
    }

    /**
     * roll one ExternallyRolledFileAppender.
     * @param externalHost host of log4j ExternallyRolledFileAppender.
     * @param externalPort port of log4j ExternallyRolledFileAppender
     */
    private void roll(final String externalHost, final int externalPort) {
        try {
            final Socket socket = new Socket(externalHost, externalPort);
            final OutputStream out = socket.getOutputStream();
            final DataOutputStream dos = new DataOutputStream(out);
            final InputStream in = socket.getInputStream();
            final DataInputStream dis = new DataInputStream(in);
            dos.writeUTF(ExternallyRolledFileAppender.ROLL_OVER);
            String rc = dis.readUTF();
            if (ExternallyRolledFileAppender.OK.equals(rc)) {
                LOGGER.info("Roll over signal acknowledged "
                        + "by remote appender.");
            } else {
                LOGGER.warn("Unexpected return code " + rc
                        + " from remote entity.");
            }
            socket.close();
        } catch (UnknownHostException e) {
            LOGGER.info("UnknownHostException", e);
        } catch (IOException e) {
            LOGGER.info("IOException", e);

        }
    }

}
