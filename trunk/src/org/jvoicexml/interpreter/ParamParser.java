/*
 * File:    $RCSfile: ParamParser.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.Collection;
import java.util.Map;

import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.vxml.Param;

/**
 * The <code>&lt;param&gt;</code> element is used to specify values that are
 * passed to subdialogs or objects.
 *
 * @see org.jvoicexml.xml.vxml.Param
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 *
 * @since 0.5
 */
class ParamParser {
    /** The node containing param tags. */
    private final VoiceXmlNode node;

    /**
     * Constructs a new object.
     * @param vxml
     *        The node to parse.
     */
    public ParamParser(final VoiceXmlNode vxml) {
        node = vxml;
    }

    /**
     * Retrieve all parameters defined in the current tag.
     * @return Mapping of parameter names to their values.
     */
    public Map<String, String> getParameters() {
        final Collection<Param> paramtags = node.getChildNodes(Param.class);

        final Map<String, String> parameters =
                new java.util.HashMap<String, String>();

        /** @todo evaluate ref params. */
        for (Param param : paramtags) {
            final String name = param.getName();
            final String value = param.getValue();

            parameters.put(name, value);
        }

        return parameters;
    }
}
