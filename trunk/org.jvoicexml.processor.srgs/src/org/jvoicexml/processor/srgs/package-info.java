/**
 * Classes that facilitate parsing and validation of grammars..
 *
 * <p>
 * {@link org.jvoicexml.implementation.GrammarImplementation}s can be validated in two phases:
 * <ol>
 * <li>
 * Parse the {@link org.jvoicexml.implementation.GrammarImplementation} using the
 * {@link org.jvoicexml.processor.srgs.SrgsXmlGrammarParser} to retrieve an
 * implementation independent representation of the grammar.
 * </li>
 * <li>
 * Use the {@link org.jvoicexml.processor.srgs.GrammarChecker} to
 * validate the grammar. Note that this implementation is not thread-safe.
 * </li>
 * </ol>
 * </p>
  */

package org.jvoicexml.processor.srgs;
