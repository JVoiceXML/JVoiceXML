/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2017 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter;

/**
 * A VoiceXML interpreter is at all times in one of follwoing states:
 *
 * <ol>
 * <li>
 * <b>waiting</b> for input in an input item (such as
 * <code>&lt;field&gt;</code>, <code>&lt;record&gt;</code>, or
 * <code>&lt;transfer&gt;</code>), or
 * </li>
 * <li>
 * <b>transitioning</b> between input items in response to an input (including
 * spoken utterances, dtmf key presses, and input-related events such as a
 * noinput or nomatch event) received while in the waiting state. While in the
 * transitioning state no speech input is collected, accepted or interpreted.
 * Consequently root and document level speech grammars (such as defined in
 * <code>&lt;link&gt;</code>s) may not be active at all times. However, DTMF
 * input (including timing information) should be collected and buffered in the
 * transition state. Similarly, asynchronously generated events not related
 * directly to execution of the transition should also be buffered until the
 * waiting state (e.g. <code>connection.disconnect.hangup</code>).
 * </li>
 * </ol>
 *
 * @author Dirk Schnelle-Walka
 * @since 0.6
 */
public enum InterpreterState {
    /**
     * The interpreter is waiting for input in an input item (such as
     * <code>&lt;field&gt;</code>, <code>&lt;record&gt;</code>, or
     * <code>&lt;transfer&gt;</code>).
     */
    WAITING("waiting"),

    /**
     * The interpreter is transitioning between input items in response to an
     * input (including spoken utterances, dtmf key presses, and input-related
     * events such as a noinput or nomatch event) received while in the waiting
     * state. While in the transitioning state no speech input is collected,
     * accepted or interpreted. Consequently root and document level speech
     * grammars (such as defined in <code>&lt;link&gt;</code>s) may not be
     * active at all times. However, DTMF input (including timing information)
     * should be collected and buffered in the transition state. Similarly,
     * asynchronously generated events not related directly to execution of the
     * transition should also be buffered until the waiting state (e.g.
     * <code>connection.disconnect.hangup</code>).
     */
    TRANSITIONING("transitioning"),

    /**
     * Under certain circumstances (in particular, while the VoiceXML
     * interpreter is processing a disconnect event) the interpreter may
     * continue executing in the final processing state after there is no longer
     * a connection to allow the interpreter to interact with the end user. The
     * purpose of this state is to allow the VoiceXML application to perform any
     * necessary final cleanup, such as submitting information to the
     * application server.
     */
    FINALPROCESSING("finalprocessing");
    
    /** Name of the state. */
    private final String name;

    /**
     * Do not create from outside.
     * @param statename name of the state.
     */
    InterpreterState(final String statename) {
        name = statename;
    }

    /**
     * Retrieves the name of this state.
     * @return Name of this state.
     */
    public String getStateName() {
        return name;
    }
}
