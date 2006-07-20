/*
 * File:    $RCSfile: SrgsXmlGrammarIdentifier.java,v $
 * Version: $Revision: 1.5 $
 * Date:    $Date: 2006/07/17 14:13:36 $
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
package org.jvoicexml.interpreter.grammar.identifier;

import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.interpreter.grammar.GrammarIdentifier;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.VoiceXmlNode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements the GrammarIdentifier interface. An instance
 * of this class is able to identifym a SRGS grammar with XML format.
 * The mime type of the accepted grammar is <code>application/srgs+xml</code>.
 *
 * @author Christoph Buente
 *
 * @version $Revision: 1.5 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsXmlGrammarIdentifier
        implements GrammarIdentifier {

    /**
     * The supported type of this identifier.
     */
    private static final String TYPE = "application/srgs+xml";

    /**
     * The Logger for this class.
     */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SrgsXmlGrammarIdentifier.class);

    /**
     * {@inheritDoc}
     *
     * The rules for a legal xml srgs grammar are listed here:
     *
     * A legal stand-alone XML Form grammar document must have a
     * legal XML Prolog [XML 2.8].
     *
     * The XML prolog in an XML Form grammar comprises the XML
     * declaration and an optional DOCTYPE declaration referencing
     * the grammar DTD. It is followed by the root grammar
     * element. The XML prolog may also contain XML comments,
     * processor instructions and other content permitted by XML
     * in a prolog.
     *
     * The version number of the XML declaration indicates which
     * version of XML is being used. The version number of the
     * grammar element indicates which version of the grammar
     * specification is being used  "1.0" for this specification.
     * The grammar version is a required attribute.
     *
     * The grammar element must designate the grammar namespace.
     * This can be achieved by declaring an xmlns attribute or an
     * attribute with an "xmlns" prefix. See [XMLNS] for details.
     * Note that when the xmlns attribute is used alone, it sets
     * the default namespace for the element on which it appears
     * and for any child elements. The namespace for XML Form
     * grammars is defined as http://www.w3.org/2001/06/grammar.
     *
     * It is recommended that the grammar element also indicate
     * the location of the grammar schema (see Appendix C) via the
     * xsi:schemaLocation attribute from [SCHEMA1]. Although such
     * indication is not required, to encourage it this document
     * provides such indication on all of the examples:
     *
     * The example contains all legal elements and attributes. The
     * comments are also valid, but are just there, to state the
     * requirement level.
     *
     * Example:
     * <code>
     * &lt;!-- required xml prolog --&gt;<br>
     * <br>
     * &lt;?xml version="1.0" encoding="ISO-8859-1"?&gt;<br>
     * <br>
     * &lt;!-- optional DOCTYPE --&gt;<br>
     * <br>
     * &lt;!DOCTYPE grammar PUBLIC "-//W3C//DTD GRAMMAR 1.0//EN"
     * "http://www.w3.org/TR/speech-grammar/grammar.dtd"&gt;<br>
     * <br>
     * &lt;!-- required grammar element --&gt;<br>
     * <br>
     * &lt;grammar<br>
     * <br>
     * &lt;!-- required attribute --&gt;<br>
     * <br>
     * version="1.0"<br>
     * <br>
     * &gt;!-- optional root rule --&gt;<br>
     * <br>
     * mode="voice"<br>
     * <br>
     * &lt;!-- required if mode provided --&gt;<br>
     * <br>
     * xml:lang="fr-CA"<br>
     * <br>
     * &lt;!-- optional root rule --&gt;<br>
     * <br>
     * root="QuebecCities"<br>
     * <br>
     * &lt;!-- required attribute --&gt;<br>
     * <br>
     * xmlns="http://www.w3.org/2001/06/grammar"<br>
     * <br>
     * &gt;!-- optional root rule --&gt;<br>
     * <br>
     * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"<br>
     * <br>
     * &lt;!-- optional root rule --&gt;<br>
     * <br>
     * xsi:schemaLocation="http://www.w3.org/2001/06/grammar
     * http://www.w3.org/TR/speech-grammar/grammar.xsd"<br>
     * <br>
     * &lt;!-- optional attribute --&gt;<br>
     * <br>
     * xml:base="http://www.example.com/another-base-file-path"&gt;<br>
     * </code>
     */
    public String identify(final String grammar) {
        /* make sure grammar is not null nor empty */
        if (grammar == null || grammar.equals("")) {
            LOGGER.warn("Grammar is null or empty");

            return null;
        }

        try {
            /* create a VoiceXmlDocument from the string */
            final VoiceXmlDocument document =
                    new VoiceXmlDocument(new InputSource(
                            new StringReader(grammar)));

            /* no exception, this must be xml a xml element */
            /* Lets test, if it is srgs+xml */
            final VoiceXmlNode node = (VoiceXmlNode) document.getFirstChild();
            if (node instanceof Grammar) {
                /* ok, it seems to be a srgs xml grammar */
                Grammar gr = (Grammar) node;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Found a SRGS XML grammar header.");
                }

                /* Is there a standard compliant version attribute? */
                if (!versionIsCompliant(gr)) {
                    return null;
                }

                /* Is there a standard compliant mode attribute? */
                if (!modeIsCompliant(gr)) {
                    return null;
                }
            }
        } catch (ParserConfigurationException e) {
            LOGGER.warn(e.getMessage());
            return null;
        } catch (SAXException e) {
            LOGGER.warn(e.getMessage());
            return null;
        } catch (IOException e) {
            LOGGER.warn(e.getMessage());
            return null;
        }

        return TYPE;
    }

    /**
     * This method checks the standard compliance of the version
     * attribute within the grammar element.
     *
     * @param grammar
     *        the grammar node to be checked
     * @return true, if version attribute of provided node is
     *         compliant, else false.
     */
    private boolean versionIsCompliant(final Grammar grammar) {
        /* Is there any version attribute? */
        final String version = grammar.getVersion();

        if (version == null) {
            LOGGER.debug("The version attribute has to be provided.");
            return false;
        }

        /*
         * Yes, there is a version attribut, what's its value?
         */

        return "1.0".equals(version);
    }

    /**
     * This method checks the standard compliance of the mode
     * attribute within the grammar element.
     *
     * @param grammar
     *        the node to be tested
     * @return true, if mode attribute of provided node is compliant,
     *         else false.
     */
    private boolean modeIsCompliant(final Grammar grammar) {
        /* Is there an optional mode attribut? */
        if ("voice".equalsIgnoreCase(grammar.getMode())) {
            /* yes, there is an optional mode attribute */
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("mode attribute provided");
            }

            /* then, there has to be a xml:lang attribute */
            if (grammar.getAttribute(Grammar.ATTRIBUTE_XML_LANG) == null) {
                LOGGER.debug("If mode is provided and equals voice, "
                             + "xml:lang has to be provided too!");
                return false;
            } else {
                LOGGER.debug("xml:lang attribute provided, thx.");
                /* does it provide correct language code? */
                Locale lang = new Locale(grammar.getXmlLang());
                LOGGER.info("locale is " + lang.getLanguage());
            }
        }

        /* no, there is no optional mode attribute */
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("no mode attribute provided, that's ok.");
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedType() {
        return TYPE;
    }
}
