/*
 * File:    $RCSfile: ExternalGrammar.java,v $
 * Version: $Revision: 1.7 $
 * Date:    $Date: 2005/12/12 08:20:28 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar;

/**
 * The <code>ExternalGrammar</code> interface defines a container
 * for any kind of external grammar.
 *
 * <p>
 * It holds the content of the grammar as well as the media type.
 * </p>
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 *
 * @version $Revision: 1.7 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public interface ExternalGrammar {
    /**
     * Returns the declared media type of the external grammar.
     *
     * @return The media type of the grammar file.
     */
    String getMediaType();

    /**
     * Sets the media type to the given <code>type</code>.
     * @param type The new media type.
     * @since 0.3
     */
    void setMediaType(final String type);

    /**
     * Returns the lexical content of the external grammar.
     *
     * @return The content of the grammar file.
     */
    String getContents();
}
