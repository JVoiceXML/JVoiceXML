/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/documentserver/schemestrategy/ScriptableObjectSerializer.java $
 * Version: $LastChangedRevision: 2674 $
 * Date:    $Date: 2011-05-24 03:58:04 -0500 (mar, 24 may 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.documentserver.schemestrategy;

import java.util.Collection;

import org.apache.http.NameValuePair;
import org.jvoicexml.event.error.SemanticError;
import org.mozilla.javascript.ScriptableObject;

/**
 * Serializer for compound objects when submitting. The VoiceXML specification
 * leaves it open how and if compound objects are to be submitted
 * (cf. <a href="http://www.w3.org/TR/voicexml20#dml5.3.8">
 * http://www.w3.org/TR/voicexml20#dml5.3.8</a>). This interface defines
 * the behaviour of JVoiceXML in this case.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2674 $
 * @since 0.7.5
 */
public interface ScriptableObjectSerializer {
    /**
     * Serializes the given {@link ScriptableObject}.
     * @param name the name of the object to serialize
     * @param object the object to serialize.
     * @return the serialized object.
     * @throws SemanticError
     *         error serializing the given object.
     * @since 0.7.5
     */
    Collection<NameValuePair> serialize(final String name,
            final ScriptableObject object) throws SemanticError;
}
