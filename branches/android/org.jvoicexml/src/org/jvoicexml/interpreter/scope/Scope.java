/*
 * File:    $RCSfile: Scope.java,v $
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
 * Scope.
 *
 * @author Dirk Schnelle
 * @version $Revision: 2129 $
 * @since 0.3
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public enum Scope {
    /**
     * Scope for a user's session.
     */
    SESSION("session"),
    /**
     * Scope that are children of the application root document's
     * <code>&lt;vxml&gt;</code> element. It is entered when the application
     * root document is loaded. It exists while the application root document
     * is loaded, is visible to the root document and any other loaded
     * application leaf document.
     */
    APPLICATION("application"),
    /**
     * Scope that is declared within document's <code>&lt;vxml&gt;</code>
     * element. It is initialized when the document is loaded. It exists while
     * the document is loaded. It is visible only within that document, unless
     * the document is an application root, in which case it is visible by leaf
     * documents through the application scope only.
     */
    DOCUMENT("document"),
    /**
     * Each dialog (<code>&lt;form&gt;</code> or <code>&lt;menu&gt;</code>) has
     * a dialog scope that exists while the user is visiting that dialog, and
     * which is visible to the elements of that dialog.
     */
    DIALOG("dialog"),
    /**
     * Each <code>&lt;block&gt;</code>, <code>&lt;filled&gt;</code>, and
     * <code>&lt;catch&gt;</code> element defines a new anonymous scope.
     */
    ANONYMOUS("anonymous");

    /** Name of the scope. */
    private final String name;

    /**
     * Do not create from outside.
     * @param scope Name of the scope.
     */
    private Scope(final String scope) {
        name = scope;
    }

    /**
     * Retrieve the scope's name.
     * @return Name of this scope.
     */
    public String getName() {
        return name;
    }
}
