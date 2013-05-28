/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.mmi.events/src/org/jvoicexml/mmi/events/Mmi.java $
 * Version: $LastChangedRevision: 3651 $
 * Date:    $Date: 2013-02-27 00:16:33 +0100 (Wed, 27 Feb 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.util.LinkedList;
import java.util.ListIterator;

import org.jvoicexml.voicexmlunit.io.Assertion;
import org.jvoicexml.voicexmlunit.io.Dtmf;
import org.jvoicexml.voicexmlunit.io.Input;
import org.jvoicexml.voicexmlunit.io.Output;

/**
 * Conversation is a helper for the communication in between the processing of
 * JVoiceXML, Supervisor and TextServer. You can add Output and Inputs objects
 * in the order you think to expect them coming from the VoiceXML document
 * interpreter. Output and Input are of the abstract type Statement with an
 * indivual message text.
 * 
 * @author Raphael Groner
 * 
 */
public final class Conversation {
    private LinkedList<Assertion> history;
    private ListIterator<Assertion> iterator;

    /**
     * Constructor
     */
    public Conversation() {
        history = new LinkedList<Assertion>();
        iterator = null;
    }

    /**
     * Add a new Output with the expected message
     * 
     * @param message
     *            Message to expect
     */
    public void addOutput(String message) {
        Output output = new Output(message);
        history.add(output);
    }

    /**
     * Add a new Input with the message to be send
     * 
     * @param message
     *            Message to send
     */
    public void addInput(String message) {
        Input input = new Input(message);
        history.add(input);
    }

    /**
     * Add a new Dtmf with the message to be send
     * 
     * @param message
     *            Message to send
     */
    public void addDtmf(char message) {
        Dtmf dtmf = new Dtmf(message);
        history.add(dtmf);
    }

    /**
     * Begin the conversation
     * 
     * @return First statement of the conversation
     */
    public Assertion begin() {
        if (history.isEmpty()) {
            iterator = null; // invalidate any existing cursor
            return null;
        } else {
            iterator = history.listIterator(0);
            return iterator.next();
        }
    }

    /**
     * Go to the next statement in the conversation If there are no more
     * elements left, this method invalidates the conversation and returns an
     * invalid object.
     * 
     * @return Next statement after the previously current one
     */
    public Assertion next() {
        if (iterator == null) {
            return begin();
        } else if (iterator.hasNext()) {
            return iterator.next();
        } else {
            iterator = null;
            return null;
        }
    }

    /**
     * @return Count of so far collected statements wit addOutput/addInput
     */
    public int countStatements() {
        return history.size();
    }
}
