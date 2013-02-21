/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/vxml/VoiceXml20DocumentType.java $
 * Version: $LastChangedRevision: 2476 $
 * Date:    $Date: 2010-12-23 05:36:01 -0600 (jue, 23 dic 2010) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.vxml;

import java.io.Serializable;

import org.jvoicexml.xml.AbstractXmlDocumentType;
import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

/**
 * The <code>DOCTYPE</code> of a VoiceXML 2.0 document.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2476 $
 */
public final class VoiceXml20DocumentType
        extends AbstractXmlDocumentType implements DocumentType, Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 6186349573029773262L;

    /**
     * Construct a new object.
     */
    public VoiceXml20DocumentType() {
        super(Vxml.TAG_NAME, "-//W3C//DTD VOICEXML 2.0//EN",
            "http://www.w3.org/TR/voicexml20/vxml.dtd");
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new VoiceXml20DocumentType();
    }
}
