/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.resources;

import java.util.Map;

/**
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 * @created 02-Jan-2009 18:31:50
 */
public interface RecognitionResource extends Resource {

    /**
     * creates a grammar item composed of the grammar, listener and properties,
     * and adds it to the activeGrammars.
     *
     * @param grammar grammar to add
     * @param properties properties
     */
    void addGrammar(Object grammar, Map<?, ?> properties);

    /**
     * prepares the device for recognition using activeGrammars and properties.
     *
     * @param grammar grammar to prepare
     * @param properties properties
     */
    void prepare(Object grammar, Map<?, ?> properties);

    /**
     * initiates/resumes recognition.
     */
    void listen();

    /**
     * suspends recognition.
     */
    void suspend();

    /**
     * terminates recognition.
     */
    void stop();
}
