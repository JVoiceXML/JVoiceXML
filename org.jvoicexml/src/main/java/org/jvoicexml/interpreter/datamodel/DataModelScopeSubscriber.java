/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2014-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.datamodel;

import org.apache.log4j.Logger;
import org.jvoicexml.interpreter.scope.Scope;
import org.jvoicexml.interpreter.scope.ScopeSubscriber;

/**
 * The hook to scope changes of the voice browser that effect the scope chain in
 * the {@link DataModel}.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.7
 */
public class DataModelScopeSubscriber implements ScopeSubscriber {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(DataModelScopeSubscriber.class);

    /** The datamodel. */
    private final DataModel model;

    /**
     * Constructs a new object.
     * 
     * @param dataModel
     *            the data model
     */
    public DataModelScopeSubscriber(final DataModel dataModel) {
        model = dataModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterScope(final Scope previous, final Scope next) {
        final int rc = model.createScope(next);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error entering scope '" + previous + "' to '"
                    + next + "'");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitScope(final Scope previous, final Scope next) {
        final int rc = model.deleteScope(previous);
        if (rc != DataModel.NO_ERROR) {
            LOGGER.warn("error exiting scope '" + previous + "' to '"
                    + next + "'");
        }
    }
}
