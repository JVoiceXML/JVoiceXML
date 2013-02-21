/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 */

package org.jvoicexml.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamWriter;

/**
 * Objects that can write themselves as XML text do so using this
 * interface.
 *
 * @author David Brownell
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2008 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface XmlWritable {
    /**
     * This is the primary method used to write an object and
     * its children as XML text. Implementations with children
     * should use writeChildrenXml to write those children, to
     * allow selective overriding.
     * @param writer XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    void writeXml(final XMLStreamWriter writer) throws IOException;

    /**
     * Used to write any children of a node.
     * @param writer XMLWriter used when writing XML text.
     * @exception IOException
     *            Error in writing.
     */
    void writeChildrenXml(final XMLStreamWriter writer)
        throws IOException;
}
