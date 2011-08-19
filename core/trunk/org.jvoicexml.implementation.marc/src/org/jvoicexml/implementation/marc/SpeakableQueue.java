/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.implementation.marc;

import java.util.Queue;

import org.jvoicexml.SpeakableText;

/**
 * Queued speakables.
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @since 0.7.5
 *
 */
class SpeakableQueue {
    /** Queued speakables. */
    private final Queue<QueuedSpeakable> queue;

    /** The sequence number of queued speakables. */
    private int seq;

    /**
     * Constructs a new object.
     */
    public SpeakableQueue() {
        queue = new java.util.LinkedList<QueuedSpeakable>();
    }

    /**
     * Adds the given speakable with the given id to the queue.
     * @param id the id of the speakable.
     * @param speakable the speakable
     */
    public void add(final SpeakableText speakable) {
        synchronized (queue) {
            ++seq;
            final String id = "JVoiceXMLTrack " + seq;
            final QueuedSpeakable elem = new QueuedSpeakable(id, speakable);
            queue.add(elem);
        }
    }

    /**
     * Retrieves the next speakable.
     * @return the next speakable.
     */
    public QueuedSpeakable peek() {
        synchronized (queue) {
            return queue.peek();
        }
    }

    /**
     * Retrieves and removes the topmost speakable of this queue.
     * @return the topmost speakable.
     */
    public QueuedSpeakable poll() {
        synchronized (queue) {
            return queue.poll();
        }
    }

    /**
     * Checks if the queue is empty.
     * @return <code>true</code> if the queue is empty
     */
    public boolean isEmpty() {
        synchronized (queue) {
            return queue.isEmpty();
        }
    }
}
