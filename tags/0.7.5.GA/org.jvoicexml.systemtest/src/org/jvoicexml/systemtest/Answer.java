/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2010 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jvoicexml.systemtest;

/**
 * An answer to a system output.
 * 
 * @author lancer
 * @author Dirk Schnelle-Walka
 * @version $Revision$
 */
public final class Answer {
    /** The text to answer. */
    private final String answer;

    /**
     * Constructs a new object.
     * @param input the text to answer.
     */
    public Answer(final String input) {
        answer = input;
    }

    /**
     * Retrieves the text to answer.
     * @return the text to answer.
     */
    public String getAnswer() {
        return answer;
    }
}
