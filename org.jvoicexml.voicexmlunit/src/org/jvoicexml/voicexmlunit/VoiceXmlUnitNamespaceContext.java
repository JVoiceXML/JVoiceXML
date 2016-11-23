/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.voicexmlunit/src/org/jvoicexml/voicexmlunit/TextCall.java $
 * Version: $LastChangedRevision: 4416 $
 * Date:    $Date: 2014-11-24 10:18:01 +0100 (Mon, 24 Nov 2014) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.voicexmlunit;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * Own mapping of namespaces to URI to be used in {@link XPathAssert}s, e.g.
 * to resolve the SSML namespace.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4120 $
 * @since 0.7.6
 */
public final class VoiceXmlUnitNamespaceContect implements NamespaceContext {
    /** Known mappings of namespace prefixes to namespace URIs. */
    private final Map<String, String> prefixes;
    
    /**
     * Constructs a new object.
     */
    public VoiceXmlUnitNamespaceContect() {
        prefixes = new java.util.HashMap<String, String>();
    }

    /**
     * Adds the given prefix and namespace URI to the list of known prefixes.
     * @param prefix the namespace prefix
     * @param namespaceUri the related URI
     */
    public void addPrefix(final String prefix, final String namespaceUri) {
        prefixes.put(prefix, namespaceUri);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getNamespaceURI(final String prefix) {
        return prefixes.get(prefix);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefix(final String namespaceURI) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Iterator getPrefixes(final String namespaceURI) {
        return null;
    }

}
