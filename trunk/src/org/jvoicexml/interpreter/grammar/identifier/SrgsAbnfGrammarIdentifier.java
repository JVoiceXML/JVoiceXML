/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date $
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2007 JVoiceXML group - http://jvoicexml.sourceforge.net
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

import java.util.StringTokenizer;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.GrammarIdentifier;
import org.jvoicexml.logging.Logger;
import org.jvoicexml.logging.LoggerFactory;
import org.jvoicexml.xml.srgs.GrammarType;

/**
 * This class implements the GrammarIdentifier interface. An instance
 * of this class is able to identify a SRGS grammar with ABNF format.
 * The mime type of the accepted grammar is <code>application/srgs</code>.
 *
 * @author Christoph Buente
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2005-2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net">http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class SrgsAbnfGrammarIdentifier
        implements GrammarIdentifier {
    /** Logger for this class. */
    private static final Logger LOGGER =
            LoggerFactory.getLogger(SrgsAbnfGrammarIdentifier.class);
    /**
     * {@inheritDoc}
     *
     * These are the rules for a valid ABDF Header:
     *
     * <p>
     * The ABNF self-identifying header must be present in any
     * legal stand-alone ABNF Form grammar document.
     * </p>
     *
     * <p>
     * The first character of an ABNF document must be the "#"
     * symbol (x23) unless preceded by an optional XML 1.0 byte
     * order mark [XML Paragraph 4.3.3]. The ABNF byte order mark
     * follows the XML definition and requirements. For example,
     * documents encoded in UTF-16 must begin with the byte order
     * mark.
     * </p>
     *
     * <p>
     * The optional byte order mark and required "#" symbol must
     * be followed immediately by the exact string "ABNF" (x41 x42
     * x4d x46) or the appropriate equivalent for the document's
     * encoding (e.g. for UTF-16 little-endian: x23 x00 x41 x00
     * x42 x00 x4d x00 x46 x00). If the byte order mark is absent
     * on a grammar encoded in UTF-16 then the grammar processor
     * should perform auto-detection of character encoding in a
     * manner analogous to auto-detection of character encoding in
     * XML [XML Appendix F].
     * </p>
     *
     * <p>
     * Next follows a single space character (x20) and the
     * required version number which is "1.0" for this
     * specification (x31 x2e x30).
     * </p>
     *
     * <p>
     * Next follows an optional character encoding. Section 4.4
     * defines character encodings in more detail. If present,
     * there must be a single space character (x20) between the
     * version number and the character encoding.
     * </p>
     *
     * <p>
     * The self-identifying header is finalized with a semicolon
     * (x3b) followed immediately by a newline. The semicolon must
     * be the first character following the version number or the
     * character encoding if is present.
     * </p>
     */
    public GrammarType identify(final GrammarDocument grammar) {
        /* make sure grammar is not null nor empty */
        if (grammar == null) {
            LOGGER.debug("Grammar is null or empty");

            return null;
        }

        /*
         * cut grammar in pieces. Delimiter is ; followed by a
         * newline immediately
         */
        final String document = grammar.getDocument();
        final StringTokenizer tok = new StringTokenizer(document, ";");
        if (!tok.hasMoreTokens()) {
            return null;
        }

        final String header = tok.nextToken();
        if (header.startsWith("#ABNF 1.0")) {
            return GrammarType.SRGS_ABNF;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public GrammarType getSupportedType() {
        return GrammarType.SRGS_ABNF;
    }

}
