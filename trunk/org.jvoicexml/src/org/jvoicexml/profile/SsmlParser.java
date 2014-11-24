package org.jvoicexml.profile;

import javax.xml.parsers.ParserConfigurationException;

import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.xml.ssml.SsmlDocument;

/**
 * Parser to transform the contents of a outputting nodes into an SSML document.
 * 
 * @author Dirk Schnelle-Walka
 * @version $Revision: 4391 $
 * 
 * @since 0.5
 */
public interface SsmlParser {
    /**
     * Retrieves the parsed SSML document.
     * 
     * @return Parsed SSML document.
     * @exception ParserConfigurationException
     *                Error creating the SSML document.
     * @exception SemanticError
     *                Error evaluating a scripting expression.
     */
    SsmlDocument getDocument() throws ParserConfigurationException,
            SemanticError;

}
