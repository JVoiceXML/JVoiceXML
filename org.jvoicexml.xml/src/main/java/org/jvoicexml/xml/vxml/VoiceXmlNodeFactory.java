/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2019 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.xml.vxml;

import java.util.Map;
import java.util.logging.Logger;

import org.jvoicexml.xml.Text;
import org.jvoicexml.xml.VoiceXmlNode;
import org.jvoicexml.xml.XmlCDataSection;
import org.jvoicexml.xml.XmlNodeFactory;
import org.jvoicexml.xml.srgs.Example;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Item;
import org.jvoicexml.xml.srgs.OneOf;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.Ruleref;
import org.jvoicexml.xml.srgs.Tag;
import org.jvoicexml.xml.srgs.Token;
import org.jvoicexml.xml.ssml.Audio;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.Desc;
import org.jvoicexml.xml.ssml.Emphasis;
import org.jvoicexml.xml.ssml.Lexicon;
import org.jvoicexml.xml.ssml.Mark;
import org.jvoicexml.xml.ssml.P;
import org.jvoicexml.xml.ssml.Phoneme;
import org.jvoicexml.xml.ssml.Prosody;
import org.jvoicexml.xml.ssml.S;
import org.jvoicexml.xml.ssml.SayAs;
import org.jvoicexml.xml.ssml.Sub;
import org.jvoicexml.xml.ssml.Voice;
import org.w3c.dom.Node;

/**
 * Factory for VoiceXmlNodes.
 *
 * @author Dirk Schnelle-Walka
 * @author Steve Doyle
 */
final class VoiceXmlNodeFactory
        implements XmlNodeFactory<VoiceXmlNode> {
    /** Logger instance for this class. */
    private static final Logger LOGGER =
        Logger.getLogger(VoiceXmlNodeFactory.class.getCanonicalName());

    /**
     * Known nodes. <br>
     * Each node can be retrieved via its tag name.
     */
    private static final Map<String, VoiceXmlNode> NODES;

    static {
        NODES = new java.util.HashMap<String, VoiceXmlNode>();

        // VoiceXML Tags
        NODES.put(Assign.TAG_NAME, new Assign());
        NODES.put(Block.TAG_NAME, new Block());
        NODES.put(Catch.TAG_NAME, new Catch());
        NODES.put(Choice.TAG_NAME, new Choice());
        NODES.put(Clear.TAG_NAME, new Clear());
        // TODO find a way to make this configurable
        NODES.put(Data.TAG_NAME, new JVoiceXmlData());
        NODES.put(Disconnect.TAG_NAME, new Disconnect());
        NODES.put(Else.TAG_NAME, new Else());
        NODES.put(Elseif.TAG_NAME, new Elseif());
        NODES.put(Enumerate.TAG_NAME, new Enumerate());
        NODES.put(org.jvoicexml.xml.vxml.Error.TAG_NAME,
                  new org.jvoicexml.xml.vxml.Error());
        NODES.put(Exit.TAG_NAME, new Exit());
        NODES.put(Field.TAG_NAME, new Field());
        NODES.put(Filled.TAG_NAME, new Filled());
        NODES.put(Form.TAG_NAME, new Form());
        NODES.put(Foreach.TAG_NAME, new Foreach());
        NODES.put(Goto.TAG_NAME, new Goto());
        NODES.put(Grammar.TAG_NAME, new Grammar());
        NODES.put(Help.TAG_NAME, new Help());
        NODES.put(If.TAG_NAME, new If());
        NODES.put(Initial.TAG_NAME, new Initial());
        NODES.put(Link.TAG_NAME, new Link());
        NODES.put(Log.TAG_NAME, new Log());
        NODES.put(Menu.TAG_NAME, new Menu());
        NODES.put(Meta.TAG_NAME, new Meta());
        NODES.put(Metadata.TAG_NAME, new Metadata());
        NODES.put(Noinput.TAG_NAME, new Noinput());
        NODES.put(Nomatch.TAG_NAME, new Nomatch());
        NODES.put(ObjectTag.TAG_NAME, new ObjectTag());
        NODES.put(Option.TAG_NAME, new Option());
        NODES.put(Param.TAG_NAME, new Param());
        // TODO find a way to make this configurable
        NODES.put(Prompt.TAG_NAME, new JVoiceXmlPrompt());
        NODES.put(Property.TAG_NAME, new Property());
        NODES.put(Record.TAG_NAME, new Record());
        NODES.put(Reprompt.TAG_NAME, new Reprompt());
        NODES.put(Return.TAG_NAME, new Return());
        NODES.put(Script.TAG_NAME, new Script());
        NODES.put(Subdialog.TAG_NAME, new Subdialog());
        NODES.put(Submit.TAG_NAME, new Submit());
        NODES.put(Throw.TAG_NAME, new Throw());
        NODES.put(Transfer.TAG_NAME, new Transfer());
        NODES.put(Value.TAG_NAME, new Value());
        NODES.put(Var.TAG_NAME, new Var());
        NODES.put(Vxml.TAG_NAME, new Vxml());
        // SSML Tags
        NODES.put(Audio.TAG_NAME, new Audio());
        NODES.put(Break.TAG_NAME, new Break());
        NODES.put(Desc.TAG_NAME, new Desc());
        NODES.put(Emphasis.TAG_NAME, new Emphasis());
        NODES.put(Lexicon.TAG_NAME, new Lexicon());
        NODES.put(Mark.TAG_NAME, new Mark());
        NODES.put(P.TAG_NAME, new P());
        NODES.put(Phoneme.TAG_NAME, new Phoneme());
        NODES.put(Prosody.TAG_NAME, new Prosody());
        NODES.put(S.TAG_NAME, new S());
        NODES.put(SayAs.TAG_NAME, new SayAs());
        NODES.put(Sub.TAG_NAME, new Sub());
        NODES.put(Voice.TAG_NAME, new Voice());
        // SRGS Tags
        NODES.put(Example.TAG_NAME, new Example());
        NODES.put(Item.TAG_NAME, new Item());
        NODES.put(OneOf.TAG_NAME, new OneOf());
        NODES.put(Rule.TAG_NAME, new Rule());
        NODES.put(Ruleref.TAG_NAME, new Ruleref());
        NODES.put(Tag.TAG_NAME, new Tag());
        NODES.put(Token.TAG_NAME, new Token());
    }

    /**
     * Constructs a new object.
     */
    VoiceXmlNodeFactory() {
        // general tags
        NODES.put(Text.TAG_NAME, new Text(null, this));
        NODES.put(XmlCDataSection.TAG_NAME, new XmlCDataSection(null, this));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VoiceXmlNode getXmlNode(final Node node) {
        // Do nothing if the node is null
        if (node == null) {
            return null;
        }

        // Do nothing if we already have the right type.
        if (node instanceof VoiceXmlNode) {
            return (VoiceXmlNode) node;
        }

        String name = node.getLocalName();
        if (name == null) {
            name = node.getNodeName();
        }
        final VoiceXmlNode voiceXmlNode = NODES.get(name);
        if (voiceXmlNode == null) {
            LOGGER.warning("cannot resolve node with name '" + name + "'");

            return new GenericVoiceXmlNode(node);
        }

        return (VoiceXmlNode) voiceXmlNode.newInstance(node, this);
    }
}
