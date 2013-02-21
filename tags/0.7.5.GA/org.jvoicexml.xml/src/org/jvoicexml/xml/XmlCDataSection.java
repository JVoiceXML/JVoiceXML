/*
 * File:    $RCSfile: XmlCDataSection.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml;

import org.w3c.dom.Node;


/**
 * Implementation of a CDataSection node.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006-2007 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class XmlCDataSection
        extends Text {
    /** Name of the cdata section tag. */
    public static final String TAG_NAME = "#cdata-section";

    /**
     * Constructs a new CDataSection object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.XmlNodeFactory
     */
    public XmlCDataSection() {
        super(null, null);
    }

    /**
     * Construct a new CDataSection object.
     * @param n The encapsulated node.
     * @param nodeFactory The node factory.
     */
    public XmlCDataSection(final Node n,
                           final XmlNodeFactory<? extends XmlNode>
                           nodeFactory) {
        super(n, nodeFactory);
    }

    /**
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlNode newInstance(final Node n,
            final XmlNodeFactory<?> factory) {
        return new XmlCDataSection(n, getNodeFactory());
    }
}
