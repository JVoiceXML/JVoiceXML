/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/unittests/src/org/jvoicexml/test/mock/scope/MockScopedSetObserver.java $
 * Version: $LastChangedRevision: 3659 $
 * Date:    $Date: 2013-03-01 15:33:27 +0100 (Fri, 01 Mar 2013) $
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
package org.jvoicexml.test.mock.scope;

import java.util.Collection;

import org.jvoicexml.interpreter.scope.ScopedSet;
import org.jvoicexml.interpreter.scope.ScopedSetObserver;

/**
 * Dummy implementation of a {@link ScopedSetObserver}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 3659 $
 * @since 0.7.3
 */
public final class MockScopedSetObserver implements ScopedSetObserver<Object> {
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
