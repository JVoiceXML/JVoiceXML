/*
 * File:    $RCSfile: XmlStringWriter.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2006/05/17 08:20:22 $
 * Author:  $Author: schnelle $
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

package org.jvoicexml.xml;

import java.io.StringWriter;

/**
 * XmlWriter that uses a string writer to write output.
 * <p>Title: JVoceXML</p>
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class XmlStringWriter
        extends XmlWriter {

    /**
     * Constructs an xml writer using a <code>java.io.StringWriter</code> that
     * doesn't pretty-print output.
     *
     * @see java.io.StringWriter
     */
    public XmlStringWriter() {
        super(new StringWriter());
    }

    /**
     * Constructs an xml writer using a <code>java.io.StringWriter</code> that
     * supports pretty-printing output with the given number of spaces for
     * block indentation.
     * @param blockSpaces Number of spaces for blocks, for
     *              use in pretty printing XML text.
     * @see java.io.StringWriter
     */
    public XmlStringWriter(final int blockSpaces) {
        super(new StringWriter(), blockSpaces);
    }

    /**
     * Returns the string writer's buffer current value as a string. This
     * is in general the current XML document.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        final StringWriter writer = (StringWriter) getWriter();
        return writer.toString();
    }

}
