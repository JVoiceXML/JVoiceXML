/*
 * File:    $HeadURL: https://svn.code.sf.net/p/jvoicexml/code/trunk/org.jvoicexml/src/org/jvoicexml/interpreter/tagstrategy/TagType.java $
 * Version: $LastChangedRevision: 4080 $
 * Date:    $Date: 2013-12-17 09:46:17 +0100 (Tue, 17 Dec 2013) $
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
package org.jvoicexml.profile.vxml21.tagstrategy;

/**
 * Type of the {@link org.jvoicexml.interpreter.TagStrategyFactory}.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4080 $
 * @since 0.7.4
 */
public enum TagType {
    /**
     * The {@link org.jvoicexml.interpreter.TagStrategy}
     * of this {@link org.jvoicexml.interpreter.TagStrategyFactory}
     * can only be used in the initializing phase of the doucment.
     */
    INITIAL,

    /**
     * The {@link org.jvoicexml.interpreter.TagStrategy}
     * of this {@link org.jvoicexml.interpreter.TagStrategyFactory}
     * describe executable content. 
     */
    EXECUTABLE,

    /**
     * The {@link org.jvoicexml.interpreter.TagStrategy}
     * of this {@link org.jvoicexml.interpreter.TagStrategyFactory}
     * collect input from the user. 
     */
    INPUT;
}
