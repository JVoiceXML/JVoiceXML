/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm;

import org.apache.log4j.Logger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
public class FiniteStateMachine {

        private static final Logger LOGGER = Logger.getLogger(FiniteStateMachine.class);
    private volatile State state;
    private final Set<State> states;
    private volatile State oldState;

    public FiniteStateMachine(final State state) {
        super();
        this.state = state;
        this.states = new HashSet<State>();
    }

    protected synchronized void addState(final State state) {
        states.add(state);
    }

    protected synchronized void assertState(final State state) throws IllegalStateException {
        if (!this.state.equals(state)) {
            final StringBuilder buffer = new StringBuilder();
            buffer.append("Illegal state exception. The current state should be ")
                    .append(state.getName()).append(" but the current state is ").append(this.state.getName());
            throw new IllegalStateException(buffer.toString());
        }
    }

    protected synchronized void assertState(final Collection<State> states) throws IllegalStateException {
        for (final State state : states) {
            if (this.state.equals(state)) {
                return;
            }
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append("Illegal state exception. The current state should be one of the following states ");
        for (final State state : states) {
            buffer.append(state.getName()).append(",");
        }
        buffer.append(" but the current state is ").append(state.getName());
        buffer.append(" this:"+this);
        throw new IllegalStateException(buffer.toString());
    }

    protected synchronized void assertStateNot(final Collection<State> states) throws IllegalStateException {
        for (final State state : states) {
            if (this.state.equals(state)) {
                throw new IllegalStateException("Illegal state: " + state.getName());
            }
        }
    }

    public synchronized State getState() {
        return state;
    }
    public synchronized State getOldState() {
        return oldState;
    }
    public void  setOldState(State st) {
         oldState=st;
    }

    protected synchronized void setState(final State state) {
        if (states.contains(state)) {
            // Get the possible transitions.
            final Set<State> transitions = this.state.getTransitions();
            if (transitions.contains(state)) {
                // Set new state.
                this.state = state;
                LOGGER.debug("set state:"+state.getName() + " for "+this);
            } else {
                final StringBuilder buffer = new StringBuilder();
                buffer.append("Can not transition from a(n) ").append(this.state.getName()).append(" state to a ").append(state.getName()).append(" state.");
                throw new IllegalArgumentException(buffer.toString());
            }
        }
    }
}
