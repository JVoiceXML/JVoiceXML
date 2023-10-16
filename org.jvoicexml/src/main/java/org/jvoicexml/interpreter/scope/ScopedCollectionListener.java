/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.interpreter.scope;

import java.util.Collection;

/**
 * A listener to changes in a {@link ScopedCollection} because of scope
 * changes.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 * @param <E> Type of the elements in the scoped collection.
 */
public interface ScopedCollectionListener<E> {
    /**
     * Notification that the provided {@code items} have been removed because of
     * a scope change from {@code previous} to {@code next}.
     * @param previous the previous scope
     * @param next the new scope
     * @param items removed items
     * @since 0.7.9
     */
    void removedForScopeChange(Scope previous, Scope next,
            Collection<E> items);
}
