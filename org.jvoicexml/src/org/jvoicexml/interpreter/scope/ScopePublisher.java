/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/scope/ScopePublisher.java $
 * Version: $LastChangedRevision: 3839 $
 * Date:    $Date: 2013-07-17 09:37:34 +0200 (Wed, 17 Jul 2013) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2013 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A publisher to propagate {@link Scope} changes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3839 $
 *
 * @since 0.3
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
