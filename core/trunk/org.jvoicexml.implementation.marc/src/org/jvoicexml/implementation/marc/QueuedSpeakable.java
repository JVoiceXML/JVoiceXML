/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.implementation.marc/src/org/jvoicexml/implementation/marc/MarcSynthesizedOutput.java $
 * Version: $LastChangedRevision: 2742 $
 * Date:    $Date: 2011-06-29 13:33:07 +0200 (Mi, 29 Jun 2011) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 * The JVoiceXML group hereby disclaims all copyright interest in the
 * library `JVoiceXML' (a free VoiceXML implementation).
 * JVoiceXML group, $Date: 2011-06-29 13:33:07 +0200 (Mi, 29 Jun 2011) $, Dirk Schnelle-Walka, project lead
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
package org.jvoicexml.implementation.marc;

import org.jvoicexml.SpeakableText;

/**
 * A queued speakable with the {@link SpeakableQueue}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2738 $
 * @since 0.7.5
 *
 */
class QueuedSpeakable {
    /** A unique identifier of the speakable. */
    private final String id;

    /** The queued speakable. */
    private final SpeakableText speakable;

    /**
     * Constructs a new object.
     * @param identifier a unique identifier for the speakable
     * @param speakableText the speakable
     */
    public QueuedSpeakable(final String identifier,
            final SpeakableText speakableText) {
        id = identifier;
        speakable = speakableText;
    }

    /**
     * Retrieves the identifier for this speakable.
     * @return the identifier for the speakable
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the speakable.
     * @return the speakable
     */
    public SpeakableText getSpeakable() {
        return speakable;
    }
}
