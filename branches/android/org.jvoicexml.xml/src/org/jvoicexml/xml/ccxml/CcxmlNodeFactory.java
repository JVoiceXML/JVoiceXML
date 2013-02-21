/*
 * File:    $HeadURL: https://jvoicexml.svn.sourceforge.net/svnroot/jvoicexml/core/trunk/org.jvoicexml.xml/src/org/jvoicexml/xml/ccxml/CcxmlNodeFactory.java $
 * Version: $LastChangedRevision: 2810 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 *
 */

package org.jvoicexml.xml.ccxml;

import java.util.Map;
import java.util.logging.Logger;

import org.jvoicexml.xml.CcxmlNode;
import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.XmlNodeFactory;
import org.w3c.dom.Node;

/**
 * Factory for CcxmlNodes.
 *
 * @author Dirk Schnelle-Walka
 * @author Steve Doyle
 * @version $Revision: 2810 $
 */
final class CcxmlNodeFactory
        implements XmlNodeFactory<CcxmlNode> {
    /** Logger instance for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(CcxmlNodeFactory.class.getCanonicalName());

    /**
     * Known nodes. <br>
     * Each nodecan be retrieved via it's tag name.
     */
    private static final Map<String, CcxmlNode> NODES;

    static {
        NODES = new java.util.HashMap<String, CcxmlNode>();

        // Ccxml Tags
        NODES.put(Accept.TAG_NAME, new Accept());
        NODES.put(Assign.TAG_NAME, new Assign());
        NODES.put(Cancel.TAG_NAME, new Cancel());
        NODES.put(Ccxml.TAG_NAME, new Ccxml());
        NODES.put(Createcall.TAG_NAME, new Createcall());
        NODES.put(Createconference.TAG_NAME, new Createconference());
        NODES.put(Destroyconference.TAG_NAME, new Destroyconference());
        NODES.put(Dialogprepare.TAG_NAME, new Dialogprepare());
        NODES.put(Dialogstart.TAG_NAME, new Dialogstart());
        NODES.put(Dialogterminate.TAG_NAME, new Dialogterminate());
        NODES.put(Disconnect.TAG_NAME, new Disconnect());
        NODES.put(Else.TAG_NAME, new Else());
        NODES.put(Elseif.TAG_NAME, new Elseif());
        NODES.put(Eventprocessor.TAG_NAME, new Eventprocessor());
        NODES.put(Exit.TAG_NAME, new Exit());
        NODES.put(Fetch.TAG_NAME, new Fetch());
        NODES.put(Goto.TAG_NAME, new Goto());
        NODES.put(If.TAG_NAME, new If());
        NODES.put(Join.TAG_NAME, new Join());
        NODES.put(Log.TAG_NAME, new Log());
        NODES.put(Merge.TAG_NAME, new Merge());
        NODES.put(Meta.TAG_NAME, new Meta());
        NODES.put(Metadata.TAG_NAME, new Metadata());
        NODES.put(Move.TAG_NAME, new Move());
        NODES.put(Redirect.TAG_NAME, new Redirect());
        NODES.put(Reject.TAG_NAME, new Reject());
        NODES.put(Script.TAG_NAME, new Script());
        NODES.put(Send.TAG_NAME, new Send());
        NODES.put(Transition.TAG_NAME, new Transition());
        NODES.put(Unjoin.TAG_NAME, new Unjoin());
        NODES.put(Var.TAG_NAME, new Var());
    }

    /**
     * Constructs a new object.
     */
    public CcxmlNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
    }

    /**
     * {@inheritDoc}
     */
    public CcxmlNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the righttype.
        if (node instanceof CcxmlNode) {
            return (CcxmlNode) node;
        }

        final String name = node.getNodeName();
        final CcxmlNode ccxmlNode = NODES.get(name);
        if (ccxmlNode == null) {
            LOGGER.warning("cannot resolve node with name '" + name + "'");

            return new GenericCcxmlNode(node);
        }

        return (CcxmlNode) ccxmlNode.newInstance(node, this);
    }
}
