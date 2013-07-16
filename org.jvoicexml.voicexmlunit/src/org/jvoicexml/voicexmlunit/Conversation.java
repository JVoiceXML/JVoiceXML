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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Iterator;

import org.jvoicexml.voicexmlunit.io.*;

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
    private ConcurrentLinkedQueue<Assertion> queue;
    private Iterator<Assertion> iterator;

    /**
     * Constructor
     */
    public Conversation() {
        queue = new ConcurrentLinkedQueue<Assertion>();
        iterator = null;
    }

    /**
     * Add a new Output with the expected message
     * Discouraged usage, better use the more generic addStatement(...)
     * @param message
     *            Message to expect
     */
    @Deprecated
    public void addOutput(final String message) {
        add(new Output(message));
    }

    /**
     * Add a new Input with the message to be send
     * Discouraged usage, better use the more generic addStatement(...)
     * @param message
     *            Message to send
     */
    @Deprecated
    public void addInput(final String message) {
        add(new Input(message));
    }

    /**
     * Add a new Dtmf with the message to be send
     * Discouraged usage, better use the more generic addStatement(...)
     * @param message
     *            Message to send
     */
    @Deprecated
    public void addDtmf(final char message) {
        add(new Dtmf(message));
    }

    /**
     * Discouraged usage, better use size()
     * @return Count of so far collected statements wit addOutput/addInput
     */
    @Deprecated
    public int countStatements() {
        return size();
    }

    /**
     * Add a statement to the internal queue.
     * @param s statement to add in queue
     */
    public boolean add(final Assertion a) {
        return queue.add(a);
    }

    /**
     * Begins the conversation.
     * @return First statement of the conversation
     */
    public Assertion begin() {
        if (queue.isEmpty()) {
            iterator = null; // invalidate any existing cursor
            return null;
        } else {
            iterator = queue.iterator();
            return iterator.next();
        }
    }

    /**
     * Go to the next statement in the conversation.
     * If there are no more elements left, this method invalidates
     * the conversation and returns an invalid object.
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

    /*
     * @return current count of collected Assertions
     */
    public int size() {
        return queue.size();
    }

    /**
     * @return Representation as a String (e.g. for logging purpose).
     */
    public String toString() {
        // TODO: insert individual concrete subclasses (out/in)
        return queue.toString();
    }
}
