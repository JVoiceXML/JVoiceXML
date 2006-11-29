/*
 * File:    $RCSfile: AnonymousField.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.form;

import org.jvoicexml.xml.vxml.Field;
import org.w3c.dom.Node;

/**
 * A <code>&lt;menu&gt;</code> is a convenient syntactic shorthand for a form
 * containing a single anonymous field that prompts the user to make a choice
 * and transitions to different places based on that choice.
 *
 * <p>
 * This is the implementation of the anonyous field, which does
 * not exist in the VoiceXML document.
 * </p>
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
class AnonymousField
        extends Field {
    /**
     * Constructs a new object.
     * @param node The node containing this field.
     */
    public AnonymousField(final Node node) {
        super(node);
    }
}
