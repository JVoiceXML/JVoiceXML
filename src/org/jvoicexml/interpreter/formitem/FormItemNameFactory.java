/*
 * File:    $RCSfile: FormItemNameFactory.java,v $
 * Version: $Revision: 1.9 $
 * Date:    $Date: 2006/05/17 08:20:22 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.formitem;

import org.jvoicexml.xml.VoiceXmlNode;

/**
 * Name factory to find a name for a form item.
 *
 * @see org.jvoicexml.interpreter.FormItem
 *
 * @author Dirk Schnelle
 * @version $Revision: 1.9 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public final class FormItemNameFactory {
    /** The name attribute. */
    private static final String ATTRIBUTE_NAME = "name";

    /**
     * A sequence number to distingush multiple form items that are created
     * within a single millisecond.
     */
    private static long sequence = 0;

    /**
     * Do not create from outside.
     */
    private FormItemNameFactory() {
    }

    /**
     * Get a name for the given node using the <code>ATTRIBUTE_NAME</code>
     * attribute. If the node is nameless an internal name is generated
     * like this:
     *
     * <p>
     * <code>
     * F&lt;Long.toHexString(System.currentTimeMillis())
     * &gt;S&lt;6-digit sequence number&gt;
     * </code>
     * </p>
     *
     *
     * @param node
     *        VoiceXmlNode
     * @return Name for the node.
     * @see #ATTRIBUTE_NAME
     */
    public static synchronized String getName(final VoiceXmlNode node) {
        // Check if the node's name attribute is set.
        final String name = node.getAttribute(ATTRIBUTE_NAME);
        if (name != null) {
            return name;
        }

        // Simple algorithm to get an internal name.
        ++sequence;

        final String leadingZeros = "000000";

        String sequenceString = leadingZeros + Long.toHexString(sequence);
        sequenceString = sequenceString.substring(sequenceString.length()
                - leadingZeros.length());

        return "F" + Long.toHexString(System.currentTimeMillis()) + "S"
                + sequenceString;

    }
}
