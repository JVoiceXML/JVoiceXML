/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager.jtapi;

import org.jvoicexml.RemoteClient;

/**
 * Jtapi based implementation of a {@link RemoteClient}.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.6
 */
@SuppressWarnings("serial")
public final class JtapiRemoteClient implements RemoteClient {
    /** A terminal for a JTapi connection. */
    private final JVoiceXmlTerminal terminal;

    /** Type of the {@link org.jvoicexml.SystemOutput} resource. */
    private final String output;

    /** Type of the {@link org.jvoicexml.UserInput} resource. */
    private final String input;

    /**
     * Constructs a new object.
     *
     * @param term
     *            ther JTAPI terminal.
     * @param outputType
     *            type of the {@link org.jvoicexml.SystemOutput} resource
     * @param inputType
     *            type of the {@link org.jvoicexml.UserInput} resource
     */
    public JtapiRemoteClient(final JVoiceXmlTerminal term,
            final String outputType, final String inputType) {
        terminal = term;
        output = outputType;
        input = inputType;
    }

    /**
     * {@inheritDoc}
     */
    public String getCallControl() {
        return "jtapi1.3.1";
    }

    /**
     * {@inheritDoc}
     */
    public String getSystemOutput() {
        return output;
    }

    /**
     * {@inheritDoc}
     */
    public String getUserInput() {
        return input;
    }

    /**
     * Retrieves the terminal name.
     *
     * @return name of the terminal.
     */
    public String getTerminalName() {
        return terminal.getTerminalName();
    }

    /**
     * Retrieves the terminal.
     * @return the terminal.
     */
    public JVoiceXmlTerminal getTerminal() {
        return terminal;
    }
}
