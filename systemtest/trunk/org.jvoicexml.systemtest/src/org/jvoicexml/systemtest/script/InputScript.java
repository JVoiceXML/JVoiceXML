/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date$, Dirk Schnelle-Walka, project lead
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

package org.jvoicexml.systemtest.script;

import java.util.Collection;
import java.util.List;

import org.jvoicexml.systemtest.Answer;
import org.jvoicexml.systemtest.Script;

/**
 * For each ir test, there is a script to control test application.
 * 
 * @author lancer
 * @author Dirk Schnelle-Walka
 */
public final class InputScript implements Script {
    /** The list of actions to perform. */
    private final List<Action> actions;

    /** The script id. */
    private final String id;

    /**
     * @param scriptId the id of the script
     */
    public InputScript(final String scriptId) {
        id = scriptId;
        actions = new java.util.LinkedList<Action>();
    }

    /**
     * @return action collection in this script
     */
    public Collection<Action> getActions() {
        return actions;
    }

    /**
     * Appends the given action to this script.
     * @param action action to add
     */
    public void append(final Action action) {
        actions.add(action);
    }

    /**
     * Retrieves the test id.
     * @return the test id
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFinished() {
        return actions.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Answer perform(final String event) {
        if (isFinished()) {
            return null;
        }
        final Action action = actions.get(0);
        Answer answer = action.execute(event);
        if (action.finished()) {
            actions.remove(0);
        }
        return answer;
    }
}
