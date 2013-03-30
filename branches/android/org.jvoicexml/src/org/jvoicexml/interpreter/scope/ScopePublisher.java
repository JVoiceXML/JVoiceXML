/*
 * File:    $RCSfile: ScopePublisher.java,v $
 * Version: $Revision: 2129 $
 * Date:    $Date: 2010-04-09 04:33:10 -0500 (vie, 09 abr 2010) $
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

package org.jvoicexml.interpreter.scope;

/**
 * A multiplier for scope changes.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2129 $
 *
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public interface ScopePublisher {
    /**
     * Add the given subscriber to the list of known
     * <code>ScopeSubscriber</code>s.
     * @param subscriber The subscriber to add.
     */
    void addScopeSubscriber(final ScopeSubscriber subscriber);

    /**
     * Remove the given subscriber from the list of known
     * <code>ScopeSubscriber</code>s.
     * @param subscriber The subscriber to remove.
     */
    void removeScopeSubscriber(final ScopeSubscriber subscriber);
}
