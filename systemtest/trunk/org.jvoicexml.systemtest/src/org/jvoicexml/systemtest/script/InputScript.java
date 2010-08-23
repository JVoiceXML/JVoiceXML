/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 2161 $
 * Date:    $Date: 2010-04-19 20:20:06 +0200 (Mo, 19 Apr 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2010-04-19 20:20:06 +0200 (Mo, 19 Apr 2010) $, Dirk Schnelle-Walka, project lead
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
public class InputScript implements Script {
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
     * @return action collection in this script
     */
    public final void append(Action a) {
        actions.add(a);
    }

    /**
     * @return IR test ID
     */
    public final String getId() {
        return id;
    }

    public boolean isFinished() {
        return actions.isEmpty();
    }

    public Answer perform(String event) {
        if (!actions.isEmpty()) {
            Action action = actions.get(0);
            Answer a = action.execute(event);
            if (action.finished()) {
                actions.remove(0);
            }
            return a;
        } else {
            return null;
        }
    }
}
