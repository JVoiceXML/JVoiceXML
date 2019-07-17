/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.srgs;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * A context factory for the {@link ScriptingEngine}. The VoiceXML specification
 * requires the activation of certain features, like strict variable
 * declarations. This implementation takes care that they are set properly.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
class SrgsContextFactory extends ContextFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasFeature(final Context cx, final int featureIndex) {
        switch (featureIndex) {
        case Context.FEATURE_STRICT_MODE:
            return true;
        case Context.FEATURE_STRICT_VARS:
            return true;
        case Context.FEATURE_STRICT_EVAL:
            return true;
        case Context.FEATURE_WARNING_AS_ERROR:
            return true;
        default:
            return super.hasFeature(cx, featureIndex);
        }
    }
}
