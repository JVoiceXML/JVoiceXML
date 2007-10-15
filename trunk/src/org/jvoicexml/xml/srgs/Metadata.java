/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/xml/vxml/Metadata.java $
 * Version: $LastChangedRevision: 465 $
 * Date:    $Date: 2007-10-05 09:22:00 +0200 (Fri, 05 Oct 2007) $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.srgs;

import org.jvoicexml.xml.XmlNode;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Define metadata information using a metadata schema.
 * <p>
 * The <code>&lt;metadata&gt;</code> element is container in which information
 * about the document can be placed using a metadata schema. Although any
 * metadata schema can be used with <code>&lt;metadata&gt;</code>, it is
 * recommended that the RDF schema is used in conjunction with metadata
 * properties defined in the Dublin Core Metadata Initiative.
 * </p>
 *
 * @see org.jvoicexml.xml.vxml.Form
 *
 * @author Dirk Schnelle
 * @version $Revision: 465 $
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class Metadata
        extends AbstractSrgsNode {

    /** Name of the tag. */
    public static final String TAG_NAME = "metadata";

    /**
     * Construct a new metadata object without a node.
     * <p>
     * This is necessary for the node factory.
     * </p>
     *
     * @see org.jvoicexml.xml.vxml.VoiceXmlNodeFactory
     */
    public Metadata() {
        super(null);
    }

    /**
     * Construct a new metadata object.
     * @param node The encapsulated node.
     */
    Metadata(final Node node) {
        super(node);
    }

    /**
     * Constructs a new node.
     *
     * @param n
     *            The encapsulated node.
     * @param factory
     *            The node factory to use.
     */
    private Metadata(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        super(n, factory);
    }

    /**
     * Get the name of the tag for the derived node.
     *
     * @return name of the tag.
     */
    public String getTagName() {
        return TAG_NAME;
    }

    /**
     * {@inheritDoc}
     */
    public XmlNode newInstance(final Node n,
            final XmlNodeFactory<? extends XmlNode> factory) {
        return new Metadata(n, factory);
    }

    /**
     * {@inheritDoc}
     */
    protected boolean canContainChild(final String tagName) {
        // Metadata node can contain any element.
        return true;
    }
}
