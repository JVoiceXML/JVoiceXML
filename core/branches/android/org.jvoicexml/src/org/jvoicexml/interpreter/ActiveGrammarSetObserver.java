/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/ActiveGrammarSetObserver.java $
 * Version: $LastChangedRevision: 2592 $
 * Date:    $Date: 2011-02-17 22:01:47 -0600 (jue, 17 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package org.jvoicexml.interpreter;

import java.util.Collection;

import org.jvoicexml.GrammarDocument;

/**
 * An observer for changes in the {@link ActiveGrammarSet}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2592 $
 * @since 0.7.3
 */
public interface ActiveGrammarSetObserver {
    /**
     * The given set changed due to a scope change.
     * @param set the changed set
     * @param removed the removed items
     */
    void removedGrammars(final ActiveGrammarSet set,
            final Collection<GrammarDocument> removed);
}
