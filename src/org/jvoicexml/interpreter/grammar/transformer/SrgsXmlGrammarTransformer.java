/*
 * File:    $RCSfile: SrgsXmlGrammarTransformer.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2006/07/17 14:14:31 $
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
package org.jvoicexml.interpreter.grammar.transformer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;

import javax.speech.recognition.RuleGrammar;

import org.apache.log4j.Logger;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.implementation.UserInput;
import org.jvoicexml.interpreter.grammar.GrammarTransformer;
import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.Rule;
import org.jvoicexml.xml.srgs.SrgsXmlDocument;
import org.xml.sax.InputSource;

/**
 * This class implements the GrammarTransformer interface. An instance
 * of this class is able to transform a SRGS grammar with XML format
 * into RuleGrammar instance. The mime type of the accepted grammar is
 * application/srgs+xml.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * <p>
 * Copyright &copy; 2005 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsXmlGrammarTransformer
        implements GrammarTransformer {

    /**
     * Representation of the supported type.
     */
    private static final String TYPE = "application/srgs+xml";

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER =
            Logger.getLogger(SrgsXmlGrammarTransformer.class);

    /**
     * Standard constructor to instantiate as much
     * <code>GrammarTransformer</code> as you need.
     */
    public SrgsXmlGrammarTransformer() {
    }

    /**
     * {@inheritDoc}
     */
    public String getSupportedType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public RuleGrammar createGrammar(final UserInput input,
                                     final String grammar, final String type)
            throws NoresourceError,
            UnsupportedFormatError, BadFetchError {
        /* First make sure, the type is supported */
        if (!TYPE.equals(type)) {
            throw new UnsupportedFormatError();
        }

        final RuleGrammar ruleGrammar = input.newGrammar("testgrammar");

        return transform(ruleGrammar, grammar);
    }

    /**
     * Transform the given grammar into the given empty
     * <code>RuleGrammar</code> object.
     *
     * @param ruleGrammar
     *        Empty <code>RuleGrammar</code>
     * @param grammar
     *        The grammar in xml format.
     * @return RuleGrammar The transformed <code>RuleGrammar</code>.
     * @exception BadFetchError
     *            Error parsing the grammar.
     */
    private RuleGrammar transform(final RuleGrammar ruleGrammar,
                                  final String grammar)
            throws BadFetchError {

        // prepare a reader to read in the grammar string
        final StringReader reader = new StringReader(grammar);

        // create a xml input source from the grammar
        final InputSource input = new InputSource(reader);

        final SrgsXmlDocument doc;
        try {
            doc = new SrgsXmlDocument(input);
        } catch (IOException ioe) {
            throw new BadFetchError(ioe);
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new BadFetchError(pce);
        } catch (org.xml.sax.SAXException se) {
            throw new BadFetchError(se);
        }

        final Grammar grammarNode = doc.getGrammar();
        final Collection<Rule> rules = grammarNode.getChildNodes(Rule.class);

        // transform each rule into a proper JSGF Rule
        for (Rule rule : rules) {
            transformRule(rule);
        }

        return ruleGrammar;
    }

    /**
     * Transform the given rule.
     *
     * @param rule
     *        The rule to transform.
     *
     * @since 0.3
     *
     * @todo Implement this method.
     */
    private void transformRule(final Rule rule) {
        LOGGER.info("transforming rule: " + rule);
    }
}
