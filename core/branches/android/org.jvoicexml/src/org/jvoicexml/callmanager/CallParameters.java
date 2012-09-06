/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/callmanager/CallParameters.java $
 * Version: $LastChangedRevision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.callmanager;

import java.net.URI;

/**
 * Container for call parameters.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2129 $
 * @since 0.7
 */
public class CallParameters {
    /** The called id. */
    private URI calledId;

    /** The id of the caller. */
    private URI callerId;

    /** The terminal. */
    private Terminal terminal;

    /**
     * Retrieves the terminal.
     * @return the terminal
     */
    public final Terminal getTerminal() {
        return terminal;
    }

    /**
     * Sets the terminal.
     * @param term the terminal to set
     */
    public final void setTerminal(final Terminal term) {
        terminal = term;
    }

    /**
     * Retrieves the called id.
     * @return the calledId
     */
    public final URI getCalledId() {
        return calledId;
    }

    /**
     * Sets the called id.
     * @param id the called id to set
     */
    public final void setCalledId(final URI id) {
        calledId = id;
    }

    /**
     * Retrieves the id of the caller.
     * @return the caller id
     */
    public final URI getCallerId() {
        return callerId;
    }

    /**
     * Sets the caller id.
     * @param id the caller id to set
     */
    public final void setCallerId(final URI id) {
        callerId = id;
    }

}
