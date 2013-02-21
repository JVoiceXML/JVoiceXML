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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.jvoicexml.systemtest.ScriptFactory;

/**
 * Factory for input scripts.
 * 
 * @author lancer
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public class InputScriptFactory implements ScriptFactory {
    /** Logger for this class. */
    static final Logger LOGGER = Logger.getLogger(InputScriptFactory.class);
    static boolean useDefaultScript = true;

    File home;

    final String suffix = ".script.xml";

    public InputScriptFactory() {

    }

    public InputScriptFactory(String dir) {
        home = new File(dir);
    }

    public InputScript create(String id) {
        InputScript script ;

        File scriptFile = new File(home, id + suffix);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("file path = " + scriptFile.getAbsolutePath());
            LOGGER.debug("exists : " + scriptFile.exists());
        }
        if (scriptFile.exists()) {
            try {
                final InputStream in = new FileInputStream(scriptFile);
                script = create(in, id);
            } catch (FileNotFoundException e) {
                script = null;
            }
        } else if (useDefaultScript) {
            script = createDefault(id);
        } else {
            script = null;
        }

        return script;
    }

    InputScript create(final InputStream is, final String id) {
        InputScript script = new InputScript(id);

        Class[] names = new Class[4];
        int i = 0;
        names[i++] = ScriptDocNode.class;
        names[i++] = SpeakAction.class;
        names[i++] = NoInputAction.class;
        names[i++] = DTMFAction.class;

        ScriptDocNode root = loadObject(ScriptDocNode.class, names, is);

        for (Action action : root.action) {
            script.append(action);
        }

        return script;
    }

    /**
     * Creates a default script.
     * @param id id of the script
     * @return default script
     */
    InputScript createDefault(final String id) {
        final InputScript script = new InputScript(id);
        script.append(new GuessAnswerAction());
        return script;
    }

    public void setScriptsDirectory(String dir) {
        home = new File(dir);
    }

    /**
     * Load XML from InputStream
     * 
     * @param source
     * @return
     */
    private <T extends Object> T loadObject(Class<T> clazz,
            Class[] classNames, final InputStream source) {

        Map<String, Object> prep = new HashMap<String, Object>();
        
        try {

            JAXBContext jc = JAXBContext.newInstance(classNames, prep);
            Unmarshaller um = jc.createUnmarshaller();

            if (LOGGER.isDebugEnabled()) {
                um.setListener(new Unmarshaller.Listener() {
                    @Override
                    public void afterUnmarshal(Object arg0, Object arg1) {
                        super.afterUnmarshal(arg0, arg1);
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                    }

                    @Override
                    public void beforeUnmarshal(Object arg0, Object arg1) {
                        LOGGER.debug("Object1 : " + arg0);
                        LOGGER.debug("Object2 : " + arg1);
                        super.beforeUnmarshal(arg0, arg1);
                    }
                });
            }

            return clazz.cast(um.unmarshal(new InputStreamReader(source)));

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @XmlRootElement(name = "scriptDoc")
    static class ScriptDocNode {

        /* IR test id. */
        @XmlAttribute
        public String id;

        /** actions. */
        @XmlElementRef(type = org.jvoicexml.systemtest.script.Action.class)
        List<Action> action = new LinkedList<Action>();


    }
}
