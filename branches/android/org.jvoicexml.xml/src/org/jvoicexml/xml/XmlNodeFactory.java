/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/XmlNodeFactory.java $
 * Version: $LastChangedRevision: 3829 $
 * Date:    $Date: 2013-07-16 13:01:00 +0200 (Tue, 16 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import org.w3c.dom.Node;

/**
 * Factory for XmlNodes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3829 $
 * @since 0.5
 *
 * @param <T> Type of <code>XmlNode</code> to produce in this factory.
 */
public interface XmlNodeFactory<T extends XmlNode> {
    /**
     * Factory method to retrieve an <code>XmlNode</code>.
     *
     * @param node
     *        The node for which a voice XML node should be retrieved.
     * @return Instance of a <code>XmlNode</code>, or <code>null</code> if
     *         there is no known implementation.
     */
    T getXmlNode(final Node node);
}
