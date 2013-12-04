/*
 * File:    $RCSfile: FormItemVisitable.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
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
 *
 */

package org.jvoicexml.interpreter;

import org.jvoicexml.event.JVoiceXMLEvent;

/**
 * A visitable form item that is visited by classes that implement the
 * <code>FormItemVisitor</code> interface.
 *
 * @see FormItemVisitor
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 */
public interface FormItemVisitable {
    /**
     * Execute the specific method of the visitor.
     * @param visitor The visitor to use.
     * @exception JVoiceXMLEvent
     *            Error or event visiting the form item.
     */
    void accept(final FormItemVisitor visitor)
            throws JVoiceXMLEvent;
}
