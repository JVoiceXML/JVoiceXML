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

import java.util.Map;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.Script;
import org.jvoicexml.systemtest.ScriptFactory;

/**
 * Factory for test scripts.
 * 
 * @author lancer
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class TestScriptFactory implements ScriptFactory {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(TestScriptFactory.class);

    /** Known test scripts. */
    private static final Map<String, Script> SCRIPTS;

    static {
        SCRIPTS = new java.util.HashMap<String, Script>();
        SCRIPTS.put("1", new Test1Script());
    }

    /**
     * Constructs a new object.
     */
    public TestScriptFactory() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Script create(final String id) {
        final Script script = SCRIPTS.get(id);
        if (script == null) {
            return new DefaultScript();
        }
        return script;
    }
}
