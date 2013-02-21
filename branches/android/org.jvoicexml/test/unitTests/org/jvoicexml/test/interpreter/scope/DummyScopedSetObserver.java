/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/test/unitTests/org/jvoicexml/test/interpreter/scope/DummyScopedSetObserver.java $
 * Version: $LastChangedRevision: 2153 $
 * Date:    $Date: 2010-04-14 02:25:59 -0500 (mié, 14 abr 2010) $
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
package org.jvoicexml.test.interpreter.scope;

import java.util.Collection;

import org.jvoicexml.interpreter.scope.ScopedSet;
import org.jvoicexml.interpreter.scope.ScopedSetObserver;

/**
 * Dummy implementation of a {@link ScopedSetObserver}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2153 $
 * @since 0.7.3
 */
public final class DummyScopedSetObserver implements ScopedSetObserver<Object> {
    /** The last removed items from a {@link ScopedSet}. */
    private Collection<?> lastRemoved;

    /**
     * Retrieves the last removed items.
     * @return the last removed items
     */
    public Collection<?> getLastRemoved() {
        return lastRemoved;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scopedSetChange(final ScopedSet<Object> set,
            final Collection<Object> removed) {
        lastRemoved = removed;
    }

}
