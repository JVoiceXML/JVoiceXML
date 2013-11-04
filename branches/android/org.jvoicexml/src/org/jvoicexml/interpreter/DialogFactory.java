/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/DialogFactory.java $
 * Version: $LastChangedRevision: 2612 $
 * Date:    $Date $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import org.jvoicexml.xml.vxml.Vxml;

/**
 * Factory for {@link Dialog} objects.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2612 $
 * @since 0.4
 */
public interface DialogFactory {
    /**
     * Retrieves a collection of all {@link Dialog}s in the
     * given VoiceXML document with the help of the given configuration.
     * @param vxml the document.
     * @return collection of all  {@link Dialog}s.
     */
    Collection<Dialog> getDialogs(final Vxml vxml);
}
