/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/JVoiceXmlContextFactory.java $
 * Version: $LastChangedRevision: 2579 $
 * Date:    $Date: 2011-02-14 07:14:36 -0600 (lun, 14 feb 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2011-02-14 07:14:36 -0600 (lun, 14 feb 2011) $, Dirk Schnelle-Walka, project lead
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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * A context factory for the {@link ScriptingEngine}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2579 $
 * @since 0.7.5
 */
class JVoiceXmlContextFactory extends ContextFactory {

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
