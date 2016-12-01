/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2010 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * A listener for scope changes.
 *
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 *
 * @since 0.3
 */
public interface ScopeSubscriber {
    /**
     * The application has entered the <code>next</code> scope and the
     * <code>previous</code> scope is no more the current scope.
     * @param previous The old scope.
     * @param next The new scope.
     */
    void enterScope(final Scope previous, final Scope next);

    /**
     * The application has left the <code>previous</code> scope and the
     * <code>next</code> scope is valid.
     * @param previous The old scope.
     * @param next The new scope.
     */
    void exitScope(final Scope previous, final Scope next);
}
