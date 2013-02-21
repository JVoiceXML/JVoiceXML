/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2009 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.processor.vxml3.module;

import org.jvoicexml.processor.vxml3.resources.ResouceController;

/**
 * The grammar RC is the primary RC for the <code><grammar></code> element.
 * @author Dirk Schnelle-Walka
 * @version 1.0
 * @updated 07-Jun-2011 08:39:04
 */
public interface GrammarResourceController extends ResouceController {

    /**
     * Causes the element and its children to be initialized.
     *
     * @param controller the controller
     */
    void initialize(Object controller);

    /**
     * Adds the grammar to the appropriate Recognition Resource.
     */
    void execute();
}
